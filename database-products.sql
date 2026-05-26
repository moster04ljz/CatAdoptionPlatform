-- 猫咪用品表
CREATE TABLE IF NOT EXISTS product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    category VARCHAR(50) NOT NULL COMMENT '分类：cat_food猫粮/cat_treat猫条/cat_litter猫砂/cat_toy玩具/cat_bed猫窝/cat_other其他',
    brand VARCHAR(50) COMMENT '品牌',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存',
    image_url VARCHAR(500) COMMENT '图片',
    description TEXT COMMENT '描述',
    status VARCHAR(20) DEFAULT 'available' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending待支付/paid已支付/shipped已发货/completed已完成',
    receiver_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单详情表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 轮播图表
CREATE TABLE IF NOT EXISTS carousel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    cat_id BIGINT COMMENT '关联猫咪ID',
    image_url VARCHAR(500) NOT NULL,
    title VARCHAR(100),
    link_url VARCHAR(200),
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试商品数据
INSERT INTO product (name, category, brand, price, stock, image_url, description) VALUES
('渴望全猫粮5kg', 'cat_food', 'Orijen', 520, 100, 'https://placekitten.com/300/200?food=1', '加拿大进口天然猫粮'),
('皇家猫罐头', 'cat_food', 'Royal Canin', 25, 200, 'https://placekitten.com/301/200?food=2', '幼猫营养罐头'),
('珍致猫罐头', 'cat_food', 'Kanpur', 18, 150, 'https://placekitten.com/302/200?food=3', '猫零食罐'),
('pidan猫砂', 'cat_litter', 'pidan', 45, 300, 'https://placekitten.com/303/200?litter=1', '豆腐猫砂可直接冲马桶'),
('Petshy猫砂', 'cat_litter', 'Petshy', 39, 250, 'https://placekitten.com/304/200?litter=2', '活性炭除臭猫砂'),
('逗猫棒', 'cat_toy', 'pidan', 15, 500, 'https://placekitten.com/305/200?toy=1', '逗猫神器'),
('猫抓板', 'cat_toy', 'Mobby', 25, 300, 'https://placekitten.com/306/200?toy=2', '瓦楞纸猫抓板'),
('贝壳猫窝', 'cat_bed', 'tmall', 68, 100, 'https://placekitten.com/307/200?bed=1', '半封闭猫窝'),
('智能饮水机', 'cat_other', 'Petsafe', 188, 50, 'https://placekitten.com/308/200?other=1', '循环过滤饮水机');

-- 插入轮播图
INSERT INTO carousel (image_url, title, sort_order, status) VALUES
('https://placekitten.com/1200/400?banner=1', '领养代替购买', 1, 'active'),
('https://placekitten.com/1200/400?banner=2', '新品上架', 2, 'active'),
('https://placekitten.com/1200/400?banner=3', '限时优惠', 3, 'active');