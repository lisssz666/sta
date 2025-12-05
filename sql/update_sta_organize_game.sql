-- 更新球队约球信息表结构
-- 删除原有字段，添加新字段以支持前备注、球队数组和后备注的结构

-- 1. 创建临时表存储现有数据（可选，如需保留历史数据）
CREATE TABLE IF NOT EXISTS `sta_organize_game_backup` (
  `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '主键',
  `team_id`     bigint(20)   DEFAULT NULL COMMENT '球队id',
  `team_name`   varchar(64)  NOT NULL COMMENT '球队名称',
  `game_date`   date         DEFAULT NULL COMMENT '比赛日期',
  `game_time`   varchar(32)  DEFAULT NULL COMMENT '比赛时间段',
  `uniform`     varchar(32)  DEFAULT NULL COMMENT '球服颜色',
  `fee`         varchar(32)  DEFAULT NULL COMMENT '费用',
  `referee`     varchar(32)  DEFAULT NULL COMMENT '裁判',
  `level`       varchar(32)  DEFAULT NULL COMMENT '水平描述',
  `venue`       varchar(255) DEFAULT NULL COMMENT '场地',
  `contact`     varchar(255) DEFAULT NULL COMMENT '联系方式',
  `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
  `create_by`   varchar(64)  DEFAULT '' COMMENT '创建者',
  `create_time` timestamp    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   varchar(64)  DEFAULT '' COMMENT '更新者',
  `update_time` timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='球队约球信息表备份';

-- 2. 备份数据（可选）
INSERT INTO `sta_organize_game_backup` SELECT * FROM `sta_organize_game`;

-- 3. 修改表结构 - 删除原有字段
ALTER TABLE `sta_organize_game`
  DROP COLUMN `team_id`,
  DROP COLUMN `team_name`,
  DROP COLUMN `game_date`,
  DROP COLUMN `game_time`,
  DROP COLUMN `uniform`,
  DROP COLUMN `fee`,
  DROP COLUMN `referee`,
  DROP COLUMN `level`,
  DROP COLUMN `venue`,
  DROP COLUMN `contact`,
  DROP COLUMN `remark`;

-- 4. 修改表结构 - 添加新字段
ALTER TABLE `sta_organize_game`
  ADD COLUMN `pre_remark` varchar(500) DEFAULT '' COMMENT '前备注',
  ADD COLUMN `teams` JSON DEFAULT NULL COMMENT '球队数组(JSON格式存储)',
  ADD COLUMN `post_remark` varchar(500) DEFAULT '' COMMENT '后备注';

-- 5. 更新表注释
ALTER TABLE `sta_organize_game` COMMENT '球队约球信息表（包含前备注、球队数组和后备注）';

-- 注意：如果需要恢复历史数据，可以执行以下操作（根据实际需求调整）
-- INSERT INTO `sta_organize_game` (id, pre_remark, teams, post_remark, create_by, create_time, update_by, update_time)
-- SELECT 
--   id, 
--   '', 
--   JSON_OBJECT(
--     'teamId', team_id,
--     'teamName', team_name,
--     'gameDate', game_date,
--     'gameTime', game_time,
--     'uniform', uniform,
--     'fee', fee,
--     'referee', referee,
--     'level', level,
--     'venue', venue,
--     'contact', contact,
--     'remark', remark
--   ) AS teams,
--   '', 
--   create_by, 
--   create_time, 
--   update_by, 
--   update_time
-- FROM `sta_organize_game_backup`;