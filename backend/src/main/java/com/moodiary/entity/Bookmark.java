package com.moodiary.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;  // 북마크 제목 (일기 제목 복사 가능)

    @Column(nullable = false)
    private String preview;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 북마크 내용 (일기 내용 복사 가능)

    // 북마크를 등록한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User", nullable = false)
    private User user;

    // 북마크가 참조하는 DiaryEntry (일기)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Diary", nullable = false)
    private DiaryEntry diaryEntry;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
