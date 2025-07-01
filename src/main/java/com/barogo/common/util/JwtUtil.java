package com.barogo.common.util;

import com.barogo.common.exception.TokenStorageException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor("jwt-key-delivery-api-security-1234567890123456".getBytes());
    private final long accessTokenValidity = 30 * 60 * 1000; // 30분

    /**
     * AccessToken 생성
     */
    public String createAccessToken(String username) {
        try {
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new TokenStorageException("토큰 생성 중 문제가 발생했습니다.");
        }
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            throw new TokenStorageException("토큰에서 사용자 ID를 추출할 수 없습니다.");
        }
    }

    /**
     * 토큰 만료 시간 추출
     */
    public Date getExpiration(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
        } catch (JwtException e) {
            throw new TokenStorageException("토큰 만료일자를 확인할 수 없습니다.");
        }
    }
}
