package com.barogo.common.util;

import com.barogo.common.exception.InvalidTokenException;
import com.barogo.common.exception.TokenStorageException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.hmacShaKeyFor("jwt-key-delivery-api-security-1234567890123456".getBytes());
    private final long accessTokenValidity = 30 * 60 * 1000; // 30분

    /**
     * AccessToken 생성
     * @param userId
     * @return
     */
    public String createAccessToken(String userId) {
        try {
            return Jwts.builder()
                    .setSubject(userId)
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
     * @param token
     * @return
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
     * @param token
     * @return
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
     * 토큰 추출
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new InvalidTokenException("Authorization 헤더가 유효하지 않습니다.");
    }
}
