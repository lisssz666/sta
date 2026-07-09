-- ========================================
-- 为订单表添加用户ID字段（用户隔离功能）
-- ========================================

-- 1. 为裁判订单表添加 user_id 字段
ALTER TABLE mall_referee_order ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT '用户ID' AFTER `order_no`;

-- 2. 为直播订单表添加 user_id 字段
ALTER TABLE mall_live_person_order ADD COLUMN `user_id` bigint DEFAULT NULL COMMENT '用户ID' AFTER `order_no`;

-- 3. 商品订单表（mall_order）已有 user_id 字段，无需添加

-- ========================================
-- 添加索引以优化查询性能
-- ========================================

-- 为裁判订单表的 user_id 字段添加索引
CREATE INDEX idx_mall_referee_order_user_id ON mall_referee_order(user_id);

-- 为直播订单表的 user_id 字段添加索引
CREATE INDEX idx_mall_live_order_user_id ON mall_live_person_order(user_id);

-- 为商品订单表的 user_id 字段添加索引（如果尚未存在）
CREATE INDEX IF NOT EXISTS idx_mall_order_user_id ON mall_order(user_id);
