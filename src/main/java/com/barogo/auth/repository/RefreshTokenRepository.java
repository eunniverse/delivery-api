package com.barogo.auth.repository;

import com.barogo.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * refresh_tokens 테이블 관련 repository
 * @author ehjang
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(String userId);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
