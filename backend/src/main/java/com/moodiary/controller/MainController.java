package com.moodiary.controller;

import com.moodiary.dto.DiaryDto;
import com.moodiary.dto.UserDto;
import com.moodiary.service.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메인 페이지 API
 * - 로그인한 사용자 정보
 * - 오늘 작성한 일기
 * - 최근 작성된 일기
 */
@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final MainService mainService;

    /**
     * 로그인한 사용자 프로필 조회
     * (닉네임, 프로필 이미지)
     */
    @GetMapping("/user/profile")
    public ResponseEntity<UserDto.UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(mainService.getUserProfile());
    }
    /**
     * 오늘 작성한 일기 조회
     * - 있으면 요약/이미지 반환
     * - 없으면 204 No Content
     */
    @GetMapping("/diary/today")
    public ResponseEntity<DiaryDto.DiaryResponse> getTodayDiary() {
        DiaryDto.DiaryResponse todayDiary = mainService.getTodayDiary();
        if (todayDiary == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(todayDiary);
    }


    /**
     * 최근 작성한 일기 4개 조회
     * - 요약/이미지 포함
     */
    @GetMapping("/diary/recent")
    public ResponseEntity<List<DiaryDto.DiaryResponse>> getRecentDiaries() {
        List<DiaryDto.DiaryResponse> recentDiaries = mainService.getRecentDiaries();
        return ResponseEntity.ok(recentDiaries);
    }


    /**
     * 메인 페이지 통합 응답
     * - userProfile + todayDiary + recentDiaries
     */
    @GetMapping("/test")
    public ResponseEntity<MainPageResponse> getMainPage() {
        return ResponseEntity.ok(
                new MainPageResponse(
                        mainService.getUserProfile(),
                        mainService.getTodayDiary(),
                        mainService.getRecentDiaries()
                )
        );
    }

    /**
     * 메인 페이지 응답용 DTO
     */
    public record MainPageResponse(
            UserDto.UserProfileResponse userProfile,
            DiaryDto.DiaryResponse todayDiary,
            List<DiaryDto.DiaryResponse> recentDiaries
    ) {}
}
