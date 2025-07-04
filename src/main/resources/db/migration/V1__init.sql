-- 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Refresh Token 테이블
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token VARCHAR(500) NOT NULL,
                                expiry_date TIMESTAMP NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 배달 데이터 테이블
CREATE TABLE IF NOT EXISTS deliveries (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT NOT NULL,
                            address VARCHAR(255) NOT NULL,
                            status VARCHAR(20) NOT NULL,
                            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            is_deleted BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (user_id) REFERENCES users(id)
);