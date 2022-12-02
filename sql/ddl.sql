CREATE DATABASE IF NOT EXISTS dynamic_threadpool;

USE dynamic_threadpool;

# DROP TABLE IF EXISTS `dtp_server`;
CREATE TABLE IF NOT EXISTS `dtp_server`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_code`   VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '服务代码',
    `server_name`   VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '服务名称',
    `server_secret` VARCHAR(90)     NOT NULL DEFAULT '' COMMENT '服务密钥',
    `create_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY uk_server_code (`server_code`)
) ENGINE = INNODB COMMENT = '动态线程池-服务实例';

# DROP TABLE IF EXISTS `dtp_executor_statistics_history`;
CREATE TABLE IF NOT EXISTS `dtp_executor_statistics_history`
(
    `id`                             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `server_id`                      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '服务ID',
    `server_name`                    VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '服务名称',
    `executor_id`                    VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '线程池ID',
    `executor_name`                  VARCHAR(90)     NOT NULL DEFAULT '' COMMENT '线程池名字',
    `executor_queue_class`           VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '线程池队列类型',
    `executor_queue_node_count`      INT             NOT NULL DEFAULT 0 COMMENT '线程池队列节点数量',
    `executor_queue_remain_capacity` INT             NOT NULL DEFAULT 0 COMMENT '线程池队列剩余容量',
    `executor_pool_size`             INT             NOT NULL DEFAULT 0 COMMENT '线程池当前线程数量',
    `executor_largest_pool_size`     INT             NOT NULL DEFAULT 0 COMMENT '线程池曾经出现过的最大线程池数量',
    `executor_active_count`          INT             NOT NULL DEFAULT 0 COMMENT '线程池正在执行任务的线程数量',
    `executor_task_count`            BIGINT          NOT NULL DEFAULT 0 COMMENT '线程池已被调度执行的任务数量',
    `executor_completed_task_count`  BIGINT          NOT NULL DEFAULT 0 COMMENT '线程池已被执行完成的任务数量',
    `create_time`                    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`                    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY idx_server_id_executor_id (`server_id`, `executor_id`) USING BTREE
) ENGINE = INNODB COMMENT = '动态线程池-数据统计记录';

# DROP TABLE IF EXISTS `dtp_administrator`;
CREATE TABLE IF NOT EXISTS `dtp_administrator`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`     VARCHAR(45)     NOT NULL DEFAULT '' COMMENT '管理者账号',
    `password`    VARCHAR(90)     NOT NULL DEFAULT '' COMMENT '管理者密码',
    `username`    VARCHAR(90)     NOT NULL DEFAULT '' COMMENT '管理者用户名',
    `email`       VARCHAR(90)     NOT NULL DEFAULT '' COMMENT '管理者邮箱',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY idx_account (`account`) USING BTREE
) ENGINE = INNODB COMMENT = '动态线程池-管理者';
