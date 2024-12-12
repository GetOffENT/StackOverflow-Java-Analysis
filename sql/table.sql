CREATE DATABASE IF NOT EXISTS `stackoverflow` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `stackoverflow`;



DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `user_id`       bigint COMMENT '用户ID，唯一标识',
    `account_id`    bigint COMMENT 'StackOverflow账户ID',
    `reputation`    int unsigned COMMENT '用户声誉分数',
    `user_type`     varchar(50) COMMENT '用户类型（如：registered）',
    `accept_rate`   int unsigned COMMENT '用户接受率（0-100）',
    `profile_image` varchar(255) COMMENT '用户头像链接',
    `display_name`  varchar(255) COMMENT '用户显示名称',
    `link`          varchar(255) COMMENT '用户个人主页链接',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '用户信息表';


DROP TABLE IF exists `question`;
CREATE TABLE `question`
(
    `question_id`        bigint unsigned NOT NULL COMMENT '问题ID，唯一标识',
    `owner_user_id`      bigint COMMENT '提问者用户ID，逻辑外键',
    `title`              varchar(255) COMMENT '问题标题',
    `body`               text COMMENT '问题内容',
    `link`               varchar(255) COMMENT '问题的链接',
    `is_answered`        boolean COMMENT '是否有回答',
    `answer_count`       int unsigned COMMENT '问题的回答数量',
    `accepted_answer_id` bigint unsigned COMMENT '接受的回答ID',
    `comment_count`      int unsigned COMMENT '问题的评论数量',
    `view_count`         int unsigned COMMENT '问题的查看次数',
    `favorite_count`     int unsigned COMMENT '问题的收藏次数',
    `up_vote_count`      int unsigned COMMENT '问题的赞同次数',
    `down_vote_count`    int unsigned COMMENT '问题的反对次数',
    `score`              int COMMENT '问题的得分',
    `creation_date`      datetime COMMENT '问题创建时间',
    `last_activity_date` datetime COMMENT '问题的最后活动时间',
    `last_edit_date`     datetime COMMENT '问题的最后编辑时间',
    `protected_date`     datetime COMMENT '问题的保护时间',
    `content_license`    varchar(50) COMMENT '内容许可',
    PRIMARY KEY (`question_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '问题信息表';


DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer`
(
    `answer_id`            bigint unsigned NOT NULL COMMENT '回答ID，唯一标识',
    `question_id`          bigint unsigned COMMENT '问题ID，逻辑外键',
    `owner_user_id`        bigint COMMENT '回答者用户ID，逻辑外键',
    `body`                 text COMMENT '回答内容',
    `is_accepted`          boolean COMMENT '是否被接受',
    `up_vote_count`        int unsigned COMMENT '回答的赞同次数',
    `down_vote_count`      int unsigned COMMENT '回答的反对次数',
    `score`                int COMMENT '回答得分',
    `comment_count`        int unsigned COMMENT '回答的评论数量',
    `creation_date`        datetime COMMENT '回答创建时间',
    `last_activity_date`   datetime COMMENT '回答的最后活动时间',
    `last_edit_date`       datetime COMMENT '回答的最后编辑时间',
    `community_owned_date` datetime COMMENT '社区拥有的日期',
    `content_license`      varchar(50) COMMENT '内容许可',
    PRIMARY KEY (`answer_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '回答信息表';


DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`
(
    `comment_id`       bigint unsigned NOT NULL COMMENT '评论ID，唯一标识',
    `post_id`          bigint unsigned COMMENT '评论的目标（问题ID或回答ID）',
    `owner_user_id`    bigint COMMENT '评论者用户ID，逻辑外键',
    `reply_to_user_id` bigint COMMENT '回复的用户ID（可以为空），逻辑外键',
    `body`             text COMMENT '评论内容',
    `edited`           boolean COMMENT '是否已编辑',
    `score`            int COMMENT '评论得分',
    `creation_date`    datetime COMMENT '评论创建时间',
    `content_license`  varchar(50) COMMENT '内容许可',
    PRIMARY KEY (`comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '评论信息表';


DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`
(
    `tag_id`   bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID，唯一标识',
    `tag_name` varchar(50)     NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`tag_id`),
    UNIQUE KEY `unique_tag_name` (`tag_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '标签信息表';


DROP TABLE IF EXISTS `question_tag`;
CREATE TABLE `question_tag`
(
    `question_id` bigint unsigned NOT NULL COMMENT '问题ID，逻辑外键',
    `tag_id`      bigint unsigned NOT NULL COMMENT '标签ID，逻辑外键',
    PRIMARY KEY (`question_id`, `tag_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '问题标签关联表';
