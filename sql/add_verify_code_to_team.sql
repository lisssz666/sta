-- 为球队表添加校验码字段
ALTER TABLE `sta_team` ADD COLUMN `verify_code` VARCHAR(100) DEFAULT NULL COMMENT '校验码' AFTER `game_order_count`;

-- 为校验码字段添加索引（可选，根据业务需求决定是否添加）
-- CREATE INDEX `idx_sta_team_verify_code` ON `sta_team`(`verify_code`);
