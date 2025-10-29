package com.moodiary.service;

import com.moodiary.entity.*;
import com.moodiary.dto.BookmarkDto;
import com.moodiary.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public void addBookmark(Long userId, Long diaryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기 없음"));

        if (bookmarkRepository.findByUserAndDiaryEntry(user, diary).isPresent()) {
            throw new IllegalStateException("이미 북마크된 일기입니다.");
        }

        // ✅ DiaryEntry에서 제목 대신 내용(content)의 앞부분을 잘라 사용
        String previewTitle = diary.getContent() != null
                ? diary.getContent().substring(0, Math.min(20, diary.getContent().length())) // 앞 20자
                : "무제";

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .diaryEntry(diary)
                .title(previewTitle)
                .content(diary.getContent())
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long userId, Long diaryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기 없음"));

        bookmarkRepository.deleteByUserAndDiaryEntry(user, diary);
    }

    @Transactional(readOnly = true)
    public List<BookmarkDto> getBookmarksByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return bookmarkRepository.findByUser(user).stream()
                .map(b -> BookmarkDto.builder()
                        .id(b.getId())
                        .userId(user.getId())
                        .diaryId(b.getDiaryEntry().getId())
                        .diaryTitle(b.getTitle()) // ✅ Bookmark에 저장된 previewTitle 사용
                        .content(b.getContent())
                        .createdAt(b.getCreatedAt() != null ? b.getCreatedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }
}
