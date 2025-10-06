package com.moodiary.service;

import com.moodiary.dto.DiaryDto;
import com.moodiary.dto.DiaryDto.DiaryResponse;
import com.moodiary.entity.DiaryEntry;
import com.moodiary.entity.EmotionType;
import com.moodiary.entity.User;
import com.moodiary.repository.DiaryRepository;
import com.moodiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    /**
     * 로그인한 사용자 프로필 조회
     */
    public com.moodiary.dto.UserDto.UserProfileResponse getUserProfile() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        return new com.moodiary.dto.UserDto.UserProfileResponse(user.getNickname(), user.getProfileImage());
    }

    /**
     * 오늘 일기 작성 여부 + 감정 요약
     */
    public DiaryResponse getTodayDiary() {
        Long userId = getCurrentUserId();
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();           // 2025-10-06 00:00:00
        LocalDateTime end = today.atTime(LocalTime.MAX);      // 2025-10-06 23:59:59.999999

        Optional<DiaryEntry> diaryOpt = diaryRepository.findByUserIdAndCreatedAtBetween(userId, start, end);


//        Optional<DiaryEntry> diaryOpt = diaryRepository.findByUserIdAndDate(userId, today);
        return diaryOpt.map(this::toDiaryResponse).orElse(null);
    }

    /**
     * 최근 작성한 일기 5개
     */
    public List<DiaryResponse> getRecentDiaries() {
        Long userId = getCurrentUserId();
        List<DiaryEntry> diaries = diaryRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);

        return diaries.stream()
                .map(this::toDiaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * DiaryEntry → DiaryResponse 변환 공통 메서드
     */
    private DiaryResponse toDiaryResponse(DiaryEntry diaryEntry) {
        String summaryTitle = diaryEntry.getContent().length() > 30
                ? diaryEntry.getContent().substring(0, 30) + "..."
                : diaryEntry.getContent();

        EmotionType emotion = diaryEntry.getIntegratedEmotion();

        return DiaryResponse.builder()
                .id(diaryEntry.getId())
                .userId(diaryEntry.getUser().getId()) // userId 직접 가져오기
                .content(summaryTitle)
                .imageUrl(null)
                .emotionAnalysis(
                        DiaryDto.EmotionAnalysisResponse.builder()
                                .integratedEmotion(
                                        DiaryDto.EmotionScoreResponse.builder()
                                                .emotion(emotion)
                                                .build()
                                )
                                .build()
                )
                .createdAt(diaryEntry.getCreatedAt())
                .updatedAt(diaryEntry.getUpdatedAt())
                .build();
    }

    /**
     * 로그인 사용자 ID 가져오기 (임시)
     */
    private Long getCurrentUserId() {
        // TODO: SecurityContextHolder or JwtUserDetails 활용
        return 9L; // 임시 값
    }
}
