package com.moodiary.jwt;



import com.moodiary.entity.User;
import com.moodiary.entity.UserUserDetails;
import com.moodiary.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;


/**
 * 토큰을 필터링하는 클래스
 * 현재 리프레시 토큰만 필터링 하는 로직만 구현
 * 추후 리프레시 토큰 추가 예정
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    private final UserRepository userRepository;

    public JwtTokenFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        log.info("JWT Filter - Path: {}, Method: {}, Has Token: {}", 
            request.getRequestURI(), request.getMethod(), token != null);

        try {
            if (token != null) {
                if (!token.startsWith("Bearer ")) {
                    throw new AuthenticationServiceException("not bearer type");
                }
                String jwtToken = token.substring(7);

                SecretKey key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), "HmacSHA256");
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();

                // User 엔티티 조회
                String email = claims.getSubject();
                User user = userRepository.findByEmail(email).orElse(null);

                if (user != null) {
                    // UserUserDetails로 래핑해서 Authentication 생성
                    UserUserDetails userDetails = new UserUserDetails(user);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, jwtToken, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("JWT Filter - 인증 성공: User ID = {}", user.getId());
                } else {
                    log.warn("JWT Filter - 사용자를 찾을 수 없음: email = {}", email);
                }
            } else {
                log.info("JWT Filter - 토큰 없음, 다음 필터로 진행: Path = {}", request.getRequestURI());
            }
            filterChain.doFilter(request, response);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            log.error("JWT Filter - 토큰 검증 실패: {}", e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\": \"Invalid token\", \"message\": \"" + e.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseClaimsJws(refreshToken);

            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return false;
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}


