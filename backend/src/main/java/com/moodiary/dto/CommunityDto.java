package com.moodiary.dto;

import com.moodiary.entity.EmotionType;
import lombok.*;

import java.time.LocalDateTime;

public class CommunityDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePostRequest {
        private String content;
        private EmotionType emotionType;
        private Boolean isAnonymous;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdatePostRequest {
        private String content;
        private EmotionType emotionType;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostResponse {
        private Long id;
        private Long userId;
        private String nickname;
        private String content;
        private EmotionType emotionType;
        private Boolean isAnonymous;
        private Integer likeCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateCommentRequest {
        private String content;
        private Boolean isAnonymous;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateCommentRequest {
        private String content;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommentResponse {
        private Long id;
        private Long postId;
        private Long userId;
        private String nickname;
        private String content;
        private Boolean isAnonymous;
        private LocalDateTime createdAt;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostDetailResponse {
        private Long id;
        private Long userId;
        private String nickname;
        private String content;
        private EmotionType emotionType;
        private Boolean isAnonymous;
        private Integer likeCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private java.util.List<CommentResponse> comments;
    }
}
