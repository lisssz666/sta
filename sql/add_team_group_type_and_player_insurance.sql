-- ===========================================
-- 球队表新增字段：组别和人数
-- ===========================================

-- 组别：0-全部，1-邀请组，2-公开组
ALTER TABLE `sta_team` ADD COLUMN `group_type` TINYINT(1) DEFAULT 0 COMMENT '组别：0-全部，1-邀请组，2-公开组' AFTER `verify_code`;

-- 人数
ALTER TABLE `sta_team` ADD COLUMN `member_count` INT(11) DEFAULT 0 COMMENT '人数' AFTER `group_type`;

-- ===========================================
-- 球员表新增字段：保险证明
-- ===========================================

-- 保险证明（图片路径）
ALTER TABLE `sta_player` ADD COLUMN `insurance_certificate` VARCHAR(500) DEFAULT NULL COMMENT '保险证明（图片路径）' AFTER `phone_number`;
