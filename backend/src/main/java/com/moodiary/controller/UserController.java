package com.moodiary.controller;

import com.moodiary.dto.UserDto;
import com.moodiary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 API 컨트롤러
 * 
 * 사용자 인증, 회원가입, 프로필 관리 등 사용자 관련 모든 REST API 엔드포인트를 제공합니다.
 * 
 * 주요 기능:
 * - 사용자 회원가입 및 이메일/닉네임 중복 검증
 * - 사용자 로그인 및 JWT 토큰 발급
 * - 사용자 프로필 조회 및 수정
 * - 비밀번호 변경 및 계정 삭제
 * 
 * API 특징:
 * - RESTful 설계 원칙 준수
 * - Swagger/OpenAPI 문서화 지원
 * - CORS 설정으로 프론트엔드 연동 지원
 * - Spring Security를 통한 인증/인가
 * - 로깅을 통한 요청/응답 추적
 * 
 * @author hyeonSuKim
 * @since 2025-09-03
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "사용자 API", description = "사용자 회원가입, 로그인, 프로필 관리 API")
public class UserController {
    
    /**
     * 사용자 서비스 의존성 주입
     * 
     * 사용자 관련 비즈니스 로직을 처리하는 서비스 계층과 연동합니다.
     * 
     * 주요 책임:
     * - 사용자 인증 및 회원가입 처리
     * - JWT 토큰 생성 및 관리
     * - 사용자 프로필 관리
     * - 데이터 변환 및 응답 구성
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    private final UserService userService;
    
    /**
     * 사용자 회원가입 API
     * 
     * 새로운 사용자를 시스템에 등록합니다.
     * 이메일과 닉네임의 중복을 검증하고, 비밀번호를 암호화하여 저장합니다.
     * 
     * 처리 과정:
     * 1. 이메일 중복 검증
     * 2. 닉네임 중복 검증
     * 3. 비밀번호 암호화
     * 4. 사용자 데이터베이스 저장
     * 5. 사용자 정보 응답 반환
     * 
     * @param request 회원가입 요청 데이터 (이메일, 비밀번호, 닉네임, 프로필 이미지)
     * @return 생성된 사용자 정보
     * 
     * HTTP 상태 코드:
     * - 201: 회원가입 성공
     * - 400: 잘못된 요청 (이메일/닉네임 중복, 필수 필드 누락)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<?> register(@RequestBody UserDto.SignUpRequest request) {
        log.info("회원가입 요청 - 이메일: {}, 닉네임: {}", request.getEmail(), request.getNickname());
        
        try {
            UserDto.UserResponse response = userService.signUp(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * 사용자 로그인 API
     * 
     * 이메일과 비밀번호를 검증하여 사용자 인증을 수행합니다.
     * 현재는 로그인 성공 시 사용자 정보만 반환합니다.
     * 
     * @param request 로그인 요청 데이터 (이메일, 비밀번호)
     * @return 사용자 기본 정보를 포함한 응답
     * 
     * HTTP 상태 코드:
     * - 200: 로그인 성공
     * - 401: 인증 실패 (이메일 또는 비밀번호 오류)
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 로그인을 수행합니다.")
    public ResponseEntity<?> login(@RequestBody UserDto.LoginRequest request) {
        log.info("로그인 요청 - 이메일: {}", request.getEmail());
        
        try {
            UserDto.UserResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    /**
     * 사용자 프로필 조회 API
     * 
     * 특정 사용자의 프로필 정보를 조회합니다.
     * 비밀번호는 보안상 제외하고 반환됩니다.
     * 
     * @param userId 조회할 사용자 ID (경로 변수)
     * @return 사용자 프로필 정보
     * 
     * HTTP 상태 코드:
     * - 200: 조회 성공
     * - 404: 사용자 없음
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @GetMapping("/{userId}")
    @Operation(summary = "프로필 조회", description = "사용자의 프로필 정보를 조회합니다.")
    public ResponseEntity<?> getUserProfile(
            @Parameter(description = "사용자 ID") @PathVariable Long userId) {
        
        log.info("프로필 조회 요청 - 사용자 ID: {}", userId);
        
        try {
            UserDto.UserResponse response = userService.getUserProfile(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("프로필 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    /**
     * 사용자 프로필 수정 API
     * 
     * 사용자의 닉네임과 프로필 이미지를 수정합니다.
     * 닉네임 변경 시 중복 검증을 수행합니다.
     * 
     * @param userId 수정할 사용자 ID (경로 변수)
     * @param request 프로필 수정 요청 데이터 (닉네임, 프로필 이미지)
     * @return 수정된 사용자 프로필 정보
     * 
     * HTTP 상태 코드:
     * - 200: 수정 성공
     * - 404: 사용자 없음
     * - 400: 닉네임 중복
     * 
     * @author hyeonSuKim
     * @since 2025-09-03
     */
    @PutMapping("/{userId}")
    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보를 수정합니다.")
    public ResponseEntity<?> updateUserProfile(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @RequestBody UserDto.UpdateProfileRequest request) {
        
        log.info("프로필 수정 요청 - 사용자 ID: {}, 새 닉네임: {}", userId, request.getNickname());
        
        try {
            UserDto.UserResponse response = userService.updateUserProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("프로필 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
