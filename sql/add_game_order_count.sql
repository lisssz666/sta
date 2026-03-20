-- 添加约赛成单总场次字段
ALTER TABLE sta_team ADD COLUMN game_order_count INT DEFAULT 0 COMMENT '约赛成单总场次';

-- 更新现有数据的约赛成单总场次（根据已收单的约赛统计）
UPDATE sta_team t
SET t.game_order_count = (
    SELECT COUNT(DISTINCT og.id)
    FROM sta_organize_game og
    CROSS JOIN JSON_TABLE(og.teams, '$[*]' COLUMNS(
        teamId VARCHAR(255) PATH '$.teamId'
    )) AS teams
    WHERE og.status = 1 AND CAST(teams.teamId AS UNSIGNED) = t.id
);