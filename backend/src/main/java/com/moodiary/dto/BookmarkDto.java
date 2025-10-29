package com.moodiary.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDto {
    private Long id;
    private Long userId;
    private Long diaryId;
    private String content;
    private String diaryTitle; // 일기 제목
    private String createdAt;  // 북마크한 날짜 (ISO string)
}
