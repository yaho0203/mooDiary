package com.moodiary.service;

import com.moodiary.dto.UserDto;
import com.moodiary.entity.Role;
import com.moodiary.entity.User;
import com.moodiary.jwt.JwtTokenFilter;
import com.moodiary.jwt.JwtTokenProvider;
import com.moodiary.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenFilter jwtTokenFilter;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, JwtTokenFilter jwtTokenFilter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    public UserDto.UserResponse createUser(UserDto.@Valid SignUpRequest signUpRequest) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Optional<User> userOptional = userRepository.findByEmail(signUpRequest.getEmail());

            if (userOptional.isPresent()) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }

            User user = User.builder()
                    .email(signUpRequest.getEmail())
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .nickname(signUpRequest.getNickname())
                    .phoneNumber(signUpRequest.getPhone())
                    .profileImage(signUpRequest.getProfileImage())
                    .createdAt(now)
                    .build();

            User savedUser = userRepository.save(user);

            UserDto.UserResponse userResponse = UserDto.UserResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .nickname(savedUser.getNickname())
                    .phone(savedUser.getPhoneNumber())
                    .profileImage(savedUser.getProfileImage())
                    .createdAt(savedUser.getCreatedAt())
                    .updatedAt(savedUser.getUpdatedAt())
                    .build();

            return userResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("사용자 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public UserDto.TokenResponse userLogin(UserDto.@Valid LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자 입니다"));

        // 비밀번호가 없는 경우 (소셜 로그인 사용자)
        if (user.getPassword() == null) {
            throw new IllegalStateException("소셜 로그인으로 가입된 계정입니다. 소셜 로그인을 이용해주세요.");
        }

        // 비밀번호 앞뒤 공백 제거
        String inputPassword = loginRequest.getPassword() != null ? loginRequest.getPassword().trim() : "";
        String storedPassword = user.getPassword();
        
        // 디버깅을 위한 로그 (프로덕션에서는 제거)
        System.out.println("로그인 시도 - 이메일: " + loginRequest.getEmail());
        System.out.println("저장된 비밀번호 형식: " + (storedPassword != null && storedPassword.length() > 0 ? storedPassword.substring(0, Math.min(20, storedPassword.length())) + "..." : "null"));
        
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        // 역할이 null인 경우 기본값으로 USER 설정하고 DB에 저장
        Role userRole = user.getRole();
        if (userRole == null) {
            userRole = Role.USER;
            user.setRole(userRole);
            userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), userRole);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), userRole);

        UserDto.TokenResponse tokenResponse = UserDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // 아직 구현 못 함 ㅠㅠ
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();

        return tokenResponse;

    }

    public UserDto.TokenResponse createNewAccessToken(String refreshToken) {
        // 리프레시 토큰 검증


        if (jwtTokenFilter.validateRefreshToken(refreshToken)) {
            Long userId = jwtTokenProvider.extractUserId(refreshToken);
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
            String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());

            return UserDto.TokenResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(86400000L)
                    .build();
        } else {
            throw new IllegalStateException("리프레시 토큰이 만료되었습니다 다시 로그인 해주세요");
        }


    }

    public UserDto.TokenResponse createNewAcceccToken(String refreshToken) {
        // 리프레시 토큰 검증
        if (jwtTokenFilter.validateRefreshToken(refreshToken)) {
            Long userId = jwtTokenProvider.extractUserId(refreshToken);
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
            String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());

            return UserDto.TokenResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(newAccessToken)
                    .tokenType("Bearer")
                    .expiresIn(86400000L)
                    .build();
        } else {
            throw new IllegalStateException("리프레시 토큰이 만료되었습니다 다시 로그인 해주세요");
        }
    }

    public UserDto.TokenResponse googleUserLogin(UserDto.@Valid GoogleLoginRequest googleLoginRequest) {
        User user = userRepository.findById(googleLoginRequest.getUserId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자 입니다"));


        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        UserDto.TokenResponse tokenResponse = UserDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken) // 아직 구현 못 함 ㅠㅠ
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .build();

        return tokenResponse;

    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }
}
