package com.barogo.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 entity
 * @author ehjang
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Builder
    public User(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }
}
