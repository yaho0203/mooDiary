package com.moodiary.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DiaryEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    // 감정 분석 결과
    @Enumerated(EnumType.STRING)
    @Column(name = "text_emotion")
    private EmotionType textEmotion;
    
    @Column(name = "text_emotion_score")
    private Double textEmotionScore;
    
    @Column(name = "text_emotion_confidence")
    private Double textEmotionConfidence;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "facial_emotion")
    private EmotionType facialEmotion;
    
    @Column(name = "facial_emotion_score")
    private Double facialEmotionScore;
    
    @Column(name = "facial_emotion_confidence")
    private Double facialEmotionConfidence;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "integrated_emotion")
    private EmotionType integratedEmotion;
    
    @Column(name = "integrated_emotion_score")
    private Double integratedEmotionScore;
    
    @Column(name = "integrated_emotion_confidence")
    private Double integratedEmotionConfidence;
    
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords; // JSON 형태로 저장
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
