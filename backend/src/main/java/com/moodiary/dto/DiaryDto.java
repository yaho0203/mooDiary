package com.moodiary.dto;

import com.moodiary.entity.EmotionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DiaryDto {
    
    @Getter
    @Setter
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateDiaryRequest {
        private String content;
        private String imageUrl;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateDiaryRequest {
        private String content;
        private String imageUrl;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryResponse {
        private Long id;
        private Long userId;
        private String content;
        private String imageUrl;
        private EmotionAnalysisResponse emotionAnalysis;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionAnalysisResponse {
        private EmotionScoreResponse textEmotion;
        private EmotionScoreResponse facialEmotion;
        private EmotionScoreResponse integratedEmotion;
        private List<String> keywords;
        private LocalDateTime timestamp;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionScoreResponse {
        private EmotionType emotion;
        private Double score;
        private Double confidence;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EmotionTrendResponse {
        private LocalDateTime date;
        private EmotionType dominantEmotion;
        private Double averageScore;
        private List<EmotionScoreResponse> emotionBreakdown;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisSummaryResponse {
        private Long diaryId;
        private String content;
        private EmotionType overallEmotion;
        private Double overallEmotionScore;
        private String dominantEmotion;
        private List<String> topKeywords;
        private String analysisInsight;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
