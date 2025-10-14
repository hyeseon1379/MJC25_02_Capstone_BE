-- Database Schema for Book Reading Application
-- Created with logical flow and proper dependencies

-- Drop tables if exist (in reverse order of creation)
DROP TABLE IF EXISTS `result_images`;
DROP TABLE IF EXISTS `vote`;
DROP TABLE IF EXISTS `dialogue_answer`;
DROP TABLE IF EXISTS `share_request`;
DROP TABLE IF EXISTS `story`;
DROP TABLE IF EXISTS `dialogue_question`;
DROP TABLE IF EXISTS `contest_result`;
DROP TABLE IF EXISTS `reply`;
DROP TABLE IF EXISTS `share_board`;
DROP TABLE IF EXISTS `contest_details`;
DROP TABLE IF EXISTS `challenge_details`;
DROP TABLE IF EXISTS `Subscription`;
DROP TABLE IF EXISTS `package_book`;
DROP TABLE IF EXISTS `dialogue`;
DROP TABLE IF EXISTS `book_details`;
DROP TABLE IF EXISTS `contest`;
DROP TABLE IF EXISTS `package`;
DROP TABLE IF EXISTS `board`;
DROP TABLE IF EXISTS `Book`;
DROP TABLE IF EXISTS `reader`;
DROP TABLE IF EXISTS `children`;
DROP TABLE IF EXISTS `challenge`;
DROP TABLE IF EXISTS `package_categories`;
DROP TABLE IF EXISTS `share_board_image`;
DROP TABLE IF EXISTS `board_image`;
DROP TABLE IF EXISTS `book_category`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `refresh_token` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `user_id` BIGINT NOT NULL,
     `token` VARCHAR(255) NOT NULL,
     `expiry_date` DATETIME NOT NULL,
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_user_id` (`user_id`),
     CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
);


-- ========================================
-- Independent Tables (No Foreign Keys)
-- ========================================

-- User Table
CREATE TABLE `user` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `username` VARCHAR(20) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `birth` DATE NULL,
    `phone` VARCHAR(20) NULL,
    `nickname` VARCHAR(20) NULL,
    `color` VARCHAR(10) NULL,
    `address` VARCHAR(255) NULL,
    `profile_img` VARCHAR(255) NULL,
    `role` ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Book Category Table
CREATE TABLE `book_category` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Package Categories Table
CREATE TABLE `package_categories` (
    `category_id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Challenge Table
CREATE TABLE `challenge` (
    `challenge_id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    PRIMARY KEY (`challenge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Board Image Table
CREATE TABLE `board_image` (
    `image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(255) NULL,
    `file_path` VARCHAR(255) NULL,
    PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Share Board Image Table
CREATE TABLE `share_board_image` (
    `image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(255) NULL,
    `file_path` VARCHAR(255) NULL,
    PRIMARY KEY (`image_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- First Level Dependencies
-- ========================================

-- Children Table
CREATE TABLE `children` (
    `child_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `birth` DATE NULL,
    `gender` ENUM('male', 'female', 'other') NULL,
    `num` INT NULL COMMENT 'Child order number',
    `profile` VARCHAR(255) NULL,
    `color` VARCHAR(10) NULL,
    PRIMARY KEY (`child_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_children_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reader Table
CREATE TABLE `reader` (
    `reader_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `type` ENUM('adult', 'child') NOT NULL,
    PRIMARY KEY (`reader_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_reader_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Book Table
CREATE TABLE `Book` (
    `book_id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_id` BIGINT NULL,
    `title` VARCHAR(255) NOT NULL,
    `img_url` VARCHAR(500) NULL,
    `author` VARCHAR(100) NULL,
    `publisher` VARCHAR(100) NULL,
    `isbn` VARCHAR(20) NULL,
    PRIMARY KEY (`book_id`),
    KEY `idx_category_id` (`category_id`),
    CONSTRAINT `fk_book_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Package Table
CREATE TABLE `package` (
    `package_id` BIGINT NOT NULL AUTO_INCREMENT,
    `category_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`package_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_package_category` FOREIGN KEY (`category_id`) REFERENCES `package_categories` (`category_id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_package_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contest Table
CREATE TABLE `contest` (
    `contest_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `content` TEXT NULL,
    `start_date` DATETIME NULL,
    `end_date` DATETIME NULL,
    `progress_status` ENUM('planned', 'ongoing', 'completed', 'cancelled') NOT NULL DEFAULT 'planned',
    `image` VARCHAR(500) NULL,
    PRIMARY KEY (`contest_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_progress_status` (`progress_status`),
    CONSTRAINT `fk_contest_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Board Table
CREATE TABLE `board` (
    `board_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `image_id` BIGINT NULL,
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NULL,
    `create_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`board_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_image_id` (`image_id`),
    CONSTRAINT `fk_board_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_board_image` FOREIGN KEY (`image_id`) REFERENCES `board_image` (`image_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Second Level Dependencies
-- ========================================

-- Book Details Table
CREATE TABLE `book_details` (
    `details_id` BIGINT NOT NULL AUTO_INCREMENT,
    `book_id` BIGINT NOT NULL,
    `reader_id` BIGINT NOT NULL,
    `start_date` DATETIME NULL,
    `end_date` DATETIME NULL,
    `reading_status` ENUM('not_started', 'reading', 'completed', 'paused') NOT NULL DEFAULT 'not_started',
    PRIMARY KEY (`details_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_reading_status` (`reading_status`),
    CONSTRAINT `fk_book_details_book` FOREIGN KEY (`book_id`) REFERENCES `Book` (`book_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_book_details_reader` FOREIGN KEY (`reader_id`) REFERENCES `reader` (`reader_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dialogue Table
CREATE TABLE `dialogue` (
    `dialog_id` BIGINT NOT NULL AUTO_INCREMENT,
    `book_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`dialog_id`),
    KEY `idx_book_id` (`book_id`),
    CONSTRAINT `fk_dialogue_book` FOREIGN KEY (`book_id`) REFERENCES `Book` (`book_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Package Book Table
CREATE TABLE `package_book` (
    `packageBook_id` BIGINT NOT NULL AUTO_INCREMENT,
    `book_id` BIGINT NOT NULL,
    `package_id` BIGINT NOT NULL,
    PRIMARY KEY (`packageBook_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_package_id` (`package_id`),
    UNIQUE KEY `uk_package_book` (`package_id`, `book_id`),
    CONSTRAINT `fk_package_book_book` FOREIGN KEY (`book_id`) REFERENCES `Book` (`book_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_package_book_package` FOREIGN KEY (`package_id`) REFERENCES `package` (`package_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Subscription Table
CREATE TABLE `Subscription` (
    `subscription_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `package_id` BIGINT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `start_date` DATETIME NOT NULL,
    `end_date` DATETIME NOT NULL,
    `auto_renew` BOOLEAN NOT NULL DEFAULT FALSE,
    `status` ENUM('active', 'expired', 'cancelled', 'pending') NOT NULL DEFAULT 'pending',
    PRIMARY KEY (`subscription_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_package_id` (`package_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_subscription_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_subscription_package` FOREIGN KEY (`package_id`) REFERENCES `package` (`package_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Challenge Details Table
CREATE TABLE `challenge_details` (
    `details_id` BIGINT NOT NULL AUTO_INCREMENT,
    `challenge_id` BIGINT NOT NULL,
    `child_id` BIGINT NULL,
    `content` TEXT NULL,
    `success` BOOLEAN NOT NULL DEFAULT FALSE,
    `completed_at` TIMESTAMP NULL,
    PRIMARY KEY (`details_id`),
    KEY `idx_challenge_id` (`challenge_id`),
    KEY `idx_child_id` (`child_id`),
    CONSTRAINT `fk_challenge_details_challenge` FOREIGN KEY (`challenge_id`) REFERENCES `challenge` (`challenge_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_challenge_details_child` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contest Details Table
CREATE TABLE `contest_details` (
    `details_id` BIGINT NOT NULL AUTO_INCREMENT,
    `contest_id` BIGINT NOT NULL,
    `round` ENUM('round_1', 'round_2', 'round_3', 'final') NOT NULL,
    `start_prompt` TEXT NULL,
    `start_date` DATETIME NULL,
    `end_date` DATETIME NULL,
    `progress_status` ENUM('not_started', 'ongoing', 'completed') NOT NULL DEFAULT 'not_started',
    PRIMARY KEY (`details_id`),
    KEY `idx_contest_id` (`contest_id`),
    KEY `idx_round` (`round`),
    CONSTRAINT `fk_contest_details_contest` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Share Board Table
CREATE TABLE `share_board` (
    `share_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `category_id` BIGINT NULL,
    `image_id` BIGINT NULL,
    `share_status` ENUM('SHARING', 'RESERVED', 'COMPLETED') NOT NULL DEFAULT 'SHARING' COMMENT '나눔중/예약중/나눔완료',
    `title` VARCHAR(100) NOT NULL,
    `content` TEXT NULL,
    `address` VARCHAR(255) NULL,
    `datetime` DATETIME NULL,
    `price` INT NULL DEFAULT 0,
    `book_status` ENUM('A', 'B', 'C') NOT NULL DEFAULT 'A' COMMENT '등급 : A, B, C',
    `create_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`share_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_image_id` (`image_id`),
    KEY `idx_share_status` (`share_status`),
    CONSTRAINT `fk_share_board_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_share_board_category` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`category_id`) ON DELETE SET NULL,
    CONSTRAINT `fk_share_board_image` FOREIGN KEY (`image_id`) REFERENCES `share_board_image` (`image_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reply Table
CREATE TABLE `reply` (
    `reply_id` BIGINT NOT NULL AUTO_INCREMENT,
    `board_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` VARCHAR(500) NULL,
    `create_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `update_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`reply_id`),
    KEY `idx_board_id` (`board_id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_reply_board` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Contest Result Table
CREATE TABLE `contest_result` (
    `result_id` BIGINT NOT NULL AUTO_INCREMENT,
    `contest_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NULL,
    `final_content` TEXT NULL,
    `cover_image` VARCHAR(500) NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`result_id`),
    KEY `idx_contest_id` (`contest_id`),
    CONSTRAINT `fk_contest_result_contest` FOREIGN KEY (`contest_id`) REFERENCES `contest` (`contest_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Third Level Dependencies
-- ========================================

-- Dialogue Question Table
CREATE TABLE `dialogue_question` (
    `question_id` BIGINT NOT NULL AUTO_INCREMENT,
    `dialog_id` BIGINT NOT NULL,
    `question` TEXT NOT NULL,
    PRIMARY KEY (`question_id`),
    KEY `idx_dialog_id` (`dialog_id`),
    CONSTRAINT `fk_dialogue_question_dialogue` FOREIGN KEY (`dialog_id`) REFERENCES `dialogue` (`dialog_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Story Table
CREATE TABLE `story` (
    `story_id` BIGINT NOT NULL AUTO_INCREMENT,
    `details_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `content` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `vote_count` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`story_id`),
    KEY `idx_details_id` (`details_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_vote_count` (`vote_count`),
    CONSTRAINT `fk_story_details` FOREIGN KEY (`details_id`) REFERENCES `book_details` (`details_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_story_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Share Request Table
CREATE TABLE `share_request` (
    `request_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `share_id` BIGINT NOT NULL,
    `content` TEXT NULL,
    `result_status` ENUM('pending', 'approved', 'rejected', 'cancelled') NOT NULL DEFAULT 'pending',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`request_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_share_id` (`share_id`),
    KEY `idx_result_status` (`result_status`),
    CONSTRAINT `fk_share_request_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_share_request_share` FOREIGN KEY (`share_id`) REFERENCES `share_board` (`share_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dialogue Answer Table
CREATE TABLE `dialogue_answer` (
    `answer_id` BIGINT NOT NULL AUTO_INCREMENT,
    `dialog_id` BIGINT NOT NULL,
    `reader_id` BIGINT NOT NULL,
    `question_id` BIGINT NOT NULL,
    `answer` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`answer_id`),
    KEY `idx_dialog_id` (`dialog_id`),
    KEY `idx_reader_id` (`reader_id`),
    KEY `idx_question_id` (`question_id`),
    CONSTRAINT `fk_dialogue_answer_dialogue` FOREIGN KEY (`dialog_id`) REFERENCES `dialogue` (`dialog_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_dialogue_answer_reader` FOREIGN KEY (`reader_id`) REFERENCES `reader` (`reader_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_dialogue_answer_question` FOREIGN KEY (`question_id`) REFERENCES `dialogue_question` (`question_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Vote Table
CREATE TABLE `vote` (
    `vote_id` BIGINT NOT NULL AUTO_INCREMENT,
    `story_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `voted_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`vote_id`),
    KEY `idx_story_id` (`story_id`),
    KEY `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_story_user` (`story_id`, `user_id`),
    CONSTRAINT `fk_vote_story` FOREIGN KEY (`story_id`) REFERENCES `story` (`story_id`) ON DELETE CASCADE,
    CONSTRAINT `fk_vote_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Result Images Table
CREATE TABLE `result_images` (
    `image_id` BIGINT NOT NULL AUTO_INCREMENT,
    `result_id` BIGINT NOT NULL,
    `image_url` VARCHAR(500) NULL,
    `image_order` INT NULL,
    PRIMARY KEY (`image_id`),
    KEY `idx_result_id` (`result_id`),
    CONSTRAINT `fk_result_images_result` FOREIGN KEY (`result_id`) REFERENCES `contest_result` (`result_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ========================================
-- Indexes for Performance Optimization
-- ========================================

-- Additional indexes for frequently queried columns
CREATE INDEX idx_user_email ON `user` (`email`);
CREATE INDEX idx_book_title ON `Book` (`title`);
CREATE INDEX idx_contest_dates ON `contest` (`start_date`, `end_date`);
CREATE INDEX idx_subscription_dates ON `Subscription` (`start_date`, `end_date`);
CREATE INDEX idx_share_board_dates ON `share_board` (`create_date`, `datetime`);

-- ========================================
-- Sample Data Insertion
-- ========================================

-- Insert Users (10 rows)
INSERT INTO `user` (`email`, `username`, `password`, `birth`, `phone`, `nickname`, `color`, `address`, `profile_img`, `role`) VALUES
('admin@bookapp.com', 'admin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1985-03-15', '010-1234-5678', '관리자', '#FF5733', '서울시 강남구 테헤란로 123', '/images/profiles/admin.jpg', 'ADMIN'),
('user1@example.com', 'kimjihoon', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1990-05-20', '010-2345-6789', '책벌레지훈', '#3498DB', '서울시 서초구 서초대로 456', '/images/profiles/user1.jpg', 'USER'),
('user2@example.com', 'leesoyoung', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1988-08-12', '010-3456-7890', '독서왕소영', '#E74C3C', '경기도 성남시 분당구 판교역로 789', '/images/profiles/user2.jpg', 'USER'),
('user3@example.com', 'parkminsu', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1992-11-30', '010-4567-8901', '북러버민수', '#2ECC71', '부산시 해운대구 센텀중앙로 101', '/images/profiles/user3.jpg', 'USER'),
('user4@example.com', 'choihyejin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1995-02-14', '010-5678-9012', '책사랑혜진', '#F39C12', '대구시 수성구 동대구로 202', '/images/profiles/user4.jpg', 'USER'),
('user5@example.com', 'jungwoojin', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1987-07-22', '010-6789-0123', '리딩마스터', '#9B59B6', '인천시 연수구 송도과학로 303', '/images/profiles/user5.jpg', 'USER'),
('user6@example.com', 'kangmina', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1993-04-18', '010-7890-1234', '동화사랑미나', '#1ABC9C', '광주시 서구 상무대로 404', '/images/profiles/user6.jpg', 'USER'),
('user7@example.com', 'yoonsungho', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1991-09-05', '010-8901-2345', '북콜렉터', '#34495E', '대전시 유성구 대학로 505', '/images/profiles/user7.jpg', 'USER'),
('user8@example.com', 'limjiyeon', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '1994-12-25', '010-9012-3456', '그림책조아', '#E67E22', '울산시 남구 삼산로 606', '/images/profiles/user8.jpg', 'USER'),
('guest@example.com', 'guestuser', '$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', '2000-01-01', '010-0123-4567', '게스트', '#95A5A6', NULL, '/images/profiles/guest.jpg', 'USER');

-- Insert Book Categories (10 rows)
INSERT INTO `book_category` (`category_name`) VALUES
('그림책'),
('동화'),
('위인전'),
('과학'),
('역사'),
('예술'),
('자연관찰'),
('창작동화'),
('전래동화'),
('영어동화');

-- Insert Package Categories (10 rows)
INSERT INTO `package_categories` (`category_name`) VALUES
('유아 (3-5세)'),
('초등 저학년 (6-8세)'),
('초등 고학년 (9-12세)'),
('베스트셀러'),
('신간도서'),
('계절별 추천'),
('주제별 학습'),
('독서 습관 형성'),
('감성 발달'),
('창의력 향상');

-- Insert Challenges (10 rows)
INSERT INTO `challenge` (`title`, `description`) VALUES
('30일 독서 챌린지', '한 달 동안 매일 책 한 권씩 읽기'),
('주말 가족 독서', '주말마다 가족과 함께 책 읽기'),
('감상문 쓰기', '읽은 책에 대한 감상문 작성하기'),
('소리내어 읽기', '책을 소리내어 읽고 녹음하기'),
('그림 그리기', '책 내용을 그림으로 표현하기'),
('독서 퀴즈', '책 내용에 대한 퀴즈 풀기'),
('친구와 토론하기', '읽은 책에 대해 친구들과 토론하기'),
('작가 되어보기', '책을 읽고 나만의 이야기 만들기'),
('등장인물 분석', '주인공의 성격과 행동 분석하기'),
('장르별 읽기', '다양한 장르의 책 경험하기');

-- Insert Board Images (10 rows)
INSERT INTO `board_image` (`file_name`, `file_path`) VALUES
('board_img_001.jpg', '/uploads/board/2024/01/board_img_001.jpg'),
('board_img_002.jpg', '/uploads/board/2024/01/board_img_002.jpg'),
('board_img_003.jpg', '/uploads/board/2024/01/board_img_003.jpg'),
('board_img_004.jpg', '/uploads/board/2024/02/board_img_004.jpg'),
('board_img_005.jpg', '/uploads/board/2024/02/board_img_005.jpg'),
('board_img_006.jpg', '/uploads/board/2024/03/board_img_006.jpg'),
('board_img_007.jpg', '/uploads/board/2024/03/board_img_007.jpg'),
('board_img_008.jpg', '/uploads/board/2024/04/board_img_008.jpg'),
('board_img_009.jpg', '/uploads/board/2024/04/board_img_009.jpg'),
('board_img_010.jpg', '/uploads/board/2024/05/board_img_010.jpg');

-- Insert Share Board Images (10 rows)
INSERT INTO `share_board_image` (`file_name`, `file_path`) VALUES
('share_001.jpg', '/uploads/share/2024/01/share_001.jpg'),
('share_002.jpg', '/uploads/share/2024/01/share_002.jpg'),
('share_003.jpg', '/uploads/share/2024/02/share_003.jpg'),
('share_004.jpg', '/uploads/share/2024/02/share_004.jpg'),
('share_005.jpg', '/uploads/share/2024/03/share_005.jpg'),
('share_006.jpg', '/uploads/share/2024/03/share_006.jpg'),
('share_007.jpg', '/uploads/share/2024/04/share_007.jpg'),
('share_008.jpg', '/uploads/share/2024/04/share_008.jpg'),
('share_009.jpg', '/uploads/share/2024/05/share_009.jpg'),
('share_010.jpg', '/uploads/share/2024/05/share_010.jpg');

-- Insert Children (10 rows)
INSERT INTO `children` (`user_id`, `name`, `birth`, `gender`, `num`, `profile`, `color`) VALUES
(2, '김민준', '2018-03-15', 'male', 1, '/images/children/child1.jpg', '#FFB6C1'),
(2, '김서연', '2020-07-22', 'female', 2, '/images/children/child2.jpg', '#DDA0DD'),
(3, '이준호', '2017-11-05', 'male', 1, '/images/children/child3.jpg', '#87CEEB'),
(4, '박지우', '2019-04-18', 'female', 1, '/images/children/child4.jpg', '#FFE4B5'),
(5, '최유진', '2016-09-30', 'female', 1, '/images/children/child5.jpg', '#F0E68C'),
(6, '강도윤', '2018-12-12', 'male', 1, '/images/children/child6.jpg', '#98FB98'),
(6, '강하은', '2021-02-28', 'female', 2, '/images/children/child7.jpg', '#FFDAB9'),
(7, '윤시우', '2017-06-14', 'male', 1, '/images/children/child8.jpg', '#B0E0E6'),
(8, '임서준', '2019-10-20', 'male', 1, '/images/children/child9.jpg', '#FFFACD'),
(8, '임예은', '2021-05-08', 'female', 2, '/images/children/child10.jpg', '#FFB6E1');

-- Insert Readers (10 rows)
INSERT INTO `reader` (`user_id`, `type`) VALUES
(2, 'adult'),
(3, 'adult'),
(4, 'adult'),
(5, 'adult'),
(6, 'adult'),
(7, 'adult'),
(8, 'adult'),
(9, 'adult'),
(2, 'child'),
(3, 'child');

-- Insert Books (10 rows)
INSERT INTO `Book` (`category_id`, `title`, `img_url`, `author`, `publisher`, `isbn`) VALUES
(1, '곰돌이 푸', 'https://image.aladin.co.kr/product/1234/pooh.jpg', 'A.A. 밀른', '비룡소', '9788949101234'),
(2, '해리포터와 마법사의 돌', 'https://image.aladin.co.kr/product/5678/harry.jpg', 'J.K. 롤링', '문학수첩', '9788983920567'),
(3, '세종대왕', 'https://image.aladin.co.kr/product/9012/sejong.jpg', '김영사', '웅진주니어', '9788901901234'),
(4, '우주 탐험대', 'https://image.aladin.co.kr/product/3456/space.jpg', '닐 디그래스 타이슨', '까치', '9788972345678'),
(5, '역사 속으로', 'https://image.aladin.co.kr/product/7890/history.jpg', '유발 하라리', '김영사', '9788934567890'),
(6, '명화 이야기', 'https://image.aladin.co.kr/product/2345/art.jpg', '레오나르도 다빈치', '예술의전당', '9788956234567'),
(7, '숲속의 친구들', 'https://image.aladin.co.kr/product/6789/forest.jpg', '제인 구달', '사계절', '9788967890123'),
(8, '마법의 정원', 'https://image.aladin.co.kr/product/0123/garden.jpg', '프란시스 호지슨 버넷', '창비', '9788901234567'),
(9, '토끼와 거북이', 'https://image.aladin.co.kr/product/4567/rabbit.jpg', '이솝', '보림', '9788945678901'),
(10, 'Brown Bear, What Do You See?', 'https://image.aladin.co.kr/product/8901/bear.jpg', 'Bill Martin Jr.', 'Henry Holt', '9780805047905');

-- Insert Packages (10 rows)
INSERT INTO `package` (`category_id`, `user_id`, `title`, `content`) VALUES
(1, 1, '우리 아이 첫 그림책 패키지', '3-5세 유아를 위한 감성 발달 그림책 모음'),
(2, 1, '초등 1학년 필독서', '초등학교 입학을 앞둔 아이들을 위한 필수 도서'),
(3, 1, '어린이 과학 탐험대', '호기심 많은 초등 고학년을 위한 과학 도서'),
(4, 1, '2024 베스트셀러 TOP 10', '올해 가장 사랑받은 어린이 도서'),
(5, 1, '봄맞이 신간 특집', '따뜻한 봄에 읽기 좋은 신간 도서'),
(6, 1, '여름방학 추천도서', '여름방학 동안 읽으면 좋은 책 모음'),
(7, 1, '세계문화 여행', '다양한 나라의 문화를 배우는 책'),
(8, 1, '독서습관 키우기', '매일 조금씩 읽기 좋은 짧은 이야기'),
(9, 1, '감정 표현 배우기', '아이의 정서 발달을 돕는 그림책'),
(10, 1, '상상력 키우는 창작동화', '창의적 사고를 기르는 이야기책');

-- Insert Contests (10 rows)
INSERT INTO `contest` (`user_id`, `title`, `content`, `start_date`, `end_date`, `progress_status`, `image`) VALUES
(1, '2024 봄 독후감 대회', '봄을 주제로 한 독후감 작성 대회', '2024-03-01 00:00:00', '2024-03-31 23:59:59', 'completed', '/images/contests/spring2024.jpg'),
(1, '여름방학 스토리 공모전', '방학 동안의 특별한 경험을 이야기로', '2024-07-01 00:00:00', '2024-08-31 23:59:59', 'ongoing', '/images/contests/summer2024.jpg'),
(1, '우리가족 이야기', '가족과 함께한 독서 경험 나누기', '2024-05-01 00:00:00', '2024-05-31 23:59:59', 'completed', '/images/contests/family2024.jpg'),
(1, '가을 동화 창작 대회', '가을을 배경으로 한 창작 동화', '2024-09-01 00:00:00', '2024-10-31 23:59:59', 'planned', '/images/contests/autumn2024.jpg'),
(1, '겨울 그림책 공모전', '겨울 이야기를 그림으로 표현하기', '2024-12-01 00:00:00', '2025-01-31 23:59:59', 'planned', '/images/contests/winter2024.jpg'),
(1, '환경보호 캠페인', '환경을 주제로 한 이야기 쓰기', '2024-04-01 00:00:00', '2024-04-30 23:59:59', 'completed', '/images/contests/environment.jpg'),
(1, '우정 이야기 대회', '친구와의 소중한 추억 나누기', '2024-06-01 00:00:00', '2024-06-30 23:59:59', 'completed', '/images/contests/friendship.jpg'),
(1, '상상력 폭발 콘테스트', '자유로운 상상력으로 쓴 이야기', '2024-08-01 00:00:00', '2024-08-31 23:59:59', 'ongoing', '/images/contests/imagination.jpg'),
(1, '역사 인물 재해석', '역사 속 인물을 현대적으로 재해석', '2024-10-01 00:00:00', '2024-10-31 23:59:59', 'planned', '/images/contests/history.jpg'),
(1, '미래 세상 상상하기', '100년 후의 세상을 상상한 이야기', '2024-11-01 00:00:00', '2024-11-30 23:59:59', 'planned', '/images/contests/future.jpg');

-- Insert Boards (10 rows)
INSERT INTO `board` (`user_id`, `image_id`, `title`, `content`) VALUES
(2, 1, '곰돌이 푸 너무 재밌어요!', '아이가 정말 좋아하는 책이에요. 매일 읽어달라고 조르네요.'),
(3, 2, '초등 입학 전 추천도서 있나요?', '내년에 초등학교 입학하는데 미리 읽히면 좋을 책 추천해주세요.'),
(4, 3, '해리포터 시리즈 완독했어요', '드디어 전권 다 읽었습니다! 다음엔 뭘 읽을까요?'),
(5, 4, '그림책 나눔 행사 참여 후기', '지난 주말 나눔 행사 정말 좋았어요. 좋은 책들 많이 얻었습니다.'),
(6, 5, '우리 아이 독서 습관 만들기', '매일 자기 전 책 읽기 시작한 지 한 달, 변화가 보여요!'),
(7, 6, '과학 도서 추천해주세요', '과학에 관심 많은 초등 3학년 아이에게 추천할 책 있을까요?'),
(8, 7, '독후감 쓰기 팁 공유합니다', '아이와 함께 독후감 쓰는 방법을 정리해봤어요.'),
(9, 8, '영어 그림책 추천', '영어 공부도 하고 재미도 있는 그림책 추천 부탁드려요.'),
(2, 9, '도서관 나들이 다녀왔어요', '날씨 좋은 날 아이와 도서관 다녀온 후기입니다.'),
(3, 10, '전래동화의 매력', '요즘 전래동화에 푹 빠진 우리 아이 이야기');

-- Insert Book Details (10 rows)
INSERT INTO `book_details` (`book_id`, `reader_id`, `start_date`, `end_date`, `reading_status`) VALUES
(1, 1, '2024-01-05 10:00:00', '2024-01-15 20:30:00', 'completed'),
(2, 2, '2024-02-01 14:00:00', NULL, 'reading'),
(3, 3, '2024-01-20 09:00:00', '2024-02-28 18:00:00', 'completed'),
(4, 4, '2024-03-01 11:00:00', NULL, 'reading'),
(5, 5, '2024-02-10 16:00:00', '2024-03-10 19:00:00', 'completed'),
(6, 6, '2024-03-15 10:30:00', NULL, 'paused'),
(7, 7, '2024-04-01 13:00:00', NULL, 'reading'),
(8, 8, '2024-03-20 15:00:00', '2024-04-20 17:00:00', 'completed'),
(9, 9, '2024-05-01 09:30:00', NULL, 'not_started'),
(10, 10, '2024-04-15 14:30:00', NULL, 'reading');

-- Insert Dialogues (10 rows)
INSERT INTO `dialogue` (`book_id`) VALUES
(1),
(2),
(3),
(4),
(5),
(6),
(7),
(8),
(9),
(10);

-- Insert Package Books (10 rows)
INSERT INTO `package_book` (`book_id`, `package_id`) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8),
(9, 9),
(10, 10);

-- Insert Subscriptions (10 rows)
INSERT INTO `Subscription` (`user_id`, `package_id`, `start_date`, `end_date`, `auto_renew`, `status`) VALUES
(2, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', TRUE, 'active'),
(3, 2, '2024-02-01 00:00:00', '2025-01-31 23:59:59', TRUE, 'active'),
(4, 3, '2024-03-01 00:00:00', '2024-08-31 23:59:59', FALSE, 'active'),
(5, 4, '2024-01-15 00:00:00', '2024-07-15 23:59:59', FALSE, 'expired'),
(6, 5, '2024-04-01 00:00:00', '2025-03-31 23:59:59', TRUE, 'active'),
(7, 6, '2024-05-01 00:00:00', '2024-10-31 23:59:59', FALSE, 'active'),
(8, 7, '2024-02-15 00:00:00', '2024-08-15 23:59:59', TRUE, 'cancelled'),
(9, 8, '2024-06-01 00:00:00', '2025-05-31 23:59:59', TRUE, 'active'),
(2, 9, '2024-03-10 00:00:00', '2024-09-10 23:59:59', FALSE, 'active'),
(3, 10, '2024-07-01 00:00:00', '2024-12-31 23:59:59', TRUE, 'pending');

-- Insert Challenge Details (10 rows)
INSERT INTO `challenge_details` (`challenge_id`, `child_id`, `content`, `success`, `completed_at`) VALUES
(1, 1, '매일 그림책 1권씩 읽기 완료!', TRUE, '2024-01-31 22:00:00'),
(2, 2, '주말마다 엄마랑 책 읽기', TRUE, '2024-02-28 20:00:00'),
(3, 3, '해리포터 독후감 작성', TRUE, '2024-03-15 18:30:00'),
(4, 4, '동화책 소리내어 읽기 녹음', FALSE, NULL),
(5, 5, '책 내용을 그림으로 표현하기', TRUE, '2024-04-10 16:00:00'),
(6, 6, '우주 탐험대 퀴즈 풀기', TRUE, '2024-04-20 19:00:00'),
(7, 7, '친구들과 책 토론하기', FALSE, NULL),
(8, 8, '나만의 동화 만들기', TRUE, '2024-05-05 17:30:00'),
(9, 9, '주인공 성격 분석하기', FALSE, NULL),
(10, 10, '다양한 장르의 책 10권 읽기', TRUE, '2024-05-30 21:00:00');

-- Insert Contest Details (10 rows)
INSERT INTO `contest_details` (`contest_id`, `round`, `start_prompt`, `start_date`, `end_date`, `progress_status`) VALUES
(1, 'round_1', '봄날의 추억을 떠올리며...', '2024-03-01 00:00:00', '2024-03-10 23:59:59', 'completed'),
(1, 'final', '봄을 주제로 한 최종 독후감', '2024-03-20 00:00:00', '2024-03-31 23:59:59', 'completed'),
(2, 'round_1', '여름방학의 특별한 하루', '2024-07-01 00:00:00', '2024-07-15 23:59:59', 'completed'),
(2, 'round_2', '바다에서의 모험', '2024-07-16 00:00:00', '2024-07-31 23:59:59', 'ongoing'),
(3, 'round_1', '우리 가족의 독서 시간', '2024-05-01 00:00:00', '2024-05-15 23:59:59', 'completed'),
(3, 'final', '가족과 함께한 최고의 순간', '2024-05-20 00:00:00', '2024-05-31 23:59:59', 'completed'),
(6, 'round_1', '지구를 지키는 작은 실천', '2024-04-01 00:00:00', '2024-04-15 23:59:59', 'completed'),
(6, 'final', '환경보호 실천 다짐', '2024-04-20 00:00:00', '2024-04-30 23:59:59', 'completed'),
(7, 'round_1', '친구와의 소중한 추억', '2024-06-01 00:00:00', '2024-06-15 23:59:59', 'completed'),
(7, 'final', '영원한 우정을 약속하며', '2024-06-20 00:00:00', '2024-06-30 23:59:59', 'completed');

-- Insert Share Boards (10 rows)
INSERT INTO `share_board` (`user_id`, `category_id`, `image_id`, `share_status`, `title`, `content`, `address`, `datetime`, `price`, `book_status`) VALUES
(2, 1, 1, 'SHARING', '곰돌이 푸 그림책 나눔', '아이가 다 커서 더 이상 안 봐요. 상태 좋습니다!', '서울시 서초구 서초대로 456', '2024-06-01 14:00:00', 0, 'A'),
(3, 2, 2, 'RESERVED', '해리포터 1-3권 세트', '1-3권 세트로 나눔합니다. 약간의 사용감 있어요.', '경기도 성남시 분당구 판교역로 789', '2024-06-05 15:00:00', 5000, 'B'),
(4, 3, 3, 'COMPLETED', '위인전 시리즈 10권', '전집 나눔합니다. 거의 새 책처럼 깨끗해요.', '부산시 해운대구 센텀중앙로 101', '2024-05-20 10:00:00', 10000, 'A'),
(5, 4, 4, 'SHARING', '과학 도서 5권 묶음', '초등 고학년용 과학책 나눔합니다.', '대구시 수성구 동대구로 202', '2024-06-10 16:00:00', 0, 'B'),
(6, 5, 5, 'SHARING', '역사 만화책 전집', '재미있는 한국사 만화책 전집 나눔', '인천시 연수구 송도과학로 303', '2024-06-12 11:00:00', 15000, 'A'),
(7, 6, 6, 'RESERVED', '예술 그림책 모음', '명화를 소개하는 그림책 5권', '광주시 서구 상무대로 404', '2024-06-15 13:00:00', 3000, 'B'),
(8, 7, 7, 'SHARING', '자연관찰 도감', '곤충, 식물 도감 세트입니다.', '대전시 유성구 대학로 505', '2024-06-18 14:30:00', 0, 'A'),
(9, 8, 8, 'COMPLETED', '창작동화 베스트 10', '아이들이 좋아하는 창작동화 모음', '울산시 남구 삼산로 606', '2024-05-25 09:00:00', 8000, 'B'),
(2, 9, 9, 'SHARING', '전래동화 그림책', '한국 전래동화 그림책 10권', '서울시 강남구 테헤란로 123', '2024-06-20 15:00:00', 5000, 'C'),
(3, 10, 10, 'SHARING', '영어 그림책 세트', '영어 학습용 그림책 8권 세트', '경기도 성남시 분당구 판교역로 789', '2024-06-22 10:00:00', 7000, 'A');

-- Insert Replies (10 rows)
INSERT INTO `reply` (`board_id`, `user_id`, `content`) VALUES
(1, 3, '저희 아이도 곰돌이 푸 정말 좋아해요!'),
(1, 4, '몇 살 아이가 읽기 좋을까요?'),
(2, 5, '초등 입학 전에는 한글 동화책이 좋을 것 같아요.'),
(2, 6, '저는 전래동화를 많이 읽혔어요. 추천합니다!'),
(3, 7, '해리포터 다음엔 나니아 연대기 어떠세요?'),
(4, 8, '나눔 행사 정보 어디서 확인할 수 있나요?'),
(5, 2, '대단하세요! 저도 시작해봐야겠어요.'),
(6, 3, '우주 탐험대 시리즈 추천드려요!'),
(7, 4, '유용한 정보 감사합니다!'),
(8, 5, 'Brown Bear 시리즈 정말 좋아요!');

-- Insert Contest Results (10 rows)
INSERT INTO `contest_result` (`contest_id`, `title`, `final_content`, `cover_image`) VALUES
(1, '봄날의 벚꽃나무 아래서', '따뜻한 봄날, 벚꽃나무 아래에서 가족과 함께 책을 읽던 그 순간이 가장 행복했습니다...', '/images/results/spring_winner.jpg'),
(3, '우리 가족의 일요일', '매주 일요일 아침, 온 가족이 모여 함께 책을 읽는 시간은 우리 가족만의 특별한 전통입니다...', '/images/results/family_winner.jpg'),
(6, '작은 실천으로 지키는 지구', '일회용품 대신 텀블러를 사용하고, 쓰레기를 줍는 작은 실천이 지구를 살립니다...', '/images/results/environment_winner.jpg'),
(7, '영원한 내 짝꿍', '유치원 때부터 함께한 내 친구, 우리는 책을 통해 더 깊은 우정을 나눴습니다...', '/images/results/friendship_winner.jpg'),
(1, '봄을 닮은 이야기', '개나리, 진달래, 벚꽃이 피어나는 봄처럼 우리들의 꿈도 활짝 피어납니다...', '/images/results/spring_runner1.jpg'),
(3, '책으로 하나되는 가족', '책을 읽으며 서로의 생각을 나누는 시간, 우리 가족을 더욱 가깝게 만들어줍니다...', '/images/results/family_runner1.jpg'),
(6, '지구를 위한 우리들의 약속', '플라스틱 사용을 줄이고, 분리수거를 열심히 하겠다는 우리들의 약속...', '/images/results/environment_runner1.jpg'),
(7, '함께 읽고 함께 자라는 우정', '같은 책을 읽고 이야기를 나누며 우리의 우정도 함께 자랐습니다...', '/images/results/friendship_runner1.jpg'),
(1, '봄 소풍의 추억', '봄 소풍 때 가져간 책 한 권, 친구들과 돌려 읽으며 만든 소중한 추억...', '/images/results/spring_runner2.jpg'),
(3, '책 읽는 우리 집', '거실 책장 가득한 책들, 각자 좋아하는 책을 읽으며 함께하는 우리 가족...', '/images/results/family_runner2.jpg');

-- Insert Dialogue Questions (10 rows)
INSERT INTO `dialogue_question` (`dialog_id`, `question`) VALUES
(1, '푸가 가장 좋아하는 것은 무엇인가요?'),
(1, '백 에이커 숲에는 누가 살고 있나요?'),
(2, '해리포터가 다니는 학교 이름은 무엇인가요?'),
(2, '해리의 가장 친한 친구들은 누구인가요?'),
(3, '세종대왕이 만든 것은 무엇인가요?'),
(4, '우리 태양계에는 몇 개의 행성이 있나요?'),
(5, '이 책에서 가장 인상 깊었던 역사적 사건은 무엇인가요?'),
(6, '가장 좋아하는 그림은 무엇이었나요?'),
(7, '숲속에서 만난 동물들 중 기억에 남는 친구는 누구인가요?'),
(8, '마법의 정원에서 일어난 신기한 일은 무엇인가요?');

-- Insert Stories (10 rows)
INSERT INTO `story` (`details_id`, `user_id`, `content`, `vote_count`) VALUES
(1, 2, '푸와 함께하는 모험은 정말 즐거웠어요. 꿀을 찾아다니는 푸의 모습이 귀여웠습니다.', 15),
(2, 3, '호그와트에서의 첫 날, 해리는 새로운 친구들을 만나며 마법의 세계에 적응해갔습니다.', 23),
(3, 4, '세종대왕의 한글 창제 이야기를 읽으며 우리 글의 소중함을 느꼈습니다.', 18),
(4, 5, '우주의 신비로운 이야기에 푹 빠졌어요. 별들 사이를 여행하는 상상을 했습니다.', 12),
(5, 6, '역사 속 위대한 인물들의 이야기를 통해 많은 것을 배웠습니다.', 9),
(6, 7, '아름다운 그림들을 보며 예술의 세계에 빠져들었어요.', 7),
(7, 8, '숲속 친구들과 함께하는 모험 이야기가 정말 재미있었어요.', 14),
(8, 9, '마법의 정원에서 일어나는 일들이 마치 꿈같았습니다.', 11),
(1, 2, '푸와 피글렛의 우정 이야기가 감동적이었어요.', 20),
(2, 3, '해리와 론, 헤르미온느의 모험은 계속됩니다!', 25);

-- Insert Share Requests (10 rows)
INSERT INTO `share_request` (`user_id`, `share_id`, `content`, `result_status`) VALUES
(4, 1, '아이가 곰돌이 푸를 정말 좋아해서 신청합니다. 소중히 읽겠습니다.', 'approved'),
(5, 2, '해리포터 시리즈 시작하려는데 꼭 읽고 싶어요!', 'approved'),
(6, 3, '위인전 전집 찾고 있었는데 감사합니다.', 'approved'),
(7, 4, '과학에 관심 많은 아이를 위해 신청합니다.', 'pending'),
(8, 5, '한국사 공부에 도움이 될 것 같아요.', 'pending'),
(9, 6, '예술 교육에 관심이 많아서 신청합니다.', 'approved'),
(2, 7, '자연 관찰 수업에 활용하려고 합니다.', 'pending'),
(3, 8, '아이들이 창작동화를 좋아해서 신청해요.', 'approved'),
(4, 9, '전래동화로 한국 문화를 알려주고 싶어요.', 'pending'),
(5, 10, '영어 학습 시작하는 아이를 위해 신청합니다.', 'pending');

-- Insert Dialogue Answers (10 rows)
INSERT INTO `dialogue_answer` (`dialog_id`, `reader_id`, `question_id`, `answer`) VALUES
(1, 1, 1, '푸는 꿀을 가장 좋아해요!'),
(1, 2, 2, '백 에이커 숲에는 푸, 피글렛, 티거, 이요르 등이 살아요.'),
(2, 3, 3, '호그와트 마법학교예요.'),
(2, 4, 4, '론 위즐리와 헤르미온느 그레인저입니다.'),
(3, 5, 5, '한글을 만드셨어요.'),
(4, 6, 6, '8개의 행성이 있어요.'),
(5, 7, 7, '세종대왕의 한글 창제가 가장 인상 깊었어요.'),
(6, 8, 8, '모나리자가 가장 좋았어요.'),
(7, 9, 9, '다람쥐 친구가 가장 기억에 남아요.'),
(8, 10, 10, '씨앗에서 순식간에 꽃이 피어나는 마법이 신기했어요.');

-- Insert Votes (10 rows)
INSERT INTO `vote` (`story_id`, `user_id`) VALUES
(1, 3),
(1, 4),
(1, 5),
(2, 4),
(2, 5),
(2, 6),
(3, 5),
(3, 6),
(4, 6),
(5, 7);

-- Insert Result Images (10 rows)
INSERT INTO `result_images` (`result_id`, `image_url`, `image_order`) VALUES
(1, '/images/results/spring_winner_1.jpg', 1),
(1, '/images/results/spring_winner_2.jpg', 2),
(2, '/images/results/family_winner_1.jpg', 1),
(2, '/images/results/family_winner_2.jpg', 2),
(3, '/images/results/environment_winner_1.jpg', 1),
(3, '/images/results/environment_winner_2.jpg', 2),
(4, '/images/results/friendship_winner_1.jpg', 1),
(4, '/images/results/friendship_winner_2.jpg', 2),
(5, '/images/results/spring_runner1_1.jpg', 1),
(6, '/images/results/family_runner1_1.jpg', 1);