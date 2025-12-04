package com.moodiary.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.moodiary.jwt.JwtTokenFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증 실패 시 JSON 응답만 반환하는 EntryPoint
        org.springframework.security.web.AuthenticationEntryPoint jsonEntryPoint = (request, response, authException) -> {
            log.error("인증 실패 - Path: {}, Method: {}, Error: {}", 
                request.getRequestURI(), request.getMethod(), authException.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            String errorMessage = String.format(
                "{\"error\": \"Unauthorized\", \"message\": \"인증이 필요합니다. JWT 토큰을 확인해주세요.\", \"path\": \"%s\", \"method\": \"%s\"}",
                request.getRequestURI(),
                request.getMethod()
            );
            response.getWriter().write(errorMessage);
            response.getWriter().flush();
        };
        
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                // 인증 실패 시 JSON 응답만 반환하도록 먼저 설정
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jsonEntryPoint)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            String errorMessage = String.format(
                                "{\"error\": \"Access Denied\", \"message\": \"접근이 거부되었습니다.\", \"path\": \"%s\", \"method\": \"%s\"}",
                                request.getRequestURI(),
                                request.getMethod()
                            );
                            response.getWriter().write(errorMessage);
                            response.getWriter().flush();
                        }))
                // JWT 필터를 가장 먼저 실행
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/login").denyAll() // OAuth2 및 기본 로그인 페이지 차단
                        .requestMatchers("/users/login", "/users/reissue").permitAll()
                        .requestMatchers("/diaries/**").authenticated()
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://moo-diary-fe.vercel.app",
                "https://*.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
