-- ========================================
-- 初始化数据库表和测试数据
-- 包含：比赛表、直播表、裁判表、商品表、订单功能表
-- 创建时间：2026-05-13
-- ========================================

-- ========================================
-- 1. 比赛表 (sta_game) - 如果表已存在则更新
-- ========================================
-- 添加缺失的字段（如果不存在）
ALTER TABLE sta_game
ADD COLUMN IF NOT EXISTS league_name varchar(100) DEFAULT NULL COMMENT '所属联赛名称',
ADD COLUMN IF NOT EXISTS league_logo_path varchar(255) DEFAULT NULL COMMENT '联赛logo',
ADD COLUMN IF NOT EXISTS game_addr_id bigint DEFAULT NULL COMMENT '比赛场地id',
ADD COLUMN IF NOT EXISTS venue_name varchar(100) DEFAULT NULL COMMENT '比赛球馆名称',
ADD COLUMN IF NOT EXISTS location varchar(100) DEFAULT NULL COMMENT '比赛场地地区',
ADD COLUMN IF NOT EXISTS game_addr varchar(255) DEFAULT NULL COMMENT '比赛详细地址',
ADD COLUMN IF NOT EXISTS home_team_logo varchar(255) DEFAULT NULL COMMENT '主队队徽',
ADD COLUMN IF NOT EXISTS away_team_logo varchar(255) DEFAULT NULL COMMENT '客队队徽';

-- 比赛测试数据
INSERT INTO sta_game (home_name, away_name, game_type, homeid, awayid, hteam_score, vteam_score, league_id, league_name, league_logo_path, playing_time, game_round, game_addr_id, venue_name, location, game_addr, game_status, game_stage, home_color, away_color, home_paused, away_paused, home_fouls, away_fouls, home_team_logo, away_team_logo, hsessions_score, asessions_score, create_time, update_time, remark) VALUES
('北京首钢', '广东宏远', 'CBA', 1, 2, 95, 98, 1, 'CBA联赛', '/uploads/league/cba.png', '2026-05-20 19:30:00', '第15轮', 1, '五棵松体育馆', '北京市海淀区', '北京市海淀区复兴路69号', 0, '常规赛', '红色', '蓝色', 2, 3, 8, 10, '/uploads/team/beijing.png', '/uploads/team/guangdong.png', '25,30,20,20', '28,28,22,20', NOW(), NOW(), '焦点战'),
('辽宁本钢', '新疆广汇', 'CBA', 3, 4, 102, 99, 1, 'CBA联赛', '/uploads/league/cba.png', '2026-05-21 19:30:00', '第16轮', 2, '辽宁体育馆', '辽宁省沈阳市', '辽宁省沈阳市浑南区浑南中路100号', 0, '常规赛', '蓝白色', '红色', 1, 2, 5, 6, '/uploads/team/liaoning.png', '/uploads/team/xinjiang.png', '30,25,22,25', '28,24,23,24', NOW(), NOW(), NULL),
('上海久事', '浙江稠州', 'CBA', 5, 6, 88, 92, 1, 'CBA联赛', '/uploads/league/cba.png', '2026-05-22 19:30:00', '第17轮', 3, '梅赛德斯-奔驰文化中心', '上海市浦东新区', '上海市浦东新区世博大道1200号', 0, '常规赛', '白色', '绿色', 3, 2, 7, 8, '/uploads/team/shanghai.png', '/uploads/team/zhejiang.png', '22,20,23,23', '25,22,22,23', NOW(), NOW(), NULL);

-- ========================================
-- 2. 裁判表 (sta_referee_info) - 如果表已存在则更新
-- ========================================
-- 添加缺失的字段（如果不存在）
ALTER TABLE sta_referee_info
ADD COLUMN IF NOT EXISTS avatar_path varchar(255) DEFAULT NULL COMMENT '头像路径',
ADD COLUMN IF NOT EXISTS referee_resume text COMMENT '执裁履历',
ADD COLUMN IF NOT EXISTS overall_rating varchar(10) DEFAULT NULL COMMENT '综合评价',
ADD COLUMN IF NOT EXISTS praise_rate double DEFAULT NULL COMMENT '好评率',
ADD COLUMN IF NOT EXISTS is_hide tinyint(1) DEFAULT 0 COMMENT '是否隐藏 0否 1是';

-- 裁判测试数据
INSERT INTO sta_referee_info (name, avatar_path, level, cert_date, reg_unit, signature, status, remark, referee_years, price, order_count, referee_resume, overall_rating, praise_rate, schedule_log, is_hide, create_time, update_time) VALUES
('张伟', '/uploads/referee/avatar1.jpg', '国家级裁判', '2018-06-15', '中国篮球协会', 'zhangwei', '1', '资深裁判，经验丰富', 15, '500.00', 120, '2018-2026年执裁CBA联赛超过500场比赛', '5.0', 98.5, '[{\"date\":\"2026-05-20\",\"available\":true},{\"date\":\"2026-05-21\",\"available\":false}]', 0, NOW(), NOW()),
('李明', '/uploads/referee/avatar2.jpg', '国际级裁判', '2015-03-20', '国际篮联', 'liming', '1', '国际级裁判，执裁过奥运会', 20, '800.00', 200, '2015-2026年执裁国际比赛超过300场', '4.9', 99.2, '[{\"date\":\"2026-05-20\",\"available\":true},{\"date\":\"2026-05-21\",\"available\":true}]', 0, NOW(), NOW()),
('王强', '/uploads/referee/avatar3.jpg', '国家级裁判', '2019-08-10', '中国篮球协会', 'wangqiang', '1', '年轻裁判，潜力巨大', 8, '400.00', 80, '2019-2026年执裁CBA联赛超过200场比赛', '4.8', 97.5, '[{\"date\":\"2026-05-20\",\"available\":false},{\"date\":\"2026-05-21\",\"available\":true}]', 0, NOW(), NOW());

-- ========================================
-- 3. 直播表 (sta_live_person) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `sta_live_person`;
CREATE TABLE `sta_live_person` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '直播人员姓名',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '直播人员头像URL',
  `qualification` varchar(200) DEFAULT NULL COMMENT '直播资质描述',
  `friend_price` decimal(10,2) DEFAULT NULL COMMENT '亲友价（元/队）',
  `live_count` int DEFAULT '0' COMMENT '直播场次总数',
  `content_desc` varchar(500) DEFAULT NULL COMMENT '直播内容描述',
  `status` int DEFAULT '1' COMMENT '直播人员状态 0-禁用 1-启用',
  `is_hide` tinyint(1) DEFAULT '0' COMMENT '是否隐藏 0否 1是',
  `schedule_log` text COMMENT '日程日志(JSON格式)',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播人员信息表';

-- 直播测试数据
INSERT INTO sta_live_person (name, avatar_url, qualification, friend_price, live_count, content_desc, status, is_hide, schedule_log, deleted, create_time, update_time) VALUES
('刘洋', '/uploads/live/avatar1.jpg', '资深直播数据员，10年经验', 300.00, 350, '全场直播、统计全队数据、个人集锦', 1, 0, '[{\"date\":\"2026-05-20\",\"available\":true},{\"date\":\"2026-05-21\",\"available\":true}]', 0, NOW(), NOW()),
('陈静', '/uploads/live/avatar2.jpg', '专业直播解说员，CBA官方认证', 400.00, 280, '专业解说、实时数据分析、精彩回放', 1, 0, '[{\"date\":\"2026-05-20\",\"available\":false},{\"date\":\"2026-05-21\",\"available\":true}]', 0, NOW(), NOW()),
('赵磊', '/uploads/live/avatar3.jpg', '新媒体直播达人，粉丝10万+', 250.00, 150, '趣味解说、互动直播、短视频制作', 1, 0, '[{\"date\":\"2026-05-20\",\"available\":true},{\"date\":\"2026-05-21\",\"available\":false}]', 0, NOW(), NOW());

-- ========================================
-- 4. 商位表 (mall_merchant) - 如果表已存在则更新
-- ========================================
-- 添加缺失的字段（如果不存在）
ALTER TABLE mall_merchant
ADD COLUMN IF NOT EXISTS is_hide int DEFAULT 0 COMMENT '是否隐藏 1是 0否',
ADD COLUMN IF NOT EXISTS merchant_logo varchar(255) DEFAULT NULL COMMENT '商位logo',
ADD COLUMN IF NOT EXISTS delivery_type varchar(50) DEFAULT NULL COMMENT '配送方式：到店/外卖/其他',
ADD COLUMN IF NOT EXISTS deleted int DEFAULT 0 COMMENT '逻辑删除 0未删1已删';

-- 商位测试数据
INSERT INTO mall_merchant (name, address, address_abbr, phone, open_time, status, is_hide, cover_img, score, comment_num, category, distance_km, parking, open_day, rank_text, delivery_type, merchant_logo, deleted, create_time, update_time) VALUES
('运动装备专营店', '北京市朝阳区体育中心B1层', '体育中心B1', '010-88888888', '09:00-21:00', 1, 0, '/uploads/merchant/cover1.jpg', '4.8', 256, '运动装备', '1.2', '1', '0', '运动装备销量冠军', '到店/外卖', '/uploads/merchant/logo1.jpg', 0, NOW(), NOW()),
('篮球用品旗舰店', '上海市浦东新区陆家嘴', '陆家嘴', '021-66666666', '10:00-22:00', 1, 0, '/uploads/merchant/cover2.jpg', '4.9', 389, '篮球用品', '2.5', '1', '0', '篮球用品TOP1', '到店', '/uploads/merchant/logo2.jpg', 0, NOW(), NOW()),
('运动服饰精品店', '广州市天河区体育西路', '体育西路', '020-77777777', '09:00-20:00', 1, 0, '/uploads/merchant/cover3.jpg', '4.7', 180, '运动服饰', '0.8', '0', '0', '运动服饰精选', '外卖', '/uploads/merchant/logo3.jpg', 0, NOW(), NOW());

-- ========================================
-- 5. 商品表 (mall_product) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `mall_product`;
CREATE TABLE `mall_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint NOT NULL COMMENT '商位ID',
  `name` varchar(200) NOT NULL COMMENT '商品名称',
  `cover_img` varchar(255) DEFAULT NULL COMMENT '商品封面图',
  `price` varchar(20) NOT NULL COMMENT '原价',
  `discount_price` varchar(20) DEFAULT NULL COMMENT '折扣价',
  `month_sales` int DEFAULT '0' COMMENT '月销量',
  `status` int DEFAULT '1' COMMENT '商品状态 0下架 1上架',
  `slogan` varchar(200) DEFAULT NULL COMMENT '商品标语',
  `sales_tag` varchar(100) DEFAULT NULL COMMENT '销量标签',
  `stock` varchar(20) DEFAULT NULL COMMENT '库存（字符串）',
  `discount` varchar(20) DEFAULT NULL COMMENT '折扣系数（字符串）',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0未删 1已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品测试数据
INSERT INTO mall_product (merchant_id, name, cover_img, price, discount_price, month_sales, status, slogan, sales_tag, stock, discount, deleted, create_time, update_time) VALUES
(1, '专业篮球鞋-乔丹系列', '/uploads/product/shoe1.jpg', '1299.00', '999.00', 580, 1, 'NBA同款，舒适透气', '月销500+', '200', '0.77', 0, NOW(), NOW()),
(1, '篮球护膝-专业级', '/uploads/product/knee1.jpg', '199.00', '149.00', 320, 1, '保护膝盖，运动必备', '月销300+', '500', '0.75', 0, NOW(), NOW()),
(2, '篮球-斯伯丁TF-1000', '/uploads/product/ball1.jpg', '399.00', '299.00', 450, 1, 'NBA官方用球', '月销400+', '300', '0.75', 0, NOW(), NOW()),
(2, '运动T恤-透气速干', '/uploads/product/shirt1.jpg', '159.00', '99.00', 680, 1, '吸湿排汗，清爽舒适', '月销600+', '800', '0.62', 0, NOW(), NOW()),
(3, '运动毛巾-超吸水', '/uploads/product/towel1.jpg', '49.00', '29.00', 890, 1, '快速吸水，柔软亲肤', '月销800+', '1000', '0.59', 0, NOW(), NOW()),
(3, '运动水壶-大容量', '/uploads/product/bottle1.jpg', '89.00', '59.00', 520, 1, '1.5L大容量，防漏设计', '月销500+', '600', '0.66', 0, NOW(), NOW());

-- ========================================
-- 6. 裁判订单表 (mall_referee_order) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `mall_referee_order`;
CREATE TABLE `mall_referee_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号（格式：RF+时间戳）',
  `referee_id` bigint NOT NULL COMMENT '裁判ID',
  `referee_name` varchar(50) DEFAULT NULL COMMENT '裁判姓名',
  `referee_level` varchar(50) DEFAULT NULL COMMENT '裁判等级',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `match_info` varchar(200) DEFAULT NULL COMMENT '比赛信息',
  `match_time` varchar(50) DEFAULT NULL COMMENT '比赛时间',
  `match_location` varchar(200) DEFAULT NULL COMMENT '比赛地点',
  `total_amount` varchar(20) NOT NULL COMMENT '订单总金额（单位：元）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` int DEFAULT '0' COMMENT '订单状态 0-未付款 1-已付款 2-已取消 3-已完成',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '微信支付官方订单号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='裁判订单表';

-- 裁判订单测试数据
INSERT INTO mall_referee_order (order_no, referee_id, referee_name, referee_level, contact_name, contact_phone, match_info, match_time, match_location, total_amount, remark, status, transaction_id, pay_time, deleted, create_time, update_time) VALUES
('RF20260513001', 1, '张伟', '国家级裁判', '李华', '13800138001', '北京首钢 vs 广东宏远', '2026-05-20 19:30:00', '五棵松体育馆', '500.00', '请提前30分钟到场', 1, 'wx123456789', NOW(), 0, NOW(), NOW()),
('RF20260513002', 2, '李明', '国际级裁判', '王芳', '13900139002', '辽宁本钢 vs 新疆广汇', '2026-05-21 19:30:00', '辽宁体育馆', '800.00', NULL, 0, NULL, NULL, 0, NOW(), NOW()),
('RF20260513003', 3, '王强', '国家级裁判', '张强', '13700137003', '上海久事 vs 浙江稠州', '2026-05-22 19:30:00', '梅赛德斯-奔驰文化中心', '400.00', '需要提供专业设备', 3, 'wx987654321', NOW(), 0, NOW(), NOW());

-- ========================================
-- 7. 直播订单表 (mall_live_person_order) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `mall_live_person_order`;
CREATE TABLE `mall_live_person_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号（格式：LP+时间戳）',
  `live_person_id` bigint NOT NULL COMMENT '直播人员ID',
  `live_person_name` varchar(50) DEFAULT NULL COMMENT '直播人员姓名',
  `qualification` varchar(200) DEFAULT NULL COMMENT '直播人员资质描述',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `match_info` varchar(200) DEFAULT NULL COMMENT '比赛信息',
  `match_time` varchar(50) DEFAULT NULL COMMENT '比赛时间',
  `match_location` varchar(200) DEFAULT NULL COMMENT '比赛地点',
  `total_amount` varchar(20) NOT NULL COMMENT '订单总金额（单位：元）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` int DEFAULT '0' COMMENT '订单状态 0-未付款 1-已付款 2-已取消 3-已完成',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '微信支付官方订单号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播订单表';

-- 直播订单测试数据
INSERT INTO mall_live_person_order (order_no, live_person_id, live_person_name, qualification, contact_name, contact_phone, match_info, match_time, match_location, total_amount, remark, status, transaction_id, pay_time, deleted, create_time, update_time) VALUES
('LV20260513001', 1, '刘洋', '资深直播数据员，10年经验', '李华', '13800138001', '北京首钢 vs 广东宏远', '2026-05-20 19:30:00', '五棵松体育馆', '300.00', '需要全程直播', 1, 'wx111222333', NOW(), 0, NOW(), NOW()),
('LV20260513002', 2, '陈静', '专业直播解说员，CBA官方认证', '王芳', '13900139002', '辽宁本钢 vs 新疆广汇', '2026-05-21 19:30:00', '辽宁体育馆', '400.00', NULL, 0, NULL, NULL, 0, NOW(), NOW()),
('LV20260513003', 3, '赵磊', '新媒体直播达人，粉丝10万+', '张强', '13700137003', '上海久事 vs 浙江稠州', '2026-05-22 19:30:00', '梅赛德斯-奔驰文化中心', '250.00', '需要短视频制作', 2, 'wx444555666', NOW(), 0, NOW(), NOW());

-- ========================================
-- 8. 商品订单表 (mall_order) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `mall_order`;
CREATE TABLE `mall_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `merchant_id` bigint NOT NULL COMMENT '商位ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号（格式：YL+时间戳）',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_amount` varchar(20) NOT NULL COMMENT '订单总金额（单位：元）',
  `address` varchar(500) DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '收货电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` int DEFAULT '0' COMMENT '订单状态 0-未付款 1-已付款 2-商家已接单 3-正在配送 4-已完成',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '微信支付官方订单号',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品订单表';

-- 商品订单测试数据
INSERT INTO mall_order (merchant_id, order_no, user_id, total_amount, address, phone, remark, status, transaction_id, pay_time, deleted, create_time, update_time) VALUES
(1, 'YL20260513001', 9527, '1098.00', '北京市朝阳区建国路88号', '13800138001', '请尽快发货', 1, 'wx777888999', NOW(), 0, NOW(), NOW()),
(2, 'YL20260513002', 9528, '398.00', '上海市浦东新区陆家嘴环路1000号', '13900139002', NULL, 0, NULL, NULL, 0, NOW(), NOW()),
(3, 'YL20260513003', 9529, '88.00', '广州市天河区体育西路188号', '13700137003', '周末配送', 4, 'wx000111222', NOW(), 0, NOW(), NOW());

-- ========================================
-- 9. 商品订单明细表 (mall_order_item) - 创建新表
-- ========================================
DROP TABLE IF EXISTS `mall_order_item`;
CREATE TABLE `mall_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `quantity` int NOT NULL COMMENT '购买数量',
  `unit_price` varchar(20) NOT NULL COMMENT '单价',
  `total_price` varchar(20) NOT NULL COMMENT '小计',
  `deleted` int DEFAULT '0' COMMENT '逻辑删除 0-未删 1-已删',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品订单明细表';

-- 商品订单明细测试数据
INSERT INTO mall_order_item (order_id, product_id, product_name, quantity, unit_price, total_price, deleted, create_time, update_time) VALUES
(1, 1, '专业篮球鞋-乔丹系列', 1, '999.00', '999.00', 0, NOW(), NOW()),
(2, 1, '篮球护膝-专业级', 1, '99.00', '99.00', 0, NOW(), NOW()),
(3, 2, '篮球-斯伯丁TF-1000', 1, '299.00', '299.00', 0, NOW(), NOW()),
(4, 2, '运动毛巾-超吸水', 1, '29.00', '29.00', 0, NOW(), NOW()),
(5, 2, '运动水壶-大容量', 1, '59.00', '59.00', 0, NOW(), NOW()),
(6, 3, '运动T恤-透气速干', 1, '99.00', '99.00', 0, NOW(), NOW());

-- ========================================
-- 初始化完成
-- ========================================