-- community_db 社区便民维护管理系统 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS community_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE community_db;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    building VARCHAR(32) DEFAULT NULL COMMENT '楼栋号',
    room_no VARCHAR(32) DEFAULT NULL COMMENT '房间号',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0-居民，1-维修工，3-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 维修分类表
DROP TABLE IF EXISTS repair_category;
CREATE TABLE repair_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(64) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修分类表';

-- 维修工单表
DROP TABLE IF EXISTS repair_order;
CREATE TABLE repair_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '工单编号',
    user_id BIGINT NOT NULL COMMENT '报修用户ID',
    category_id BIGINT DEFAULT NULL COMMENT '分类ID',
    description TEXT COMMENT '问题描述',
    address VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    urgency VARCHAR(16) DEFAULT 'normal' COMMENT '紧急程度：low-低，normal-普通，high-高，urgent-紧急',
    images TEXT COMMENT '故障图片URL，逗号分隔',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待分配，1-已分配，2-处理中，3-待评价，4-已完成，5-已驳回',
    maintainer_id BIGINT DEFAULT NULL COMMENT '维修工ID',
    reject_reason VARCHAR(255) DEFAULT NULL COMMENT '驳回原因',
    result_desc TEXT COMMENT '处理结果描述',
    result_images TEXT COMMENT '处理结果图片',
    materials TEXT COMMENT '使用材料',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修工单表';

-- 维修工单日志表
DROP TABLE IF EXISTS repair_order_log;
CREATE TABLE repair_order_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '工单ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    action VARCHAR(64) NOT NULL COMMENT '操作动作',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修工单日志表';

-- 维修评价表
DROP TABLE IF EXISTS repair_evaluation;
CREATE TABLE repair_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '工单ID',
    user_id BIGINT NOT NULL COMMENT '评价用户ID',
    stars INT NOT NULL DEFAULT 5 COMMENT '评分：1-5',
    content TEXT COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修评价表';

-- 公告表
DROP TABLE IF EXISTS notice;
CREATE TABLE notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(128) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    type TINYINT DEFAULT 0 COMMENT '类型：0-普通公告，1-重要公告',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 插入默认数据：管理员（密码：123456，BCrypt加密）
INSERT INTO sys_user (id, username, password, nickname, role, status, create_time)
VALUES (1, 'admin', '$2a$10$N.zJ5Q5Q5Q5Q5Q5Q5Q5Q5Oe7v1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0', '系统管理员', 3, 1, NOW());
