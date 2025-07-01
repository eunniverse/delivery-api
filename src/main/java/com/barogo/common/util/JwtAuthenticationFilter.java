package com.barogo.common.util;

import com.barogo.user.entity.User;
import com.barogo.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            if (!jwtUtil.validateToken(token)) {
                log.warn("유효하지 않은 token : {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtUtil.extractUserId(token);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, null);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT 예외: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류", e);
        }

        filterChain.doFilter(request, response);
    }
}
