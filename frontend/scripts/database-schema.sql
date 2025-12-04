-- mooDiary 데이터베이스 스키마

-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS moodiary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE moodiary;

-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    profile_image VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 일기 엔트리 테이블
CREATE TABLE diary_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    image_url VARCHAR(500),
    text_emotion ENUM('HAPPY', 'SAD', 'ANGRY', 'DEPRESSED', 'CALM', 'EXCITED', 'ANXIOUS', 'NEUTRAL'),
    text_emotion_score DOUBLE,
    text_emotion_confidence DOUBLE,
    facial_emotion ENUM('HAPPY', 'SAD', 'ANGRY', 'DEPRESSED', 'CALM', 'EXCITED', 'ANXIOUS', 'NEUTRAL'),
    facial_emotion_score DOUBLE,
    facial_emotion_confidence DOUBLE,
    integrated_emotion ENUM('HAPPY', 'SAD', 'ANGRY', 'DEPRESSED', 'CALM', 'EXCITED', 'ANXIOUS', 'NEUTRAL'),
    integrated_emotion_score DOUBLE,
    integrated_emotion_confidence DOUBLE,
    keywords TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 추천 콘텐츠 테이블
CREATE TABLE recommended_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('QUOTE', 'MUSIC', 'VIDEO', 'MEDITATION') NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    content_url VARCHAR(500),
    target_emotion ENUM('HAPPY', 'SAD', 'ANGRY', 'DEPRESSED', 'CALM', 'EXCITED', 'ANXIOUS', 'NEUTRAL') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 커뮤니티 게시글 테이블
CREATE TABLE community_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    emotion_type ENUM('HAPPY', 'SAD', 'ANGRY', 'DEPRESSED', 'CALM', 'EXCITED', 'ANXIOUS', 'NEUTRAL') NOT NULL,
    is_anonymous BOOLEAN DEFAULT FALSE,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 커뮤니티 댓글 테이블
CREATE TABLE community_comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_anonymous BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_diary_entries_user_id ON diary_entries(user_id);
CREATE INDEX idx_diary_entries_created_at ON diary_entries(created_at);
CREATE INDEX idx_community_posts_user_id ON community_posts(user_id);
CREATE INDEX idx_community_posts_created_at ON community_posts(created_at);
CREATE INDEX idx_community_comments_post_id ON community_comments(post_id);

-- 샘플 데이터 삽입
INSERT INTO recommended_contents (type, title, content, target_emotion) VALUES
('QUOTE', '행복한 마음', '행복은 내 마음속에 있습니다.', 'HAPPY'),
('QUOTE', '희망의 메시지', '어려운 시간도 지나갑니다.', 'SAD'),
('MUSIC', '평온한 음악', '차분한 클래식 음악', 'CALM'),
('VIDEO', '웃음 유발 영상', '기분을 좋게 해주는 영상', 'HAPPY');
