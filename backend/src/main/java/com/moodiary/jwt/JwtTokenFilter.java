package com.moodiary.jwt;



import com.moodiary.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 토큰을 필터링하는 클래스
 * 현재 리프레시 토큰만 필터링 하는 로직만 구현
 * 추후 리프레시 토큰 추가 예정
 */
@Component
public class JwtTokenFilter extends GenericFilter {
    @Value("${jwt.secret}")
    private String secretKey;

    private final UserRepository userRepository;

    public JwtTokenFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // 토큰을 검증하는 필터
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader("Authorization");
        try {
            if (token != null ) {
                if (!token.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationServiceException("not bearer type");
                }
                String jwtToken = token.substring(7);

                SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(jwtToken)
                        .getPayload();

                List<GrantedAuthority> authoruties = new ArrayList<>();
                authoruties.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authoruties);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Member 기반 Authentication으로 교체
                String email = claims.getSubject();
                com.moodiary.entity.User user = userRepository.findByEmail(email).orElse(null);
                if (user != null) {
                    Authentication memberAuth = new UsernamePasswordAuthenticationToken(user, jwtToken, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(memberAuth);
                }


            }
            chain.doFilter(request, response);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) { // JWT 문제만 401로 반환
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json;char");
            httpServletResponse.getWriter().write("Invalid token");}
    }


}
