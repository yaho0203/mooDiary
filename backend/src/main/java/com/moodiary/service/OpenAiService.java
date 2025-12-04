package com.moodiary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * OpenAI API 기본 URL
     * 
     * OpenAI API 서버의 기본 URL 주소입니다.
     * 
     * 기본값:
     * - https://api.openai.com/v1
     * - 프록시나 로컬 환경에서 변경 가능
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Value("${openai.api.url:https://api.openai.com/v1}")
    private String apiUrl;

    /**
     * 감정 분석 결과 데이터 클래스
     * 
     * OpenAI API를 통해 분석된 감정 정보를 담는 내부 클래스입니다.
     * 
     * 필드 설명:
     * - emotion: 감정 타입 (행복, 슬픔, 분노 등)
     * - score: 감정 강도 점수 (0~100)
     * - confidence: 분석 신뢰도 (0~100)
     * - keywords: 추출된 키워드 (쉼표로 구분)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmotionAnalysisResult {
        private String emotion;
        private Double score;
        private Double confidence;
        private String keywords;
    }

    /**
     * 텍스트 기반 감정 분석
     * 
     * OpenAI GPT-4 API를 사용하여 텍스트의 감정을 분석합니다.
     * 
     * 분석 과정:
     * 1. 텍스트를 OpenAI API 요청 형식으로 변환
     * 2. GPT-4 모델에 감정 분석 요청 전송
     * 3. API 응답을 파싱하여 감정 정보 추출
     * 4. 감정 점수 및 신뢰도 계산
     * 5. 키워드 추출
     * 
     * 프롬프트 예시:
     * "다음 텍스트의 감정을 분석해주세요: [텍스트]
     *  감정 타입: 행복, 슬픔, 분노, 평온, 우울, 기쁨, 불안, 화남, 만족, 실망 중에서 선택
     *  감정 점수: 0(매우 부정적) ~ 100(매우 긍정적)
     *  신뢰도: 0(낮음) ~ 100(높음)
     *  키워드: 주요 단어들을 쉼표로 구분하여 나열"
     * 
     * @param text 분석할 텍스트 내용
     * @return 감정 분석 결과 (EmotionAnalysisResult)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public EmotionAnalysisResult analyzeTextEmotion(String text) {
        log.info("=== 텍스트 감정 분석 시작 ===");
        log.info("텍스트 길이: {}", text.length());
        log.info("텍스트 내용: {}", text);

        try {
            log.info("=== 1단계: 프롬프트 생성 시작 ===");
            
            // OpenAI API 요청 데이터 구성
             String prompt = String.format("""
                 다음 텍스트의 감정을 정확하게 분석하여 JSON으로 응답하세요.
                 
                 ========================================
                 🚨 절대 규칙 (반드시 지켜야 함) 🚨
                 ========================================
                 
                 1. 슬픔 감정 키워드가 있으면 절대 평온(calm)이나 중립(neutral)을 선택하지 마세요!
                    - 키워드: "슬프다", "슬픔", "슬퍼", "눈물", "울었어", "우울", "그리움", "아쉬움", "서러움", "비참", "절망", "힘들다", "괴로워", "괴롭다", "아프다", "마음이 아프다", "가슴이 아프다", "슬프네", "슬퍼요", "슬퍼서", "슬퍼했어", "슬퍼함", "슬퍼하는", "슬퍼하는데", "슬퍼하는게", "슬퍼하는걸"
                    - 위 키워드가 하나라도 있으면 반드시 "sad" 또는 "depressed"를 선택하고 score는 0-30 사이로 설정하세요!
                    - 절대로 "calm"이나 "neutral"을 선택하지 마세요!
                 
                 2. 화남 감정 키워드가 있으면 절대 평온(calm)이나 중립(neutral)을 선택하지 마세요!
                    - 키워드: "화난다", "화가 난다", "분노", "짜증", "답답", "열받", "빡친다", "화났어", "화났다", "화났네", "화났는데", "화났어요", "화났습니다"
                    - 위 키워드가 하나라도 있으면 반드시 "angry" 또는 "frustrated"를 선택하고 score는 85-100 사이로 설정하세요!
                    - 절대로 "calm"이나 "neutral"을 선택하지 마세요!
                 
                 3. 기쁨/행복 감정 키워드가 있으면 절대 평온(calm)이나 중립(neutral)을 선택하지 마세요!
                    - 키워드: "기쁘다", "기쁨", "기뻐", "행복", "행복하다", "좋다", "좋아", "좋아요", "좋아해", "즐겁다", "즐거워", "신나", "신난다", "재밌다", "재미있어", "웃긴다", "웃겨", "웃음", "웃었어", "뿌듯", "뿌듯하다", "만족", "만족스럽다", "성취감", "기대된다", "설렌다", "설레", "환상적", "최고", "최고야", "완벽", "완벽해", "사랑", "사랑해", "고마워", "감사", "감사해", "고마워요", "고마웠어", "고마웠다", "행복해", "행복했어", "행복하다", "행복해요", "행복합니다", "기쁘네", "기쁘다", "기쁘네요", "기쁘다", "기쁘네", "기쁘다", "기쁘네요"
                    - 위 키워드가 하나라도 있으면 반드시 "happy", "joyful", 또는 "satisfied"를 선택하고 score는 55-80 사이로 설정하세요!
                    - "기쁘다", "행복", "좋다" 같은 명확한 긍정 표현이 있으면 최소 55점 이상으로 설정하세요!
                    - 절대로 "calm"이나 "neutral"을 선택하지 마세요!
                 
                 4. 평온(calm)이나 중립(neutral)은 오직 다음 경우에만 사용하세요:
                    - 감정 표현이 전혀 없는 순수한 사실 서술 (예: "오늘 날씨가 맑다", "점심을 먹었다")
                    - 감정 키워드가 전혀 없는 중립적인 내용
                    - 슬픔, 화남, 기쁨 등 어떤 감정 표현도 없을 때만 사용
                 
                 ========================================
                 
                 텍스트: %s
                 
                 위 텍스트를 분석하여 다음 JSON 형식으로만 응답하세요 (다른 설명 없이 JSON만):
                 {
                   "emotion": "감정명",
                   "score": 감정점수,
                   "confidence": 신뢰도,
                   "description": "텍스트의 감정과 내용에 대한 설명",
                   "keywords": "주요 키워드들을 쉼표로 구분"
                 }
                 
                 감정명 선택 가이드:
                 - sad (슬픔): "슬프다", "슬픔", "눈물", "울었어", "우울", "그리움", "아쉬움", "서러움", "비참", "절망", "힘들다", "괴로워", "아프다" 등의 표현이 있을 때 반드시 선택 (score: 0-30)
                 - depressed (우울): "우울", "침울", "무기력", "절망", "힘들다", "괴로워" 등의 표현이 있을 때 선택 (score: 0-30)
                 - angry (분노): "화난다", "화가 난다", "분노", "짜증", "답답", "열받", "빡친다" 등의 표현이 있을 때 반드시 선택 (score: 85-100)
                 - frustrated (화남): "좌절", "답답", "화남" 등의 표현이 있을 때 선택 (score: 85-100)
                 - disappointed (실망): "실망", "아쉬움" 등의 표현이 있을 때 선택 (score: 30-45)
                 - anxious (불안): "불안", "걱정", "불안감" 등의 표현이 있을 때 선택 (score: 30-45)
                 - happy (행복): "행복", "기쁘다", "좋다", "좋아", "즐겁다", "신나", "재밌다", "웃긴다", "뿌듯", "만족", "성취감", "기대된다", "설렌다", "고마워", "감사" 등의 긍정적 표현이 있을 때 반드시 선택 (score: 55-70)
                 - joyful (기쁨): "매우 기쁘다", "정말 좋다", "완벽", "최고", "환상적", "사랑", "사랑해", "완벽해" 등의 강한 긍정 표현이 있을 때 반드시 선택 (score: 70-85)
                 - satisfied (만족): "만족", "뿌듯", "성취감" 등의 표현이 있을 때 선택 (score: 55-65)
                 - calm (평온): 감정 표현이 전혀 없는 중립적인 내용일 때만 사용 (score: 50)
                 - neutral (중립): 정말로 감정이 없는 경우에만 사용 (거의 사용하지 않음, score: 50)
                 
                 감정점수(score) 규칙:
                 - 슬픔/우울 (sad, depressed): 0-30 (슬플수록 낮은 점수, "슬프다"는 표현이 있으면 최대 30점 이하)
                 - 화남/분노 (angry, frustrated): 85-100 (화가 날수록 높은 점수, "화난다"는 표현이 있으면 최소 85점 이상)
                 - 행복/기쁨 (happy, joyful): 55-85 ("기쁘다", "행복", "좋다" 같은 명확한 긍정 표현이 있으면 최소 55점 이상, 강한 긍정 표현은 70점 이상)
                 - 만족 (satisfied): 55-65
                 - 평온 (calm): 50 (감정 표현이 전혀 없을 때만)
                 - 중립 (neutral): 50 (거의 사용하지 않음)
                 - 기타 부정적 감정: 30-45
                 - 기타 긍정적 감정: 55-75
                 
                 신뢰도(confidence)는 0(낮음)부터 100(높음)까지의 숫자로 표현하세요.
                 
                 ========================================
                 정확한 예시 (반드시 이렇게 분석하세요):
                 ========================================
                 
                 예시 1: "오늘 정말 슬펐어"
                 → {"emotion": "sad", "score": 15, "confidence": 95, "description": "슬픔 감정이 명확히 드러남", "keywords": "슬프다"}
                 ❌ 잘못된 분석: {"emotion": "calm", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 2: "슬픈 일기"
                 → {"emotion": "sad", "score": 20, "confidence": 95, "description": "슬픔 감정이 명확히 드러남", "keywords": "슬픔"}
                 ❌ 잘못된 분석: {"emotion": "calm", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 3: "화난다"
                 → {"emotion": "angry", "score": 90, "confidence": 95, "description": "화남 감정이 명확히 드러남", "keywords": "화난다"}
                 ❌ 잘못된 분석: {"emotion": "calm", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 4: "오늘 날씨가 맑다"
                 → {"emotion": "calm", "score": 50, "confidence": 80, "description": "감정 표현이 없는 중립적인 내용", "keywords": "날씨"}
                 ✅ 올바른 분석: 감정 표현이 없으므로 calm 사용 가능
                 
                 예시 5: "기분이 정말 좋아"
                 → {"emotion": "happy", "score": 60, "confidence": 90, "description": "긍정적 감정이 명확히 드러남", "keywords": "기분 좋다"}
                 ❌ 잘못된 분석: {"emotion": "neutral", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 6: "오늘 정말 기뻐"
                 → {"emotion": "happy", "score": 65, "confidence": 95, "description": "기쁨 감정이 명확히 드러남", "keywords": "기쁘다"}
                 ❌ 잘못된 분석: {"emotion": "neutral", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 7: "행복해"
                 → {"emotion": "happy", "score": 60, "confidence": 95, "description": "행복 감정이 명확히 드러남", "keywords": "행복"}
                 ❌ 잘못된 분석: {"emotion": "neutral", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 예시 8: "오늘 최고야!"
                 → {"emotion": "joyful", "score": 75, "confidence": 95, "description": "강한 긍정 감정이 명확히 드러남", "keywords": "최고"}
                 ❌ 잘못된 분석: {"emotion": "neutral", "score": 50, ...} ← 절대 이렇게 하지 마세요!
                 
                 ========================================
                 
                 위 텍스트를 분석하여 JSON으로 응답하세요:
                 """, text);

            log.info("생성된 프롬프트: {}", prompt);
            log.info("프롬프트 길이: {}", prompt.length());
            log.info("=== 1단계: 프롬프트 생성 완료 ===");

            log.info("=== 2단계: OpenAI API 호출 시작 ===");
            log.info("사용할 모델: gpt-4o-mini");
            
            // OpenAI API 호출
            String response = callOpenAiApi(prompt, "gpt-4o-mini");
            
            log.info("=== 2단계: OpenAI API 호출 완료 ===");
            log.info("API 응답 길이: {}", response != null ? response.length() : "null");
            log.info("API 응답 내용: {}", response);
            
            // API 호출 실패 시 기본값 반환
            if (response == null || response.trim().isEmpty()) {
                log.warn("=== OpenAI API 응답이 비어있음 - 기본값 반환 ===");
                return getDefaultEmotionResult();
            }
            
            log.info("=== 3단계: 응답 파싱 시작 ===");
            
            // 응답 파싱 및 결과 반환
            EmotionAnalysisResult result = parseEmotionResponse(response);
            
            log.info("=== 3단계: 응답 파싱 완료 ===");
            log.info("파싱 결과: {}", result);
            
            return result;
            
        } catch (Exception e) {
            log.error("=== 텍스트 감정 분석 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 스택 트레이스:", e);
            return getDefaultEmotionResult();
        }
    }

    /**
     * 이미지 기반 감정 분석
     * 
     * OpenAI GPT-4 Vision API를 사용하여 이미지의 감정을 분석합니다.
     * 
     * 분석 과정:
     * 1. 이미지 URL을 OpenAI Vision API 요청 형식으로 변환
     * 2. GPT-4 Vision 모델에 감정 분석 요청 전송
     * 3. API 응답을 파싱하여 감정 정보 추출
     * 4. 감정 점수 및 신뢰도 계산
     * 5. 키워드 추출
     * 
     * 프롬프트 예시:
     * "이 이미지에서 보이는 사람의 표정을 분석하여 감정을 판단해주세요.
     *  감정 타입: 행복, 슬픔, 분노, 평온, 우울, 기쁨, 불안, 화남, 만족, 실망 중에서 선택
     *  감정 점수: 0(매우 부정적) ~ 100(매우 긍정적)
     *  신뢰도: 0(낮음) ~ 100(높음)
     *  키워드: 표정, 분위기 등을 설명하는 단어들을 쉼표로 구분하여 나열"
     * 
     * @param imageUrl 분석할 이미지의 URL
     * @return 감정 분석 결과 (EmotionAnalysisResult)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public EmotionAnalysisResult analyzeImageEmotion(String imageUrl) {
        log.info("=== 이미지 감정 분석 시작 ===");
        log.info("이미지 URL: {}", imageUrl);

        try {
            // OpenAI API 요청 데이터 구성
            String prompt = String.format("""
                다음 이미지에서 보이는 사람의 표정을 정확하게 분석하여 감정을 판단해주세요.
                얼굴 표정, 눈빛, 입꼴, 자세 등을 자세히 관찰하여 가장 적합한 감정을 선택하세요.
                중립(neutral)은 정말로 감정이 없는 경우에만 사용하세요.
                
                이미지 URL: %s
                
                다음 JSON 형식으로만 응답하세요 (다른 설명 없이 JSON만):
                {
                  "emotion": "감정명",
                  "score": 감정점수,
                  "confidence": 신뢰도,
                  "description": "표정과 분위기에 대한 설명",
                  "keywords": "주요 키워드들을 쉼표로 구분"
                }
                
                감정명은 다음 중 하나를 선택하세요 (중립은 최후의 수단으로만 사용):
                - happy (행복): 미소나 밝은 표정
                - sad (슬픔): 슬프고 우울한 표정
                - angry (분노): 화가 나고 분노한 표정
                - calm (평온): 차분하고 평화로운 표정
                - depressed (우울): 우울하고 침울한 표정
                - joyful (기쁨): 매우 기쁘고 행복한 표정
                - anxious (불안): 불안하고 걱정되는 표정
                - frustrated (화남): 좌절하고 답답한 표정
                - satisfied (만족): 만족스럽고 뿌듯한 표정
                - disappointed (실망): 실망하고 아쉬운 표정
                - neutral (중립): 정말로 감정이 없는 경우에만 사용
                
                감정점수(score)는 감정 온도로 직접 표현하세요:
                - 슬픔/우울 (sad, depressed): 34-36.5도 (슬플수록 낮은 온도, 최저 34도)
                - 평온/기분 좋음 (calm, happy, joyful, satisfied): 36.5도 (항상 36.5도 고정)
                - 화남/분노 (angry, frustrated): 36.5-40도 (화가 날수록 높은 온도, 분노 단계에 따라 최대 40도까지)
                - 기타 부정적 감정: 34-36.5도
                - 기타 긍정적 감정: 36.5도
                
                중요:
                - 슬픔은 34도에 가까울수록 더 슬픈 상태입니다.
                - 화남은 40도에 가까울수록 더 화가 난 상태입니다.
                - 평온/기분 좋음은 항상 정확히 36.5도로 설정하세요.
                
                신뢰도(confidence)는 0(낮음)부터 100(높음)까지의 숫자로 표현하세요.
                """, imageUrl);

            log.info("=== OpenAI Vision API 호출 시작 ===");
            
            // 로컬 파일을 Base64로 변환
            String base64Image = convertImageToBase64(imageUrl);
            log.info("Base64 이미지 길이: {}", base64Image.length());
            
            // OpenAI Vision API 호출 (Base64 사용)
            String response = callOpenAiVisionApiWithBase64(prompt, base64Image);
            log.info("=== OpenAI Vision API 호출 완료 ===");
            
            // 응답 파싱 및 결과 반환
            EmotionAnalysisResult result = parseEmotionResponse(response);
            log.info("=== 이미지 감정 분석 완료 ===");
            log.info("분석 결과: {}", result);
            
            return result;
            
        } catch (Exception e) {
            log.error("=== 이미지 감정 분석 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 toString: {}", e.toString());
            log.error("예외 스택 트레이스:", e);
            
            // OpenAI 거부 응답인 경우 null 반환 (텍스트 분석 결과를 사용하도록)
            String errorMessage = e.getMessage();
            String exceptionString = e.toString();
            
            // 예외 메시지나 toString에 거부 응답이 포함되어 있는지 확인
            boolean isRefusal = false;
            if (errorMessage != null) {
                String lowerMessage = errorMessage.toLowerCase();
                isRefusal = lowerMessage.contains("openai api가 요청을 거부했습니다") ||
                           lowerMessage.contains("i'm sorry") ||
                           lowerMessage.contains("can't assist") ||
                           lowerMessage.contains("can't help");
            }
            
            if (!isRefusal && exceptionString != null) {
                String lowerString = exceptionString.toLowerCase();
                isRefusal = lowerString.contains("i'm sorry") ||
                           lowerString.contains("can't assist") ||
                           lowerString.contains("can't help");
            }
            
            // RuntimeException이고 원인 예외가 있는 경우 원인 예외도 확인
            if (!isRefusal && e instanceof RuntimeException && e.getCause() != null) {
                String causeMessage = e.getCause().getMessage();
                if (causeMessage != null) {
                    String lowerCause = causeMessage.toLowerCase();
                    isRefusal = lowerCause.contains("openai api가 요청을 거부했습니다") ||
                               lowerCause.contains("i'm sorry") ||
                               lowerCause.contains("can't assist") ||
                               lowerCause.contains("can't help");
                }
            }
            
            if (isRefusal) {
                log.warn("=== 이미지 분석이 거부되었습니다. 텍스트 분석 결과를 사용합니다. ===");
                return null;
            }
            
            // 기타 예외는 예외를 다시 던져서 DiaryService에서 처리하도록 함
            log.error("이미지 분석 중 예상치 못한 오류 발생. 예외를 다시 던집니다. (예외: {})", errorMessage);
            throw e;
        }
    }

    /**
     * 텍스트와 이미지 통합 감정 분석
     * 
     * OpenAI GPT-4 API를 사용하여 텍스트와 이미지를 종합적으로 분석하여
     * 통합된 감정 결과를 도출합니다.
     * 
     * 통합 분석 방식:
     * 1. 텍스트와 이미지를 모두 제공하여 종합적 분석
     * 2. 텍스트의 문맥과 이미지의 시각적 정보를 결합
     * 3. 일관성 있는 최종 감정 결과 도출
     * 4. 가중 평균을 통한 감정 점수 계산
     * 
     * 프롬프트 예시:
     * "다음 텍스트와 이미지를 종합적으로 분석하여 감정을 판단해주세요.
     *  텍스트: [텍스트 내용]
     *  이미지: [이미지 설명]
     *  
     *  두 정보를 종합하여 일관성 있는 감정을 분석해주세요.
     *  감정 타입: 행복, 슬픔, 분노, 평온, 우울, 기쁨, 불안, 화남, 만족, 실망 중에서 선택
     *  감정 점수: 0(매우 부정적) ~ 100(매우 긍정적)
     *  신뢰도: 0(낮음) ~ 100(높음)
     *  키워드: 텍스트와 이미지에서 추출한 주요 단어들을 쉼표로 구분하여 나열"
     * 
     * @param text 분석할 텍스트 내용
     * @param imageUrl 분석할 이미지의 URL
     * @return 통합 감정 분석 결과 (EmotionAnalysisResult)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    public EmotionAnalysisResult analyzeIntegratedEmotion(String text, String imageUrl) {
        log.info("통합 감정 분석 시작 - 텍스트 길이: {}, 이미지 URL: {}", text.length(), imageUrl);

        try {
            // OpenAI Vision API 요청 데이터 구성 (텍스트와 이미지 모두 포함)
            String prompt = String.format("""
                다음 텍스트와 이미지를 종합하여 감정을 정확하게 분석해주세요.
                텍스트의 내용과 이미지의 표정을 자세히 분석하여 가장 적합한 감정을 선택하세요.
                중립(neutral)은 정말로 감정이 없는 경우에만 사용하세요.
                
                텍스트: %s
                
                다음 JSON 형식으로만 응답하세요 (다른 설명 없이 JSON만):
                {
                  "emotion": "감정명",
                  "score": 감정점수,
                  "confidence": 신뢰도,
                  "description": "텍스트와 이미지를 종합한 감정 분석 설명",
                  "keywords": "주요 키워드들을 쉼표로 구분"
                }
                
                감정명은 다음 중 하나를 선택하세요 (중립은 최후의 수단으로만 사용):
                - happy (행복): 긍정적이고 즐거운 감정
                - sad (슬픔): 슬프고 우울한 감정
                - angry (분노): 화가 나고 분노한 감정
                - calm (평온): 차분하고 평화로운 감정
                - depressed (우울): 우울하고 침울한 감정
                - joyful (기쁨): 매우 기쁘고 행복한 감정
                - anxious (불안): 불안하고 걱정되는 감정
                - frustrated (화남): 좌절하고 답답한 감정
                - satisfied (만족): 만족스럽고 뿌듯한 감정
                - disappointed (실망): 실망하고 아쉬운 감정
                - neutral (중립): 정말로 감정이 없는 경우에만 사용
                
                감정점수(score)는 감정 온도로 직접 표현하세요:
                - 슬픔/우울 (sad, depressed): 34-36.5도 (슬플수록 낮은 온도, 최저 34도)
                - 평온/기분 좋음 (calm, happy, joyful, satisfied): 36.5도 (항상 36.5도 고정)
                - 화남/분노 (angry, frustrated): 36.5-40도 (화가 날수록 높은 온도, 분노 단계에 따라 최대 40도까지)
                - 기타 부정적 감정: 34-36.5도
                - 기타 긍정적 감정: 36.5도
                
                중요:
                - 슬픔은 34도에 가까울수록 더 슬픈 상태입니다.
                - 화남은 40도에 가까울수록 더 화가 난 상태입니다.
                - 평온/기분 좋음은 항상 정확히 36.5도로 설정하세요.
                
                신뢰도(confidence)는 0(낮음)부터 100(높음)까지의 숫자로 표현하세요.
                """, text);

            // 이미지를 Base64로 변환하여 Vision API 사용
            String base64Image = convertImageToBase64(imageUrl);
            String response = callOpenAiVisionApiWithBase64(prompt, base64Image);
            
            // 응답 파싱 및 결과 반환
            return parseEmotionResponse(response);
            
        } catch (Exception e) {
            log.error("=== 통합 감정 분석 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 toString: {}", e.toString());
            log.error("예외 스택 트레이스:", e);
            
            // OpenAI 거부 응답인 경우 null 반환 (텍스트 분석 결과를 사용하도록)
            String errorMessage = e.getMessage();
            String exceptionString = e.toString();
            
            // 예외 메시지나 toString에 거부 응답이 포함되어 있는지 확인
            boolean isRefusal = false;
            if (errorMessage != null) {
                String lowerMessage = errorMessage.toLowerCase();
                isRefusal = lowerMessage.contains("openai api가 요청을 거부했습니다") ||
                           lowerMessage.contains("i'm sorry") ||
                           lowerMessage.contains("can't assist") ||
                           lowerMessage.contains("can't help");
            }
            
            if (!isRefusal && exceptionString != null) {
                String lowerString = exceptionString.toLowerCase();
                isRefusal = lowerString.contains("i'm sorry") ||
                           lowerString.contains("can't assist") ||
                           lowerString.contains("can't help");
            }
            
            // RuntimeException이고 원인 예외가 있는 경우 원인 예외도 확인
            if (!isRefusal && e instanceof RuntimeException && e.getCause() != null) {
                String causeMessage = e.getCause().getMessage();
                if (causeMessage != null) {
                    String lowerCause = causeMessage.toLowerCase();
                    isRefusal = lowerCause.contains("openai api가 요청을 거부했습니다") ||
                               lowerCause.contains("i'm sorry") ||
                               lowerCause.contains("can't assist") ||
                               lowerCause.contains("can't help");
                }
            }
            
            if (isRefusal) {
                log.warn("=== 통합 분석이 거부되었습니다. 텍스트 분석 결과를 사용합니다. ===");
                return null;
            }
            
            // 기타 예외는 예외를 다시 던져서 DiaryService에서 처리하도록 함
            log.error("통합 분석 중 예상치 못한 오류 발생. 예외를 다시 던집니다. (예외: {})", errorMessage);
            throw e;
        }
    }

    /**
     * OpenAI API 호출 (텍스트 전용)
     * 
     * OpenAI GPT-4 API에 텍스트 기반 요청을 전송합니다.
     * 
     * API 엔드포인트:
     * - POST /v1/chat/completions
     * - 모델: gpt-4o-mini
     * - 최대 토큰: 1000
     * - 온도: 0.3 (일관성 있는 응답)
     * 
     * 요청 형식:
     * ```json
     * {
     *   "model": "gpt-4o-mini",
     *   "messages": [
     *     {"role": "user", "content": "프롬프트 내용"}
     *   ],
     *   "max_tokens": 1000,
     *   "temperature": 0.3
     * }
     * ```
     * 
     * @param prompt 분석 요청 프롬프트
     * @param model 사용할 OpenAI 모델명
     * @return API 응답 문자열
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private String callOpenAiApi(String prompt, String model) {
        log.info("=== callOpenAiApi 메서드 시작 ===");
        log.info("입력 프롬프트: {}", prompt);
        log.info("입력 모델: {}", model);
        log.info("API URL: {}", apiUrl);
        log.info("API 키 길이: {}", apiKey != null ? apiKey.length() : "null");
        log.info("API 키 앞 10자: {}", apiKey != null ? apiKey.substring(0, Math.min(10, apiKey.length())) : "null");
        
        // API 요청 데이터 구성 (Map + Jackson 직렬화 사용)
        log.info("=== API 요청 데이터 구성 시작 ===");
        
        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", List.of(Map.of("role", "user", "content", prompt)),
            "max_tokens", 1000,
            "temperature", 0.3
        );

        log.info("=== API 요청 데이터 구성 완료 ===");
        log.info("요청 본문: {}", requestBody);
        
        log.info("=== WebClient API 호출 시작 ===");
        log.info("호출할 URI: {}", apiUrl + "/chat/completions");
        
        try {
            // WebClient를 통한 API 호출
            String response = webClient.post()
                    .uri(apiUrl + "/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("=== WebClient API 호출 성공 ===");
            log.info("응답 길이: {}", response != null ? response.length() : "null");
            log.info("응답 내용 (전체): {}", response);
            
            // 응답이 있으면 구조 확인
            if (response != null && !response.trim().isEmpty()) {
                try {
                    JsonNode responseNode = objectMapper.readTree(response);
                    log.info("=== OpenAI API 응답 구조 분석 ===");
                    log.info("응답 JSON 파싱 성공");
                    log.info("choices 배열 존재: {}", responseNode.has("choices"));
                    if (responseNode.has("choices") && responseNode.path("choices").isArray()) {
                        log.info("choices 배열 크기: {}", responseNode.path("choices").size());
                        if (responseNode.path("choices").size() > 0) {
                            JsonNode firstChoice = responseNode.path("choices").path(0);
                            log.info("첫 번째 choice 존재: {}", !firstChoice.isMissingNode());
                            if (firstChoice.has("message")) {
                                JsonNode message = firstChoice.path("message");
                                log.info("message 존재: {}", !message.isMissingNode());
                                if (message.has("content")) {
                                    String content = message.path("content").asText();
                                    log.info("content 길이: {}", content.length());
                                    log.info("content 앞 500자: {}", content.length() > 500 ? content.substring(0, 500) : content);
                                }
                            }
                        }
                    }
                    log.info("에러 필드 존재: {}", responseNode.has("error"));
                    if (responseNode.has("error")) {
                        log.error("OpenAI API 에러: {}", responseNode.path("error").toPrettyString());
                    }
                } catch (Exception e) {
                    log.warn("응답 구조 분석 실패 (응답은 있음): {}", e.getMessage());
                }
            }
            
            return response;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized e) {
            log.error("=== OpenAI API 인증 실패 (401 Unauthorized) ===");
            log.error("API 키가 유효하지 않거나 만료되었습니다.");
            log.error("API 키 앞 10자: {}", apiKey != null && apiKey.length() >= 10 ? apiKey.substring(0, 10) : "없음");
            log.error("응답 본문: {}", e.getResponseBodyAsString());
            log.error("응답 헤더: {}", e.getHeaders());
            log.error("해결 방법:");
            log.error("1. OpenAI 웹사이트(https://platform.openai.com/api-keys)에서 API 키 확인");
            log.error("2. API 키가 만료되었거나 삭제되었는지 확인");
            log.error("3. API 키에 충분한 크레딧이 있는지 확인");
            log.error("4. application.yml의 openai.api.key 값을 확인하고 업데이트");
            return null;
        } catch (Exception e) {
            log.error("=== WebClient API 호출 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            
            // API 키 확인
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.error("=== OpenAI API 키가 설정되지 않음 ===");
                log.error("application.yml 또는 환경 변수에서 openai.api.key를 확인하세요.");
            } else {
                log.error("API 키는 설정되어 있습니다. 길이: {}", apiKey.length());
            }
            
            // null 반환하여 상위에서 기본값 처리하도록 함
            return null;
        }
    }

    /**
     * OpenAI Vision API 호출 (이미지 포함)
     * 
     * OpenAI GPT-4 Vision API에 이미지가 포함된 요청을 전송합니다.
     * 
     * API 엔드포인트:
     * - POST /v1/chat/completions
     * - 모델: gpt-4-vision-preview
     * - 최대 토큰: 1000
     * - 온도: 0.3 (일관성 있는 응답)
     * 
     * 요청 형식:
     * ```json
     * {
     *   "model": "gpt-4-vision-preview",
     *   "messages": [
     *     {
     *       "role": "user",
     *       "content": [
     *         {"type": "text", "text": "프롬프트 내용"},
     *         {"type": "image_url", "image_url": {"url": "이미지URL"}}
     *       ]
     *     }
     *   ],
     *   "max_tokens": 1000,
     *   "temperature": 0.3
     * }
     * ```
     * 
     * @param prompt 분석 요청 프롬프트
     * @param imageUrl 분석할 이미지 URL
     * @return API 응답 문자열
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private String callOpenAiVisionApi(String prompt, String imageUrl) {
        // API 요청 데이터 구성 (이미지 포함, Map + Jackson 직렬화 사용)
        Map<String, Object> requestBody = Map.of(
            "model", "gpt-4o",  // Vision API는 gpt-4o 사용
            "messages", List.of(Map.of(
                "role", "user",
                "content", List.of(
                    Map.of("type", "text", "text", prompt),
                    Map.of("type", "image_url", "image_url", Map.of("url", imageUrl))
                )
            )),
            "max_tokens", 1000,
            "temperature", 0.3
        );

        // WebClient를 통한 API 호출
        return webClient.post()
                .uri(apiUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * OpenAI API 응답 파싱
     * 
     * OpenAI API의 응답을 파싱하여 감정 분석 결과를 추출합니다.
     * 
     * 응답 형식:
     * ```json
     * {
     *   "choices": [
     *     {
     *       "message": {
     *         "content": "{\"emotion\": \"행복\", \"score\": 90, \"confidence\": 95, \"keywords\": \"오늘,정말,행복한,하루\"}"
     *       }
     *     }
     *   ]
     * }
     * ```
     * 
     * 파싱 과정:
     * 1. API 응답에서 choices[0].message.content 추출
     * 2. content를 JSON으로 파싱
     * 3. 감정 정보를 EmotionAnalysisResult 객체로 변환
     * 4. 파싱 실패 시 기본값 반환
     * 
     * @param response OpenAI API 응답 문자열
     * @return 파싱된 감정 분석 결과
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private EmotionAnalysisResult parseEmotionResponse(String response) {
        log.info("=== parseEmotionResponse 메서드 시작 ===");
        log.info("입력 응답: {}", response);
        log.info("응답 길이: {}", response != null ? response.length() : "null");
        
        // 응답이 null이거나 비어있으면 기본값 반환
        if (response == null || response.trim().isEmpty()) {
            log.warn("=== parseEmotionResponse: 응답이 null이거나 비어있음 - 기본값 반환 ===");
            return getDefaultEmotionResult();
        }
        
        try {
            log.info("=== 1단계: API 응답 JSON 파싱 시작 ===");
            
            // API 응답을 JsonNode로 파싱
            JsonNode responseNode = objectMapper.readTree(response);
            log.info("응답 JSON 파싱 성공");
            log.info("응답 구조: {}", responseNode.toPrettyString());
            
            log.info("=== 2단계: content 추출 시작 ===");
            
            // choices[0].message.content 추출
            String content = responseNode.path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();
            
            log.info("추출된 content: {}", content);
            log.info("content 길이: {}", content.length());
            
            // OpenAI의 거부 응답 감지 (콘텐츠 정책 위반 등)
            String contentLower = content.toLowerCase().trim();
            if (contentLower.contains("i'm sorry") || 
                contentLower.contains("i can't assist") || 
                contentLower.contains("cannot assist") ||
                contentLower.contains("unable to") ||
                contentLower.contains("content policy")) {
                log.warn("=== OpenAI 거부 응답 감지: {} ===", content);
                log.warn("이 응답은 OpenAI의 콘텐츠 정책 위반으로 인한 거부 응답입니다.");
                throw new RuntimeException("OpenAI API가 요청을 거부했습니다: " + content);
            }
            
            log.info("=== 3단계: 감정 정보 JSON 파싱 시작 ===");
            
            // content에서 마크다운 코드 블록 제거
            // OpenAI API가 응답을 ```json ... ``` 형태로 감싸서 반환하는 경우가 있음
            String cleanContent = content;
            if (content.startsWith("```json")) {
                cleanContent = content.substring(7); // "```json" 제거
            }
            if (cleanContent.endsWith("```")) {
                cleanContent = cleanContent.substring(0, cleanContent.length() - 3); // "```" 제거
            }
            cleanContent = cleanContent.trim();
            
            log.info("정리된 content: {}", cleanContent);
            
            // content를 JSON으로 파싱하여 감정 정보 추출
            JsonNode emotionNode = objectMapper.readTree(cleanContent);
            log.info("감정 정보 JSON 파싱 성공");
            log.info("감정 정보 구조: {}", emotionNode.toPrettyString());
            
            log.info("=== 4단계: EmotionAnalysisResult 생성 시작 ===");
            
            // OpenAI가 반환하는 실제 구조에 맞춰 필드 추출
            String emotion = "";
            double score = 0.0;
            double confidence = 0.0;
            String keywords = "";
            
            // 1. 새로운 형식 처리: {"emotion": "neutral", "score": 50, "confidence": 80, "description": "...", "keywords": "..."}
            if (emotionNode.has("emotion")) {
                emotion = emotionNode.path("emotion").asText();
                
                // OpenAI가 반환한 실제 점수와 신뢰도 사용 (없으면 기본값 사용)
                if (emotionNode.has("score") && !emotionNode.path("score").isNull()) {
                    double rawScore = emotionNode.path("score").asDouble();
                    String emotionLower = emotion.toLowerCase();
                    
                    // 감정 타입에 따라 다른 온도 변환 적용
                    if (rawScore >= 0 && rawScore <= 100) {
                        if (emotionLower.equals("angry") || emotionLower.equals("frustrated") || 
                            emotionLower.equals("분노") || emotionLower.equals("화남") || emotionLower.equals("좌절")) {
                            // 화남: 점수에 따라 38-40도 (화가 나면 온도가 올라감)
                            if (rawScore >= 80) {
                                score = 36.5 + ((rawScore - 80) / 20.0) * 3.5; // 80점=36.5도, 100점=40도
                            } else {
                                // 80점 미만이어도 화남 감정이면 최소 38도 이상으로 설정
                                score = Math.max(38.0, 36.5 + (rawScore / 80.0) * 0.0); // 최소 38도
                            }
                        } else if (emotionLower.equals("sad") || emotionLower.equals("depressed") || 
                                   emotionLower.equals("슬픔") || emotionLower.equals("우울")) {
                            // 슬픔: 0-30점 → 34-36.5도 (슬플수록 낮은 온도, 점수가 높을수록 더 슬픔)
                            // 0점=36.5도(덜 슬픔), 30점=34도(매우 슬픔)
                            if (rawScore <= 30) {
                                score = 36.5 - (rawScore / 30.0) * 2.5; // 0점=36.5도, 30점=34도
                            } else {
                                // 30점 초과면 34도로 설정 (매우 슬픔)
                                score = 34.0;
                            }
                        } else if (emotionLower.equals("calm") || emotionLower.equals("neutral") || 
                                   emotionLower.equals("happy") || emotionLower.equals("joyful") || 
                                   emotionLower.equals("satisfied") || emotionLower.equals("평온") || 
                                   emotionLower.equals("중립") || emotionLower.equals("행복") || 
                                   emotionLower.equals("기쁨") || emotionLower.equals("만족")) {
                            // 평온/기분 좋음: 36.5도 고정
                            score = 36.5;
                        } else {
                            // 기타 감정: 일반 변환 (30.5 + (score/100) * 12)
                            score = 30.5 + (rawScore / 100.0) * 12.0;
                        }
                    } else if (rawScore >= 30 && rawScore <= 42) {
                        // 이미 온도 형식인 경우 그대로 사용
                        score = rawScore;
                    } else {
                        // 범위를 벗어난 경우 기본값 사용
                        score = 36.5;
                    }
                } else {
                    // 점수가 없는 경우에만 감정 타입에 따른 기본 온도값 사용
                    switch (emotion.toLowerCase()) {
                        case "happy", "행복", "joyful", "기쁨", "satisfied", "만족":
                            score = 36.5; // 기분 좋음: 36.5도
                            break;
                        case "sad", "슬픔", "depressed", "우울":
                            score = 34.0; // 슬픔: 34도까지 내려감
                            break;
                        case "angry", "분노", "화남", "frustrated", "좌절":
                            score = 39.0; // 화남: 기본값 39도 (화가 나면 온도가 올라감)
                            break;
                        case "neutral", "중립", "calm", "평온":
                            score = 36.5; // 평온: 36.5도
                            break;
                        case "anxious", "불안":
                            score = 35.5; // 불안 감정 온도
                            break;
                        case "disappointed", "실망":
                            score = 35.0; // 실망 감정 온도
                            break;
                        default:
                            score = 36.5; // 기본 온도
                    }
                }
                
                // OpenAI가 반환한 실제 신뢰도 사용 (없으면 기본값 사용)
                if (emotionNode.has("confidence") && !emotionNode.path("confidence").isNull()) {
                    confidence = emotionNode.path("confidence").asDouble();
                } else {
                    confidence = 80.0; // 기본 신뢰도
                }
                
                // 키워드 추출 (keywords 필드가 있으면 사용, 없으면 description 사용)
                if (emotionNode.has("keywords") && !emotionNode.path("keywords").isNull()) {
                    keywords = emotionNode.path("keywords").asText();
                } else if (emotionNode.has("description") && !emotionNode.path("description").isNull()) {
                    keywords = emotionNode.path("description").asText();
                } else {
                    keywords = "";
                }
            }
            // 2. 기존 형식 처리: {"감정": {"행복": {"점수": 85, "신뢰도": 90}}}
            else if (emotionNode.has("감정")) {
                JsonNode emotionObj = emotionNode.path("감정");
                double maxScore = 0.0;
                String maxEmotion = "";
                double maxConfidence = 0.0;
                
                for (String emotionType : Arrays.asList("행복", "슬픔", "분노", "평온", "우울", "기쁨", "불안", "화남", "만족", "실망")) {
                    JsonNode emotionData = emotionObj.path(emotionType);
                    if (!emotionData.isMissingNode() && emotionData.isObject()) {
                        double emotionScore = emotionData.path("점수").asDouble();
                        double emotionConfidence = emotionData.path("신뢰도").asDouble();
                        
                        if (emotionScore > maxScore) {
                            maxScore = emotionScore;
                            maxEmotion = emotionType;
                            maxConfidence = emotionConfidence;
                        }
                    }
                }
                
                emotion = maxEmotion;
                score = maxScore;
                confidence = maxConfidence;
                keywords = emotion;
            }
            // 3. 기타 형식 처리: {"text_emotion": "중립", "image_emotion": "편안함", "overall_emotion": "긍정적"}
            else if (emotionNode.has("text_emotion") || emotionNode.has("image_emotion") || emotionNode.has("overall_emotion")) {
                emotion = emotionNode.path("overall_emotion").asText();
                if (emotion.isEmpty()) {
                    emotion = emotionNode.path("image_emotion").asText();
                }
                if (emotion.isEmpty()) {
                    emotion = emotionNode.path("text_emotion").asText();
                }
                score = 36.5; // 기본 온도
                confidence = 70.0; // 기본 신뢰도
                keywords = emotion;
            }
            // 4. 기본 필드 처리: {"emotion": "행복", "score": 85, "confidence": 90, "keywords": "..."}
            else {
                emotion = emotionNode.path("emotion").asText();
                double rawScore = emotionNode.path("score").asDouble();
                // 점수가 0-100 범위면 온도로 변환 (중립 50점 = 36.5도 기준)
                if (rawScore >= 0 && rawScore <= 100) {
                    // 0-100 점수를 30-42도로 매핑 (중립 50점 = 36.5도)
                    score = 30.5 + (rawScore / 100.0) * 12.0;
                } else if (rawScore >= 30 && rawScore <= 42) {
                    score = rawScore;
                } else {
                    score = 36.5;
                }
                confidence = emotionNode.path("confidence").asDouble();
                keywords = emotionNode.path("keywords").asText();
            }
            
            log.info("=== 추출된 감정 정보 ===");
            log.info("  - emotion: {} (원본: {})", emotion, emotionNode.has("emotion") ? emotionNode.path("emotion").asText() : "없음");
            log.info("  - score: {} (원본 rawScore: {})", score, emotionNode.has("score") && !emotionNode.path("score").isNull() ? emotionNode.path("score").asDouble() : "없음");
            log.info("  - confidence: {}", confidence);
            log.info("  - keywords: {}", keywords);
            log.info("  - 전체 JSON: {}", emotionNode.toPrettyString());
            
            // 감정이 비어있거나 기본값인 경우 경고
            if (emotion == null || emotion.trim().isEmpty()) {
                log.error("⚠️ 경고: 추출된 감정이 비어있습니다!");
                log.error("원본 응답: {}", response);
            }
            
            // 평온(calm)이 선택된 경우 원본 텍스트 확인
            if (emotion != null && (emotion.toLowerCase().equals("calm") || emotion.equals("평온"))) {
                log.warn("⚠️ 경고: 평온(calm) 감정이 선택되었습니다!");
                log.warn("이것이 정확한지 확인이 필요합니다. 원본 텍스트에 슬픔이나 화남 키워드가 있었는지 확인하세요.");
            }
            
            EmotionAnalysisResult result = EmotionAnalysisResult.builder()
                    .emotion(emotion)
                    .score(score)
                    .confidence(confidence)
                    .keywords(keywords)
                    .build();
            
            log.info("=== 4단계: EmotionAnalysisResult 생성 완료 ===");
            log.info("생성된 결과: {}", result);
            
            return result;
                    
        } catch (RuntimeException e) {
            // RuntimeException인 경우 (거부 응답 등) 예외를 다시 던져서 상위 메서드에서 처리하도록 함
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("OpenAI API가 요청을 거부했습니다")) {
                log.warn("parseEmotionResponse에서 거부 응답 감지. 예외를 다시 던집니다.");
                throw e; // 예외를 다시 던져서 analyzeImageEmotion/analyzeIntegratedEmotion에서 처리
            }
            // 기타 RuntimeException도 다시 던짐
            throw e;
        } catch (Exception e) {
            log.error("=== parseEmotionResponse 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 스택 트레이스:", e);
            
            // 기타 예외는 기본값 반환
            log.warn("parseEmotionResponse에서 예상치 못한 오류 발생. 기본값을 반환합니다.");
            return getDefaultEmotionResult();
        }
    }

    /**
     * 이미지 파일을 Base64로 변환
     * 
     * @param imageUrl 이미지 파일 경로
     * @return Base64 인코딩된 이미지 문자열
     */
    private String convertImageToBase64(String imageUrl) {
        try {
            // 상대 경로를 절대 경로로 변환
            String filePath = uploadDir + imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path path = Paths.get(filePath);
            
            // 파일 읽기
            byte[] imageBytes = Files.readAllBytes(path);
            
            // Base64 인코딩
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            
            // MIME 타입 결정
            String mimeType = Files.probeContentType(path);
            if (mimeType == null) {
                mimeType = "image/png"; // 기본값
            }
            
            // Data URL 형식으로 반환
            return "data:" + mimeType + ";base64," + base64;
            
        } catch (Exception e) {
            log.error("이미지 Base64 변환 실패: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * OpenAI Vision API 호출 (Base64 이미지 사용)
     * 
     * @param prompt 분석 요청 프롬프트
     * @param base64Image Base64 인코딩된 이미지
     * @return API 응답 문자열
     */
    private String callOpenAiVisionApiWithBase64(String prompt, String base64Image) {
        log.info("=== callOpenAiVisionApiWithBase64 메서드 시작 ===");
        log.info("Base64 이미지 길이: {}", base64Image != null ? base64Image.length() : "null");
        
        // API 요청 데이터 구성 (Base64 이미지 포함)
        Map<String, Object> requestBody = Map.of(
            "model", "gpt-4o",  // Vision API는 gpt-4o 사용
            "messages", List.of(Map.of(
                "role", "user",
                "content", List.of(
                    Map.of("type", "text", "text", prompt),
                    Map.of("type", "image_url", "image_url", Map.of("url", base64Image))
                )
            )),
            "max_tokens", 1000,
            "temperature", 0.3
        );

        try {
            // WebClient를 통한 API 호출
            String response = webClient.post()
                    .uri(apiUrl + "/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("=== Vision API 호출 성공 ===");
            log.info("응답 길이: {}", response != null ? response.length() : "null");
            return response;
            
        } catch (org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized e) {
            log.error("=== OpenAI Vision API 인증 실패 (401 Unauthorized) ===");
            log.error("API 키가 유효하지 않거나 만료되었습니다.");
            log.error("API 키 앞 10자: {}", apiKey != null && apiKey.length() >= 10 ? apiKey.substring(0, 10) : "없음");
            log.error("응답 본문: {}", e.getResponseBodyAsString());
            log.error("해결 방법:");
            log.error("1. OpenAI 웹사이트(https://platform.openai.com/api-keys)에서 API 키 확인");
            log.error("2. API 키가 만료되었거나 삭제되었는지 확인");
            log.error("3. API 키에 충분한 크레딧이 있는지 확인");
            log.error("4. application.yml의 openai.api.key 값을 확인하고 업데이트");
            return null;
        } catch (Exception e) {
            log.error("=== Vision API 호출 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 기본 감정 분석 결과 반환
     * 
     * OpenAI API 호출 실패 시 반환할 기본값을 제공합니다.
     * 
     * 기본값:
     * - 감정: "분석 실패"
     * - 점수: 0.0
     * - 신뢰도: 0.0
     * - 키워드: ""
     * 
     * 사용 사례:
     * - API 키가 유효하지 않은 경우
     * - 네트워크 오류가 발생한 경우
     * - API 응답 파싱에 실패한 경우
     * - 일기 저장은 정상 진행하되 감정 분석 결과만 기본값 사용
     * 
     * @return 기본 감정 분석 결과
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private EmotionAnalysisResult getDefaultEmotionResult() {
        return EmotionAnalysisResult.builder()
                .emotion("calm") // 기본값을 calm으로 설정 (36.5도에 해당)
                .score(36.5) // 기본 온도 36.5도
                .confidence(50.0) // 기본 신뢰도
                .keywords("")
                .build();
    }
}
