package com.moodiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommended_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RecommendedContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType type;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "content_url")
    private String contentUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "target_emotion", nullable = false)
    private EmotionType targetEmotion;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum ContentType {
        QUOTE("명언"),
        MUSIC("음악"),
        VIDEO("영상"),
        MEDITATION("명상");
        
        private final String description;
        
        ContentType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
