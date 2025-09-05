package com.moodiary.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final int expiration;
    private final int refreshExpiration;
    private Key SECRET_KEY;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expiration}") int expiration,@Value("{jwt.refresh-expiration") int refreshExpiration) {
        this.secretKey = secretKey;
        this.expiration = expiration;
        this.SECRET_KEY = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS512.getJcaName());
        this.refreshExpiration = refreshExpiration;
    }

    // 액세스 토큰 생성
    public String createToken(Long id, String email) {
        Claims claims = Jwts.claims()
                .subject(email)
                .add("id", id)
                .build();

        Date now =  new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() * expiration * 60 * 1000L))
                .signWith(SECRET_KEY)
                .compact();

        return token;
    }


    // Refresh Token 생성
    public String createRefreshToken(Long id) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshExpiration * 24 * 60 * 60 * 1000L); // 예: 14일

        Claims claims = Jwts.claims()
                .add("id", id)
                .build();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SECRET_KEY)
                .compact();
    }


    // 토큰에서 userId 꺼내는 헬퍼
    public Long extractUserId(String token) {
        // JJWT 0.12.3 버전에서 확실히 작동하는 코드
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getEncoded());
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return ((Number) claims.get("id")).longValue(); // int/long 모두 대응
    }
}
