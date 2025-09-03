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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

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
                 
                 감정 타입: 행복, 슬픔, 분노, 평온, 우울, 기쁨, 불안, 화남, 만족, 실망
                 점수: 0-100 (0=매우 부정적, 100=매우 긍정적)
                 신뢰도: 0-100 (0=낮음, 100=높음)
                 
                 JSON 형식으로만 응답하세요.
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
        log.info("이미지 감정 분석 시작 - 이미지 URL: {}", imageUrl);

        try {
                         // OpenAI Vision API 요청 데이터 구성
             String prompt = """
                 이 이미지의 표정을 분석하여 감정을 판단해주세요.
                 
                 JSON 응답만 반환하세요.
                 """;

            // OpenAI Vision API 호출
            String response = callOpenAiVisionApi(prompt, imageUrl);
            
            // 응답 파싱 및 결과 반환
            return parseEmotionResponse(response);
            
        } catch (Exception e) {
            log.error("이미지 감정 분석 실패: {}", e.getMessage(), e);
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
                         // OpenAI API 요청 데이터 구성 (텍스트와 이미지 모두 포함)
             String prompt = String.format("""
                 다음 텍스트와 이미지를 종합하여 감정을 분석해주세요.
                 
                 텍스트: %s
                 이미지: %s
                 
                 JSON 응답만 반환하세요.
                 """, text, imageUrl);

            // OpenAI API 호출
            String response = callOpenAiApi(prompt, "gpt-4o-mini");
            
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
            
            // 감정 타입과 점수를 감정 객체에서 추출
            // OpenAI API는 "감정" 객체 안에 각 감정별 점수와 신뢰도를 포함하여 반환
            JsonNode emotionObj = emotionNode.path("감정");
            if (!emotionObj.isMissingNode()) {
                // 가장 높은 점수의 감정을 선택 (0~100 범위)
                double maxScore = 0.0;
                String maxEmotion = "";
                double maxConfidence = 0.0;
                
                for (String emotionType : Arrays.asList("행복", "슬픔", "분노", "평온", "우울", "기쁨", "불안", "화남", "만족", "실망")) {
                    JsonNode emotionData = emotionObj.path(emotionType);
                    if (!emotionData.isMissingNode() && emotionData.isObject()) {
                        // 새로운 구조: {"점수": 85, "신뢰도": 90}
                        double emotionScore = emotionData.path("점수").asDouble();
                        double emotionConfidence = emotionData.path("신뢰도").asDouble();
                        
                        log.info("감정 타입: {}, 점수: {}, 신뢰도: {}", emotionType, emotionScore, emotionConfidence);
                        
                        if (emotionScore > maxScore) {
                            maxScore = emotionScore;
                            maxEmotion = emotionType;
                            maxConfidence = emotionConfidence;
                        }
                    } else {
                        // 기존 구조: 직접 점수 값
                        double emotionScore = emotionData.asDouble();
                        log.info("감정 타입: {}, 점수: {}", emotionType, emotionScore);
                        
                        if (emotionScore > maxScore) {
                            maxScore = emotionScore;
                            maxEmotion = emotionType;
                        }
                    }
                }
                
                emotion = maxEmotion;
                score = maxScore;
                
                // 새로운 구조에서는 감정별 신뢰도를 사용
                if (maxConfidence > 0) {
                    confidence = maxConfidence;
                }
            }
            
            // 점수 필드가 있으면 직접 사용 (OpenAI API가 별도로 제공하는 경우)
            if (emotionNode.has("점수")) {
                score = emotionNode.path("점수").asDouble();
            }
            
            // 신뢰도가 아직 설정되지 않았으면 루트 레벨에서 추출 (0~100 범위)
            if (confidence == 0.0 && emotionNode.has("신뢰도")) {
                confidence = emotionNode.path("신뢰도").asDouble();
            }
            
            // 키워드는 감정 타입으로 대체 (현재는 감정 타입을 키워드로 사용)
            keywords = emotion;
            
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
