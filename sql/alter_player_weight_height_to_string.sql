-- ===========================================
-- 球员表：身高和体重字段改为字符串格式
-- ===========================================

-- 将体重字段从 DOUBLE 改为 VARCHAR
ALTER TABLE `sta_player` MODIFY COLUMN `weight` VARCHAR(50) DEFAULT NULL COMMENT '体重（kg）';

-- 将身高中字段从 DOUBLE 改为 VARCHAR
ALTER TABLE `sta_player` MODIFY COLUMN `height` VARCHAR(50) DEFAULT NULL COMMENT '身高（cm）';
