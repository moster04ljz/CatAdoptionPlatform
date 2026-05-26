-- 流浪猫领养平台数据库
CREATE DATABASE IF NOT EXISTS cat_adoption DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cat_adoption;

-- 猫咪表
CREATE TABLE IF NOT EXISTS cat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '名字',
    breed VARCHAR(50) COMMENT '品种',
    age INT COMMENT '年龄',
    gender VARCHAR(10) COMMENT '性别',
    color VARCHAR(50) COMMENT '毛色',
    description TEXT COMMENT '简介',
    image_url VARCHAR(255) COMMENT '图片URL',
    status VARCHAR(20) DEFAULT 'available' COMMENT '状态:available可领养,adopted已领养',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色:user普通用户,admin管理员',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 领养申请表
CREATE TABLE IF NOT EXISTS adoption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cat_id BIGINT NOT NULL COMMENT '猫咪ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态:pending待审核,approved已通过,rejected已拒绝',
    reason TEXT COMMENT '理由/备注',
    apply_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cat_id) REFERENCES cat(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO cat (name, breed, age, gender, color, description, status) VALUES
('小橘', '中华田园猫', 2, '公', '橘色', '性格温顺，喜欢撒娇', 'available'),
('小白', '中华田园猫', 1, '母', '白色', '活泼可爱，适合家庭领养', 'available'),
('灰灰', '英国短毛猫', 3, '公', '灰色', '安静稳重，喜欢睡觉', 'available'),
('咪咪', '中华田园猫', 2, '母', '三花', '聪明伶俐，已绝育', 'available');

INSERT INTO user (username, password, nickname, role) VALUES
('admin', 'admin', '管理员', 'admin'),
('test', '123456', '测试用户', 'user');