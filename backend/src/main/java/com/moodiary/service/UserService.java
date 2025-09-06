package com.moodiary.service;

import com.moodiary.dto.UserDto;
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

    public User createUser(UserDto.@Valid SignUpRequest signUpRequest) {
        LocalDateTime now = LocalDateTime.now();
        Optional<User> userOptional = userRepository.findByEmail(signUpRequest.getEmail());

        if (userOptional.isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .createdAt(now)
                .build();


        userRepository.save(user);

        return user;
    }

    public UserDto.TokenResponse userLogin(UserDto.@Valid LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자 입니다"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalStateException("<UNK> <UNK> <UNK>.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail());

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
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("<UNK> <UNK> <UNK>."));
            String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail());

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
}
