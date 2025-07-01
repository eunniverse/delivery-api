-- users 테이블 status 추가 (휴면계정 관리)
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- refresh_tokens 테이블 user_id 형 변경
ALTER TABLE refresh_tokens DROP FOREIGN KEY refresh_tokens_ibfk_1;

ALTER TABLE refresh_tokens MODIFY COLUMN user_id VARCHAR(50) NOT NULL;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user_id
        FOREIGN KEY (user_id) REFERENCES users(user_id);