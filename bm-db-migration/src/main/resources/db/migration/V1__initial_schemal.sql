CREATE TABLE IF NOT EXISTS `employees` (
    `id` varchar(50) NOT NULL,
    `full_name` varchar(225),
    `birth_date` DATETIME,
    `phone` varchar(50),
    `email` varchar(225) NOT NULL,
    `password` varchar(225) NOT NULL,
    `role` varchar(50),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

-- pass: 123456
INSERT INTO employees(id, full_name, birth_date, email, password, role)
VALUES('f7887a85-1e83-55f1-86d0-b3574b7fe3eb', 'Admin', '2020-04-24', 'pkt@pos.com', '$2a$09$DlFMW.ofkjNQXaNhPp7ug..kWu5i3jSacX7w1HcKdm9cd6xq4C8FC', 'MANAGER');

CREATE TABLE IF NOT EXISTS `customers` (
    `id` varchar(50) NOT NULL,
    `full_name` varchar(225),
    `address` varchar(225),
    `phone` varchar(50),
    `birth_date` DATETIME,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
INSERT INTO customers(id, full_name, address, phone, birth_date) VALUES
('1dfd5e63-f1dd-445d-b83e-94070a01ec20', 'Huỳnh Kim Kha', '480 Mã Lò', '0378411656', '1999-11-11');

CREATE TABLE IF NOT EXISTS `settings` (
    `id` int NOT NULL AUTO_INCREMENT,
    `label` varchar(225),
    `key` varchar(225),
    `value` varchar(225),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `products` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
    `price` float,
    `name_slug` varchar(225),
    `images` varchar(225),
    `content` varchar(225),
    `status` varchar(50),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `categories` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225) NOT NULL,
    `status` varchar(50),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `product_category` (
    `id` varchar(50) NOT NULL,
    `product_id` varchar(50) NOT NULL,
    `category_id` varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `promotion` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
    `description` varchar(225),
    `amount` float,
    `status` varchar(50),
    `expired_date` DATETIME,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `promotion_product` (
    `id` varchar(50) NOT NULL,
    `product_id` varchar(50) NOT NULL,
    `promotion_id` varchar(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `bills` (
    `id` varchar(50) NOT NULL,
    `employee_id` varchar(50) NOT NULL,
    `customer_id` varchar(50),
    `promotion_id` varchar(50),
    `agency_id` varchar(50),
    `code` varchar(50),
    `number` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `amount` float,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `product_category` (
    `id` varchar(50) NOT NULL,
    `product_id` varchar(50) NOT NULL,
    `bill_id` varchar(50) NOT NULL,
    `quantity` float,
    `price` float,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `agency` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
    `address` varchar(225),
    `org_code` varchar(50),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
INSERT INTO agency(id, `name`, address, org_code) VALUES
('bfbd7f9d-f880-4076-a342-eeceba11dcfa', 'HQSV_DHSG', '273 An Dương Vương, Quận 5, TPHCM', '');


CREATE TABLE IF NOT EXISTS `cost` (
    `id` varchar(50) NOT NULL,
    `agency_id` varchar(50),
    `type_cost` varchar(225),
    `amount` float,
    `description` varchar(225),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
