package com.barogo.user.repository;

import com.barogo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 repository
 * @author ehjang
 */
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
}