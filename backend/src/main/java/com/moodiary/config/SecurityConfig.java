package com.moodiary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * - 개발 단계에서 모든 API 접근을 허용 (permitAll)
 * - CSRF 및 로그인 폼 비활성화
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // 모든 요청 허용 (개발용)
            )
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화
            .formLogin(form -> form.disable()) // 로그인 폼 비활성화
            .httpBasic(basic -> basic.disable()); // HTTP Basic 인증 비활성화
        
        return http.build();
    }
    
    /**
     * 비밀번호 암호화를 위한 PasswordEncoder Bean
     * BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 암호화합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 