-- 更新数据库结构
USE cat_adoption;

-- 添加新字段到cat表
ALTER TABLE cat ADD COLUMN category VARCHAR(20) DEFAULT 'adoption' COMMENT 'adoption领养/market商城';
ALTER TABLE cat ADD COLUMN add_user_id BIGINT COMMENT '添加者ID';
ALTER TABLE cat ADD COLUMN is_hot TINYINT(1) DEFAULT 0 COMMENT '是否热门';
ALTER TABLE cat ADD COLUMN health_status VARCHAR(100) COMMENT '健康状况';
ALTER TABLE cat ADD COLUMN vaccine_record VARCHAR(200) COMMENT '疫苗记录';
ALTER TABLE cat ADD COLUMN is_sterilized TINYINT(1) DEFAULT 0 COMMENT '是否绝育';
ALTER TABLE cat ADD COLUMN price DECIMAL(10,2) DEFAULT 0 COMMENT '领养费用';
ALTER TABLE cat ADD COLUMN location VARCHAR(100) COMMENT '所在地';

-- 修改status字段增加选项
ALTER TABLE cat MODIFY COLUMN status VARCHAR(20) DEFAULT 'available';

-- 创建评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL COMMENT '评论内容',
    parent_id BIGINT COMMENT '回复的评论ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cat_id) REFERENCES cat(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
TRUNCATE TABLE cat;
INSERT INTO cat (name, breed, age, gender, color, description, image_url, status, category, is_hot, health_status, vaccine_record, is_sterilized, price, location) VALUES
('小橘', '中华田园猫', 2, '公', '橘色', '性格温顺，喜欢撒娇，已绝育驱虫齐全', 'https://placekitten.com/301/200?image=1', 'available', 'adoption', 1, '健康', '已接种疫苗', 1, 0, '北京'),
('小白', '中华田园猫', 1, '母', '白色', '活泼可爱，适合有孩子的家庭', 'https://placekitten.com/302/200?image=2', 'available', 'adoption', 1, '健康', '已完成驱虫', 0, 0, '上海'),
('灰灰', '英国短毛猫', 3, '公', '银渐层', '安静稳重，喜欢睡觉，已绝育', 'https://placekitten.com/303/200?image=3', 'available', 'adoption', 0, '健康', '疫苗齐全', 1, 200, '广州'),
('咪咪', '中华田园猫', 2, '母', '三花', '聪明伶俐，非常粘人', 'https://placekitten.com/304/200?image=4', 'available', 'adoption', 1, '健康', '已绝育', 1, 0, '深圳'),
('雪球', '美国短毛猫', 1, '母', '白色', '活泼好动，血统纯正', 'https://placekitten.com/306/200?image=6', 'available', 'market', 0, '健康', '疫苗齐全', 0, 800, '杭州'),
('胖橘', '中华田园猫', 4, '公', '橘色', '胖乎乎的很可爱，性格好', 'https://placekitten.com/305/200?image=5', 'available', 'market', 0, '健康', '已绝育', 1, 150, '成都');

-- 插入测试评论
INSERT INTO comment (cat_id, user_id, content) VALUES
(1, 2, '这只猫猫好可爱，希望能领养它！'),
(1, 2, '请问领养需要什么条件？'),
(2, 2, '已领养，非常乖！'),
(3, 2, '有血统证书吗？');