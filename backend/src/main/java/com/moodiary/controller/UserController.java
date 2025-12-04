package com.moodiary.controller;

import com.moodiary.dto.UserDto;
import com.moodiary.entity.User;
import com.moodiary.entity.UserUserDetails;
import com.moodiary.repository.UserRepository;
import com.moodiary.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
@Tag(name = "사용자 API", description = "사용자 회원가입, 로그인, 토큰 갱신 API")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // TODO: 사용자 관련 API 구현
    // - 회원가입
    @PostMapping("/create")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto.SignUpRequest signUpRequest) {
        UserDto.UserResponse userResponse = userService.createUser(signUpRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }


    // - 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        try {
            UserDto.TokenResponse tokenResponse = userService.userLogin(loginRequest);
            
            // 사용자 정보를 가져와서 함께 반환
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
            UserDto.UserResponse userResponse = UserDto.UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            
            // 토큰과 사용자 정보를 함께 반환
            UserDto.LoginResponse loginResponse = UserDto.LoginResponse.builder()
                    .accessToken(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType(tokenResponse.getTokenType())
                    .expiresIn(tokenResponse.getExpiresIn())
                    .user(userResponse)
                    .build();
            
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ErrorResponse("로그인 중 오류가 발생했습니다: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
    
    // 에러 응답을 위한 내부 클래스
    private static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }


    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token을 사용하여 새로운 Access Token을 발급받습니다")
    public ResponseEntity<?> createRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh-token");
        refreshToken = refreshToken.substring(7);
//        System.out.println("refresh: " + refreshToken);
        UserDto.TokenResponse tokenResponse = userService.createNewAccessToken(refreshToken);

        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);

    }

    @PostMapping("/social/login")
    @Operation(summary = "소셜 로그인", description = "Google, Kakao, Naver 등 소셜 로그인을 처리합니다")
    public ResponseEntity<?> googleToken(@Valid @RequestBody UserDto.GoogleLoginRequest googleLoginRequest) {
        UserDto.TokenResponse tokenResponse = userService.googleUserLogin(googleLoginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }


    @GetMapping("/test")
    @Operation(summary = "테스트 엔드포인트", description = "서버 연결 테스트용 엔드포인트입니다")
    public String test() {
        return "OK";
    }
}
