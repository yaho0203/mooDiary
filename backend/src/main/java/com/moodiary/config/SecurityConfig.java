package com.moodiary.config;

import com.moodiary.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security 설정 클래스
 * - 개발 단계에서 모든 API 접근을 허용 (permitAll)
 * - CSRF 및 로그인 폼 비활성화
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()  // 모든 요청 허용 (개발용)
                )
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화
                .formLogin(form -> form.disable()) // 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // HTTP Basic 인증 비활성화
                .sessionManagement(s->s.sessionCreationPolicy((SessionCreationPolicy.STATELESS))) // 세션 비활성화
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
//    @Bean
//    public CorsConfigurationSource configurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList("Https://localhost:3000"));
//        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // 모든 url에서 요청 허용 (배포시 url 설정)
//        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 메서드 허용
//        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 HTTP 헤더 허용
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // cors 설정 소스 생성
//        source.registerCorsConfiguration("/**", configuration); // 모든 요청에 적용
//        return source;
//    }

}
