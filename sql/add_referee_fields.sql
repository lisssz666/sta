-- 在裁判信息表中添加执裁履历、综合评价、好评率字段
ALTER TABLE sta_referee_info ADD COLUMN referee_resume TEXT COMMENT '执裁履历';
ALTER TABLE sta_referee_info ADD COLUMN overall_rating VARCHAR(255) COMMENT '综合评价';
ALTER TABLE sta_referee_info ADD COLUMN praise_rate DOUBLE COMMENT '好评率';
