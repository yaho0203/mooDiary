package com.moodiary.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;


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
                 다음 텍스트의 감정을 분석하여 JSON으로 응답하세요:
                 
                 텍스트: %s
                 
                 다음 JSON 형식으로만 응답하세요:
                 {
                   "emotion": "감정명",
                   "score": 감정점수,
                   "confidence": 신뢰도,
                   "description": "텍스트의 감정과 내용에 대한 설명",
                   "keywords": "주요 키워드들을 쉼표로 구분"
                 }
                 
                 감정명은 다음 중 하나를 선택하세요:
                 - happy (행복)
                 - sad (슬픔)
                 - angry (분노)
                 - calm (평온)
                 - depressed (우울)
                 - joyful (기쁨)
                 - anxious (불안)
                 - frustrated (화남)
                 - satisfied (만족)
                 - disappointed (실망)
                 - neutral (중립)
                 
                 감정점수(score)는 0(매우 부정적)부터 100(매우 긍정적)까지의 숫자로 표현하세요.
                 신뢰도(confidence)는 0(낮음)부터 100(높음)까지의 숫자로 표현하세요.
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
                다음 이미지 URL의 이미지에서 보이는 사람의 표정을 분석하여 감정을 판단해주세요.
                
                이미지 URL: %s
                
                다음 JSON 형식으로만 응답하세요:
                {
                  "emotion": "감정명",
                  "score": 감정점수,
                  "confidence": 신뢰도,
                  "description": "표정과 분위기에 대한 설명",
                  "keywords": "주요 키워드들을 쉼표로 구분"
                }
                
                감정명은 다음 중 하나를 선택하세요:
                - happy (행복)
                - sad (슬픔)
                - angry (분노)
                - calm (평온)
                - depressed (우울)
                - joyful (기쁨)
                - anxious (불안)
                - frustrated (화남)
                - satisfied (만족)
                - disappointed (실망)
                - neutral (중립)
                
                감정점수(score)는 0(매우 부정적)부터 100(매우 긍정적)까지의 숫자로 표현하세요.
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
            log.error("예외 스택 트레이스:", e);
            return getDefaultEmotionResult();
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
                다음 텍스트와 이미지를 종합하여 감정을 분석해주세요.
                
                텍스트: %s
                
                다음 JSON 형식으로만 응답하세요:
                {
                  "emotion": "감정명",
                  "score": 감정점수,
                  "confidence": 신뢰도,
                  "description": "텍스트와 이미지를 종합한 감정 분석 설명",
                  "keywords": "주요 키워드들을 쉼표로 구분"
                }
                
                감정명은 다음 중 하나를 선택하세요:
                - happy (행복)
                - sad (슬픔)
                - angry (분노)
                - calm (평온)
                - depressed (우울)
                - joyful (기쁨)
                - anxious (불안)
                - frustrated (화남)
                - satisfied (만족)
                - disappointed (실망)
                - neutral (중립)
                
                감정점수(score)는 0(매우 부정적)부터 100(매우 긍정적)까지의 숫자로 표현하세요.
                신뢰도(confidence)는 0(낮음)부터 100(높음)까지의 숫자로 표현하세요.
                """, text);

            // 이미지를 Base64로 변환하여 Vision API 사용
            String base64Image = convertImageToBase64(imageUrl);
            String response = callOpenAiVisionApiWithBase64(prompt, base64Image);
            
            // 응답 파싱 및 결과 반환
            return parseEmotionResponse(response);
            
        } catch (Exception e) {
            log.error("통합 감정 분석 실패: {}", e.getMessage(), e);
            return getDefaultEmotionResult();
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
            log.info("응답 내용: {}", response);
            
            return response;
            
        } catch (Exception e) {
            log.error("=== WebClient API 호출 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 스택 트레이스:", e);
            throw e;
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
                    score = emotionNode.path("score").asDouble();
                } else {
                    // 점수가 없는 경우에만 감정 타입에 따른 기본값 사용
                    switch (emotion.toLowerCase()) {
                        case "happy", "행복", "joyful", "기쁨":
                            score = 85.0;
                            break;
                        case "sad", "슬픔", "depressed", "우울":
                            score = 20.0;
                            break;
                        case "angry", "분노", "화남", "frustrated", "좌절":
                            score = 15.0;
                            break;
                        case "neutral", "중립":
                            score = 50.0;
                            break;
                        case "anxious", "불안":
                            score = 30.0;
                            break;
                        case "satisfied", "만족":
                            score = 80.0;
                            break;
                        case "disappointed", "실망":
                            score = 25.0;
                            break;
                        case "calm", "평온":
                            score = 70.0;
                            break;
                        default:
                            score = 50.0;
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
                score = 50.0; // 기본 점수
                confidence = 70.0; // 기본 신뢰도
                keywords = emotion;
            }
            // 4. 기본 필드 처리: {"emotion": "행복", "score": 85, "confidence": 90, "keywords": "..."}
            else {
                emotion = emotionNode.path("emotion").asText();
                score = emotionNode.path("score").asDouble();
                confidence = emotionNode.path("confidence").asDouble();
                keywords = emotionNode.path("keywords").asText();
            }
            
            log.info("추출된 감정 정보:");
            log.info("  - emotion: {}", emotion);
            log.info("  - score: {}", score);
            log.info("  - confidence: {}", confidence);
            log.info("  - keywords: {}", keywords);
            
            EmotionAnalysisResult result = EmotionAnalysisResult.builder()
                    .emotion(emotion)
                    .score(score)
                    .confidence(confidence)
                    .keywords(keywords)
                    .build();
            
            log.info("=== 4단계: EmotionAnalysisResult 생성 완료 ===");
            log.info("생성된 결과: {}", result);
            
            return result;
                    
        } catch (Exception e) {
            log.error("=== parseEmotionResponse 실패 ===");
            log.error("예외 타입: {}", e.getClass().getSimpleName());
            log.error("예외 메시지: {}", e.getMessage());
            log.error("예외 스택 트레이스:", e);
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
                .emotion("분석 실패")
                .score(0.0)
                .confidence(0.0)
                .keywords("")
                .build();
    }
}
