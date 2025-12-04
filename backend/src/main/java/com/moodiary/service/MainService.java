package com.moodiary.service;

import com.moodiary.dto.DiaryDto;
import com.moodiary.dto.DiaryDto.DiaryResponse;
import com.moodiary.dto.UserDto;
import com.moodiary.entity.DiaryEntry;
import com.moodiary.entity.EmotionType;
import com.moodiary.entity.User;
import com.moodiary.entity.UserUserDetails;
import com.moodiary.repository.DiaryRepository;
import com.moodiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public UserDto.UserProfileResponse getUserProfile() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        UserDto.UserProfileResponse userProfileResponse = UserDto.UserProfileResponse.builder()
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .phoneNumber(user.getPhoneNumber())
                .build();


        return userProfileResponse;
    }

    /**
     * 오늘 일기 작성 여부 + 감정 요약
     */
    public DiaryResponse getTodayDiary() {
        Long userId = getCurrentUserId();
        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();           // 2025-10-06 00:00:00
        LocalDateTime end = today.atTime(LocalTime.MAX);      // 2025-10-06 23:59:59.999999

        // 오늘 작성된 일기들을 생성일자 기준 내림차순으로 가져오기
        List<DiaryEntry> diaries = diaryRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, start, end);

        // 가장 최신 일기 하나만 가져와서 DiaryResponse로 변환
        return diaries.isEmpty() ? null : toDiaryResponse(diaries.get(0));
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
        String content = diaryEntry.getContent();
        if (content == null) {
            content = "";
        }
        
        String summaryTitle = content.length() > 30
                ? content.substring(0, 30) + "..."
                : content;

        EmotionType emotion = diaryEntry.getIntegratedEmotion();
        
        Long userId = null;
        if (diaryEntry.getUser() != null) {
            userId = diaryEntry.getUser().getId();
        }

        return DiaryResponse.builder()
                .id(diaryEntry.getId())
                .userId(userId)
                .content(summaryTitle)
                .imageUrl(diaryEntry.getImageUrl())
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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) {
                throw new IllegalStateException("인증 정보가 없습니다.");
            }
            
            if (!(auth.getPrincipal() instanceof UserUserDetails)) {
                throw new IllegalStateException("인증 정보 형식이 올바르지 않습니다.");
            }
            
            UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
            if (userDetails.getUser() == null) {
                throw new IllegalStateException("사용자 정보가 없습니다.");
            }
            
            Long userId = userDetails.getUser().getId();
            if (userId == null) {
                throw new IllegalStateException("사용자 ID가 없습니다.");
            }
            
            return userId;
        } catch (ClassCastException e) {
            throw new IllegalStateException("인증 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
