package com.moodiary.controller;

import com.moodiary.dto.UserDto;
import com.moodiary.entity.User;
import com.moodiary.entity.UserUserDetails;
import com.moodiary.repository.UserRepository;
import com.moodiary.service.UserService;
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
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto.SignUpRequest signUpRequest) {
        User user = userService.createUser(signUpRequest);
        
        // 사용자 정보를 DTO로 변환하여 반환
        UserDto.UserResponse userResponse = UserDto.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
        
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }
    // - 로그인
    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
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
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> createRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("refresh-token");
        refreshToken = refreshToken.substring(7);
//        System.out.println("refresh: " + refreshToken);
        UserDto.TokenResponse tokenResponse = userService.createNewAccessToken(refreshToken);

        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);

    }

    @PostMapping("/social/login")
    public ResponseEntity<?> googleToken(@Valid @RequestBody UserDto.GoogleLoginRequest googleLoginRequest) {
        UserDto.TokenResponse tokenResponse = userService.googleUserLogin(googleLoginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }


    @GetMapping("/test")
    public String test() {
        return "OK";
    }
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserUserDetails userDetails = (UserUserDetails) auth.getPrincipal();
    Long userId = userDetails.getUser().getId();

    
}
