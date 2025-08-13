package com.moodiary.dto;

import com.moodiary.entity.EmotionType;
import lombok.*;

import java.util.List;

public class OpenAiDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionAnalysisRequest {
        private String text;
        private String imageUrl;
        private Long userId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionAnalysisResponse {
        private EmotionScore textEmotion;
        private EmotionScore facialEmotion;
        private EmotionScore integratedEmotion;
        private List<String> keywords;
        private Double processingTime;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionScore {
        private EmotionType emotion;
        private Double score;
        private Double confidence;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentRecommendationRequest {
        private EmotionType emotionType;
        private Long userId;
        private Integer limit;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContentRecommendationResponse {
        private List<RecommendedContent> recommendations;
        private EmotionType emotionType;
        private Long userId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendedContent {
        private String type; // QUOTE, MUSIC, VIDEO, MEDITATION
        private String title;
        private String content;
        private String url;
        private EmotionType targetEmotion;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpenAiApiRequest {
        private String model;
        private List<Message> messages;
        private Double temperature;
        private Integer maxTokens;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OpenAiApiResponse {
        private String id;
        private String object;
        private Long created;
        private String model;
        private List<Choice> choices;
        private Usage usage;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
