-- 创建订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address VARCHAR(255) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status enum('PENDING_PAYMENT', 'PENDING_SHIPMENT', 'SHIPPED', 'PENDING_RECEIPT','COMPLETED', 'CANCELLED') DEFAULT 'PENDING_PAYMENT' NOT NULL,
    tracking_number VARCHAR(50),
    shipping_company VARCHAR(50),
    create_time DATETIME NOT NULL,
    pay_time DATETIME,
    ship_time DATETIME,
    complete_time DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
);

-- 创建订单项表
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- 创建索引
CREATE INDEX idx_user_id ON orders(user_id);
CREATE INDEX idx_seller_id ON orders(seller_id);
CREATE INDEX idx_order_number ON orders(order_number);
CREATE INDEX idx_status ON orders(status);
CREATE INDEX idx_order_id ON order_items(order_id);
CREATE INDEX idx_product_id ON order_items(product_id); 