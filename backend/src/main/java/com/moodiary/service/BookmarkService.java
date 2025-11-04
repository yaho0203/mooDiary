package com.moodiary.service;

import com.moodiary.entity.*;
import com.moodiary.dto.BookmarkDto;
import com.moodiary.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public void addBookmark(Long diaryId) {
        Long userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기 없음"));

        if (bookmarkRepository.findByUserAndDiaryEntry(user, diary).isPresent()) {
            throw new IllegalStateException("이미 북마크된 일기입니다.");
        }

        // ✅ DiaryEntry에서 제목 대신 내용(content)의 앞부분을 잘라 사용
        String previewTitle = diary.getContent() != null
                ? diary.getContent().substring(0, Math.min(20, diary.getContent().length())) : "무제"; // 앞 20글자

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .diaryEntry(diary)
//                .title()
                .preview(previewTitle)
                .content(diary.getContent())
                .build();

        bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(Long diaryId) {
        // 1차로 일기 꺼내기
        // 이 일기가 존재 하냐 안하냐
        // 로그인중인 유저 아이디 추출
        // 일기 작성한 유저와 현재 로그인중인 유저 일치하는지 검사
        // 일기 아이디로 일기 삭제
        DiaryEntry diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기 없음"));
        if (diary.getUser().getId().equals(getCurrentUserId())) {
            // 여기 예외처리는 알아서 하십쇼
        }
        User user = userRepository.findById(getCurrentUserId()).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

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


    private Long getCurrentUserId() {
        // TODO: SecurityContextHolder or JwtUserDetails 활용
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUser().getId();
        return userId;
    }
}
