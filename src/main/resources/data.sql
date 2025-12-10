-- data.sql
-- Initial Data for Book Reading Application

-- 외래 키 체크 잠시 비활성화 (데이터 삽입 순서 문제 방지)
SET FOREIGN_KEY_CHECKS = 0;

-- 1. User Data
INSERT INTO `user` (`user_id`, `email`, `username`, `password`, `birth`, `phone`, `nickname`, `color`, `address`, `profile_img`, `role`, `provider`, `provider_id`, `reset_token`, `reset_token_expiry`)
VALUES
    (1,'admin1@admin.com','admin1','$2a$10$UX7LPes/mDVlBOlpoZRl/u/6wRLongxZVEBrJN4a6XGdBXxjqL5Km','2000-01-01','010-1111-1111','admin1','#FFFFFF','admin',NULL,'ADMIN','LOCAL',NULL,NULL,NULL),
    (2,'admin2@admin.com','admin2','$2a$10$rP.0wpQ5KDjGhqvAceh5YO.poPHgikyHNlmMaLMJ.2rtZ9LX.2XG.','2000-01-01','010-1111-1111','admin2','#FFFFFF','admin',NULL,'ADMIN','LOCAL',NULL,NULL,NULL),
    (3,'admin3@admin.com','admin3','$2a$10$tDI0SWtroMdOpduPIQd2zOKVnvCDzx1qK7KSo.ZzrsF6s4IQE5W66','2000-01-01','010-1111-1111','admin3','#FFFFFF','admin',NULL,'ADMIN','LOCAL',NULL,NULL,NULL);

-- 2. Subscription Plan Data
INSERT INTO `subscription_plan` (`plan_id`, `name`, `description`, `target_age`, `price`, `duration_days`, `is_active`)
VALUES
    (1, '영·유아 패키지', '우리 아이의 첫 독서 여정', '0-7세', 19900.00, 30, TRUE),
    (2, '초등·청소년 패키지', '생각의 깊이를 키우는 독서', '8-13세', 24900.00, 30, TRUE),
    (3, '부모 패키지', '부모의 성장이 자녀의 성장으로', '부모', 22900.00, 30, TRUE);

-- 외래 키 체크 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;