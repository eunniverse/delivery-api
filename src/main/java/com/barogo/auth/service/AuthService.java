package com.barogo.auth.service;

import com.barogo.auth.dto.LoginRequest;
import com.barogo.auth.dto.TokenRefreshRequest;
import com.barogo.auth.dto.TokenResponse;
import com.barogo.auth.entity.RefreshToken;
import com.barogo.auth.enums.UserStatus;
import com.barogo.auth.repository.RefreshTokenRepository;
import com.barogo.common.exception.AccountInactiveException;
import com.barogo.common.exception.InvalidRefreshTokenException;
import com.barogo.common.exception.LoginFailedException;
import com.barogo.common.exception.TokenStorageException;
import com.barogo.common.util.JwtUtil;
import com.barogo.user.entity.User;
import com.barogo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 로그인 관련 service
 * @author ehjang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final int REFRESH_TOKEN_VALID_DAYS = 7;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new LoginFailedException("존재하지 않는 사용자입니다."));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new AccountInactiveException("휴면 계정입니다. 관리자에게 문의하세요.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new LoginFailedException("비밀번호가 일치하지 않습니다.");
        }

        try {
            String accessToken = jwtUtil.createAccessToken(user.getUserId());
            String refreshToken = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(REFRESH_TOKEN_VALID_DAYS);

            refreshTokenRepository.findByUserId(user.getUserId())
                    .ifPresentOrElse(
                            existing -> existing.update(refreshToken, expiryDate),
                            () -> refreshTokenRepository.save(new RefreshToken(user.getUserId(), refreshToken, expiryDate))
                    );

            return new TokenResponse(accessToken, refreshToken);

        } catch (TokenStorageException e) {
            log.error("Token 생성 실패", e);
            throw e;

        } catch (Exception e) {
            log.error("예기치 못한 오류로 로그인 실패", e);
            throw new TokenStorageException("로그인 실패하였습니다.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public TokenResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new InvalidRefreshTokenException("RefreshToken이 누락되었습니다.");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new LoginFailedException("유효하지 않은 RefreshToken입니다."));

        if (stored.isExpired()) {
            throw new InvalidRefreshTokenException("RefreshToken이 만료되었습니다. 다시 로그인 해주세요.");
        }

        String userId = stored.getUserId();
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new LoginFailedException("존재하지 않는 사용자입니다."));

        try {
            String newAccessToken = jwtUtil.createAccessToken(userId);
            String newRefreshToken = UUID.randomUUID().toString();
            LocalDateTime newExpiryDate = LocalDateTime.now().plusDays(REFRESH_TOKEN_VALID_DAYS);

            stored.update(newRefreshToken, newExpiryDate);
            return new TokenResponse(newAccessToken, newRefreshToken);

        } catch (Exception e) {
            log.error("예기치 못한 오류로 토큰 재발급 실패", e);
            throw new TokenStorageException("토큰 재발급에 실패했습니다.");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(TokenRefreshRequest request) {
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
    }
}
