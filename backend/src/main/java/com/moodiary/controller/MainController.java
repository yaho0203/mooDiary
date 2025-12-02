package com.moodiary.controller;

import com.moodiary.dto.DiaryDto;
import com.moodiary.dto.UserDto;
import com.moodiary.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "메인 페이지 API", description = "메인 페이지 데이터 조회 API")
public class MainController {

    private final MainService mainService;

    /**
     * 로그인한 사용자 프로필 조회
     * (닉네임, 프로필 이미지)
     */
    @GetMapping("/user/profile")
    @Operation(summary = "사용자 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다")
    public ResponseEntity<UserDto.UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(mainService.getUserProfile());
    }
    /**
     * 오늘 작성한 일기 조회
     * - 있으면 요약/이미지 반환
     * - 없으면 204 No Content
     */
    @GetMapping("/diary/today")
    @Operation(summary = "오늘 작성한 일기 조회", description = "오늘 작성한 일기를 조회합니다")
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
    @Operation(summary = "최근 작성한 일기 조회", description = "최근 작성한 일기 4개를 조회합니다")
    public ResponseEntity<List<DiaryDto.DiaryResponse>> getRecentDiaries() {
        List<DiaryDto.DiaryResponse> recentDiaries = mainService.getRecentDiaries();
        return ResponseEntity.ok(recentDiaries);
    }


    /**
     * 메인 페이지 통합 응답
     * - userProfile + todayDiary + recentDiaries
     */
    @GetMapping("/test")
    @Operation(summary = "메인 페이지 통합 조회", description = "사용자 프로필, 오늘 일기, 최근 일기를 한 번에 조회합니다")
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
