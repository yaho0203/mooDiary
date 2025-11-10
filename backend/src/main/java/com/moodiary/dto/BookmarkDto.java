package com.moodiary.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDto {
//    private Long id;
//    private Long userId;
//    private Long diaryId;
//    private String content;
//    private String diaryTitle; // 일기 제목
//    private String createdAt;  // 북마크한 날짜 (ISO string)


    private Long numberOfBookmarkedDiary;
    private Long numberOfTotalDiary;
    private Double averageTemperature;
    private List<DiaryContent> bookmarks;


    @Getter
    @Setter
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiaryContent {
        private Long diaryId;
        private String content;
        private Double Temperature;
        private LocalDateTime createdAt;
    }
}
