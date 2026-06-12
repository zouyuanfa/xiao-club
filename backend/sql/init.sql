-- 创建数据库
CREATE DATABASE IF NOT EXISTS xiao_club DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE xiao_club;

-- 创建问卷表（JPA 会自动建表，此脚本仅供手动初始化使用）
CREATE TABLE IF NOT EXISTS survey (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name                    VARCHAR(50)     COMMENT '姓名',
    gender                  VARCHAR(10)     COMMENT '性别',
    age                     VARCHAR(20)     COMMENT '年龄段',
    phone                   VARCHAR(20)     COMMENT '手机号（前3****后4）',
    advisor                 VARCHAR(50)     COMMENT '置业顾问',
    living_area             VARCHAR(50)     COMMENT '居住区域',
    work_area               VARCHAR(50)     COMMENT '工作区域',
    industry                VARCHAR(50)     COMMENT '所属行业',
    occupation              VARCHAR(50)     COMMENT '职业',
    floor_area              VARCHAR(30)     COMMENT '目前居住户型面积',
    preferred_area          VARCHAR(30)     COMMENT '意向户型面积',
    unit_layout_preference  VARCHAR(200)    COMMENT '意向户型结构（多选逗号分隔）',
    purchase_funds          VARCHAR(30)     COMMENT '购房资金',
    property_purchase_count VARCHAR(30)     COMMENT '置业次数',
    accreditation_metrics   VARCHAR(500)    COMMENT '认可点（多选逗号分隔）',
    tracked_items           VARCHAR(500)    COMMENT '关注项目（多选逗号分隔）',
    master_plan_review      VARCHAR(30)     COMMENT '是否了解CID规划',
    xichuan_campus_layout   VARCHAR(30)     COMMENT '是否了解西川一校三区布局',
    xichuan_faculty         VARCHAR(30)     COMMENT '是否了解西川师资情况',
    event_interest          VARCHAR(500)    COMMENT '感兴趣活动（多选逗号分隔）',
    customer_interests      TEXT            COMMENT '建议',
    member_number           VARCHAR(50)     UNIQUE COMMENT '会员编号',
    created_at              DATETIME        COMMENT '创建时间',
    INDEX idx_member_number (member_number),
    INDEX idx_name (name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会员调查问卷';
