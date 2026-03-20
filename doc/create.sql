CREATE TABLE `sta_game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `home_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主队名称',
  `away_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客队名称',
  `homeid` bigint(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主队id',
  `awayid` bigint(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '客队id',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='比赛信息表';

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `sta_league_match` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `league_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联赛名称',
  `enroll_team` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '报名球队id',
  `host` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主办单位',
  `organizer` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '承办单位',
  `co_organizer` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '协办单位',
  `sponsor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '赞助单位',
  `title_sponsor` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冠名单位',
  `awayid` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `location` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联赛地点',

  `enroll_starttime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名开始时间',
  `enroll_endtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名结束时间',
  `game_starttime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '比赛开始时间',
  `game_endtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '比赛结束时间',

  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联赛信息表';

SET FOREIGN_KEY_CHECKS = 1;



CREATE TABLE `sta_player_statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `player_id` bigint(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '球员id',
  `compeid` bigint(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '比赛id',
  `score` int(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '得分',
  `backboard` int(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '篮板',
  `assist` int(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '助攻',
  `shoot_the_ball` int(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '两分球',
  `trisection` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '三分球',
  `free_throw` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '罚球',
  `tackle` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '抢断',
  `block_shot` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '快攻',
  `mistake` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '失误',
  `foul` int(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '犯规',
  `hit_rate` float COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '命中率',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球员统计表表';

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `sta_team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `team_title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '球队名',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='球队信息表';

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `sta_player` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `team_id` bigint(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属球队id',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '球员姓名/号码',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='运动员信息表';

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `sta_video` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `file_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名称',
  `file_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径',
  `create_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频录像文件表';

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `sta_live_person` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) DEFAULT NULL COMMENT '直播人员姓名',
  `avatar_url` varchar(500) DEFAULT NULL COMMENT '直播人员头像URL',
  `qualification` varchar(500) DEFAULT NULL COMMENT '直播资质描述',
  `friend_price` decimal(10,2) DEFAULT NULL COMMENT '亲友价（元/队）',
  `live_count` int(11) DEFAULT '0' COMMENT '直播场次总数',
  `content_desc` varchar(1000) DEFAULT NULL COMMENT '直播内容描述',
  `status` int(11) DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
  `is_hide` int(11) DEFAULT '0' COMMENT '是否隐藏：1-是 0-否',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除：0-未删 1-已删',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='直播人员信息表';