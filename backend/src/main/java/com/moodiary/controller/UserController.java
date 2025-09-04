package com.moodiary.controller;

import com.moodiary.dto.UserDto;
import com.moodiary.entity.User;
import com.moodiary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO: 사용자 관련 API 구현
    // - 회원가입
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto.SignUpRequest signUpRequest) {
        User user = userService.createUser(signUpRequest);
        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }
    // - 로그인
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserDto.LoginRequest loginRequest) {
        UserDto.TokenResponse tokenResponse = userService.userLogin(loginRequest);
        return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    }


    // - 사용자 정보 조회
    // - 사용자 정보 수정
    
}
