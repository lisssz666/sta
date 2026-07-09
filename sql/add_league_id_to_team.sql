-- ===========================================
-- 球队表新增字段：联赛ID
-- ===========================================

-- 联赛ID
ALTER TABLE `sta_team` ADD COLUMN `league_id` BIGINT(20) DEFAULT NULL COMMENT '联赛ID' AFTER `member_count`;

-- 为联赛ID添加索引（提高查询效率）
CREATE INDEX `idx_sta_team_league_id` ON `sta_team`(`league_id`);
