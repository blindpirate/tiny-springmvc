-- 用户表
create table user
(
    id   bigint auto_increment primary key,
    name varchar(100) unique comment '用户名'
) DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 商品表
create table goods
(
    id   bigint auto_increment primary key,
    name varchar(100) comment '商品名'
) DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- 订单表
create table `order`
(
    id       bigint auto_increment primary key,
    goods_id bigint comment '商品id',
    user_id  bigint comment '用户id',
    price    int comment '价格',
    quantity int comment '数量'
) DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

insert into user(id, name)
values (1, '张三'),
       (2, '李四'),
       (3, '王五'),
       (4, '赵六');

insert into goods(id, name)
values (100, '苹果'),
       (200, '香蕉'),
       (300, '桃子'),
       (400, '葡萄'),
       (500, '西瓜');

insert into `order` (id, goods_id, user_id, price, quantity)
values (1, 100, 1, 100, 2),
       (2, 100, 4, 100, 10),
       (3, 200, 3, 200, 11),
       (4, 300, 1, 300, 9),
       (5, 200, 2, 200, 3),
       (6, 400, 1, 400, 1),
       (7, 500, 4, 500, 2),
       (8, 200, 2, 200, 6),
       (9, 100, 2, 100, 6),
       (10, 400, 3, 400, 4),
       (11, 500, 4, 500, 5),
       (12, 300, 1, 300, 20),
       (13, 200, 3, 200, 2),
       (14, 400, 2, 400, 7),
       (15, 200, 4, 200, 8),
       (16, 300, 1, 300, 20),
       (17, 100, 4, 100, 13),
       (18, 400, 2, 400, 8)
