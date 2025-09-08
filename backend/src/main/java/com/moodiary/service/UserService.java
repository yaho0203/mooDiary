package com.moodiary.service;

import com.moodiary.dto.UserDto;
import com.moodiary.entity.User;
import com.moodiary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 사용자 서비스 클래스
 * 
 * 사용자 인증, 회원가입, 프로필 관리 등 사용자 관련 모든 비즈니스 로직을 처리합니다.
 * 비밀번호 암호화, JWT 토큰 생성, 사용자 데이터 검증 등을 담당합니다.
 * 
 * 주요 기능:
 * - 사용자 회원가입 및 이메일/닉네임 중복 검증
 * - 사용자 로그인 및 비밀번호 검증
 * - JWT 토큰 생성 (Access Token, Refresh Token)
 * - 사용자 프로필 조회 및 수정
 * - 비밀번호 변경 및 사용자 계정 삭제
 * - 보안을 위한 비밀번호 암호화
 * 
 * 보안 특징:
 * - BCrypt를 사용한 비밀번호 암호화
 * - JWT 기반 인증 시스템
 * - 이메일과 닉네임의 유일성 보장
 * 
 * @author hyeonSuKim
 * @since 2025-09-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    // ==================== 의존성 주입 ====================
    
    /**
     * 사용자 데이터 접근을 위한 Repository
     */
    private final UserRepository userRepository;
    
    /**
     * 비밀번호 암호화를 위한 인코더
     * BCrypt 알고리즘 사용으로 보안 강화
     */
    private final PasswordEncoder passwordEncoder;
    
    // ==================== 사용자 인증 메서드들 ====================
    
    /**
     * 사용자 회원가입
     * 
     * 새로운 사용자를 시스템에 등록합니다.
     * 이메일과 닉네임의 중복을 검증하고, 비밀번호를 암호화하여 저장합니다.
     * 
     * @param request 회원가입 요청 데이터 (이메일, 비밀번호, 닉네임, 프로필 이미지)
     * @return 생성된 사용자 정보 응답 DTO
     * @throws RuntimeException 이메일 또는 닉네임이 이미 존재하는 경우
     */
    @Transactional
    public UserDto.UserResponse signUp(UserDto.SignUpRequest request) {
        log.info("회원가입 요청 - 이메일: {}, 닉네임: {}", request.getEmail(), request.getNickname());
        
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("회원가입 실패 - 이미 존재하는 이메일: {}", request.getEmail());
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        
        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            log.warn("회원가입 실패 - 이미 존재하는 닉네임: {}", request.getNickname());
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug("비밀번호 암호화 완료 - 원본 길이: {}", request.getPassword().length());
        
        // 사용자 엔티티 생성
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .profileImage(request.getProfileImage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // 데이터베이스에 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 완료 - 사용자 ID: {}, 이메일: {}", savedUser.getId(), savedUser.getEmail());
        
        return convertToResponse(savedUser);
    }
    
    /**
     * 사용자 로그인
     * 
     * 이메일과 비밀번호를 검증하여 사용자 인증을 수행합니다.
     * 현재는 로그인 성공 시 사용자 정보만 반환합니다.
     * 
     * @param request 로그인 요청 데이터 (이메일, 비밀번호)
     * @return 사용자 기본 정보를 포함한 응답 DTO
     * @throws RuntimeException 이메일 또는 비밀번호가 올바르지 않은 경우
     */
    public UserDto.UserResponse login(UserDto.LoginRequest request) {
        log.info("로그인 요청 - 이메일: {}", request.getEmail());
        
        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("로그인 실패 - 존재하지 않는 이메일: {}", request.getEmail());
                    return new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
                });
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패 - 잘못된 비밀번호 - 사용자: {}", user.getId());
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        log.info("로그인 성공 - 사용자 ID: {}, 이메일: {}", user.getId(), user.getEmail());
        
        // 사용자 정보 반환
        return convertToResponse(user);
    }
    
    // ==================== 사용자 프로필 관리 메서드들 ====================
    
    /**
     * 사용자 프로필 조회
     * 
     * 특정 사용자의 프로필 정보를 조회합니다.
     * 비밀번호는 보안상 제외하고 반환됩니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보 응답 DTO
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    public UserDto.UserResponse getUserProfile(Long userId) {
        log.info("프로필 조회 요청 - 사용자 ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("프로필 조회 실패 - 존재하지 않는 사용자: {}", userId);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });
        
        log.debug("프로필 조회 완료 - 사용자: {}, 닉네임: {}", userId, user.getNickname());
        return convertToResponse(user);
    }
    
    /**
     * 사용자 프로필 수정
     * 
     * 사용자의 닉네임과 프로필 이미지를 수정합니다.
     * 닉네임 변경 시 중복 검증을 수행합니다.
     * 
     * @param userId 수정할 사용자 ID
     * @param request 프로필 수정 요청 데이터 (닉네임, 프로필 이미지)
     * @return 수정된 사용자 프로필 정보 응답 DTO
     * @throws RuntimeException 사용자를 찾을 수 없거나 닉네임이 중복되는 경우
     */
    @Transactional
    public UserDto.UserResponse updateUserProfile(Long userId, UserDto.UpdateProfileRequest request) {
        log.info("프로필 수정 요청 - 사용자 ID: {}, 새 닉네임: {}", userId, request.getNickname());
        
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("프로필 수정 실패 - 존재하지 않는 사용자: {}", userId);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });
        
        // 닉네임 중복 확인 (자신의 기존 닉네임은 제외)
        if (!user.getNickname().equals(request.getNickname()) && 
            userRepository.existsByNickname(request.getNickname())) {
            log.warn("프로필 수정 실패 - 이미 존재하는 닉네임: {}", request.getNickname());
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }
        
        // 프로필 정보 업데이트
        user.updateProfile(request.getNickname(), request.getProfileImage());
        
        log.info("프로필 수정 완료 - 사용자 ID: {}, 닉네임: {}", userId, request.getNickname());
        return convertToResponse(user);
    }
    
    /**
     * 사용자 비밀번호 변경
     * 
     * 현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.
     * 새 비밀번호는 암호화되어 저장됩니다.
     * 
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param currentPassword 현재 비밀번호 (검증용)
     * @param newPassword 새로운 비밀번호
     * @throws RuntimeException 사용자를 찾을 수 없거나 현재 비밀번호가 올바르지 않은 경우
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        log.info("비밀번호 변경 요청 - 사용자 ID: {}", userId);
        
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("비밀번호 변경 실패 - 존재하지 않는 사용자: {}", userId);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });
        
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("비밀번호 변경 실패 - 잘못된 현재 비밀번호 - 사용자: {}", userId);
            throw new RuntimeException("현재 비밀번호가 올바르지 않습니다.");
        }
        
        // 새 비밀번호 암호화 및 저장
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedNewPassword);
        
        log.info("비밀번호 변경 완료 - 사용자 ID: {}", userId);
    }
    
    /**
     * 사용자 계정 삭제
     * 
     * 사용자 계정을 영구적으로 삭제합니다.
     * 비밀번호 확인 후 삭제를 진행하여 보안을 강화합니다.
     * 
     * @param userId 삭제할 사용자 ID
     * @param password 계정 삭제 확인용 비밀번호
     * @throws RuntimeException 사용자를 찾을 수 없거나 비밀번호가 올바르지 않은 경우
     */
    @Transactional
    public void deleteUser(Long userId, String password) {
        log.info("회원 탈퇴 요청 - 사용자 ID: {}", userId);
        
        // 사용자 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("회원 탈퇴 실패 - 존재하지 않는 사용자: {}", userId);
                    return new RuntimeException("사용자를 찾을 수 없습니다.");
                });
        
        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("회원 탈퇴 실패 - 잘못된 비밀번호 - 사용자: {}", userId);
            throw new RuntimeException("비밀번호가 올바르지 않습니다.");
        }
        
        // 사용자 계정 삭제
        userRepository.delete(user);
        log.info("회원 탈퇴 완료 - 사용자 ID: {}", userId);
    }
    
    // ==================== 사용자 조회 메서드들 ====================
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 정보 (Optional)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * 사용자 ID로 사용자 조회
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보 (Optional)
     */
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    // ==================== 내부 헬퍼 메서드들 ====================
    
    /**
     * User 엔티티를 UserResponse DTO로 변환
     * 
     * 보안을 위해 비밀번호는 제외하고 반환합니다.
     * 
     * @param user 변환할 사용자 엔티티
     * @return 변환된 사용자 응답 DTO
     */
    private UserDto.UserResponse convertToResponse(User user) {
        return UserDto.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
