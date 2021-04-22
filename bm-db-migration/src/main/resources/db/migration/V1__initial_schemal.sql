CREATE TABLE IF NOT EXISTS `employees` (
    `id` varchar(50) NOT NULL,
    `agency_id` varchar(50),
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
INSERT INTO employees(id, agency_id, full_name, birth_date, email, password, role)
VALUES('f7887a85-1e83-55f1-86d0-b3574b7fe3eb', 'bfbd7f9d-f880-4076-a342-eeceba11dcfa', 'Admin', '2020-04-24', 'pkt@pos.com', '$2a$09$DlFMW.ofkjNQXaNhPp7ug..kWu5i3jSacX7w1HcKdm9cd6xq4C8FC', 'MANAGER');

CREATE TABLE IF NOT EXISTS `customers` (
    `id` varchar(50) NOT NULL,
    `customer_type` varchar(225),
    `full_name` varchar(225),
    `address` varchar(225),
    `phone` varchar(50),
    `birth_date` DATETIME,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
INSERT INTO customers(id, customer_type, full_name, address, phone, birth_date) VALUES
('1dfd5e63-f1dd-445d-b83e-94070a01ec20', 'CUSTOMER', 'Huỳnh Kim Kha', '480 Mã Lò', '0378411656', '1999-11-11'),
('323d5e63-f1dd-445d-b83e-94070a01ec12', 'VENDOR', 'Huỳnh Ngọc Thanh Phong', '353 Nguyễn Trãi', '0123456789', '1999-11-11');

CREATE TABLE IF NOT EXISTS `products` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
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
INSERT INTO categories(id, `name`, status) VALUES
('6d717691-9af5-43a5-be9c-eef336db6990', 'Sinh Tố', 'ACTIVE'),
('8a8ffbb9-bce7-411d-a5ae-d6af899d1f3b', 'Cà phê', 'ACTIVE'),
('cc72f914-2e06-4a6f-9a66-06b62c7cc020', 'Sữa chua', 'ACTIVE'),
('cedb935f-fcc8-4888-b8f7-e056673d3661', 'Trà', 'ACTIVE');

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
    `type_promotion` varchar(50),
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
    `cost_category_id` varchar(225),
    `code` varchar(50),
    `number` varchar(50),
    `amount` float,
    `description` varchar(225),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `cost_category` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
    PRIMARY KEY (id)
);

INSERT INTO cost_category(id, name) VALUES
('12317691-9af5-43a5-be9c-eef336db6111', 'TRẢ LƯƠNG'),
('234ffbb9-bce7-411d-a5ae-d6af899d1222', 'MẶT BẰNG'),
('3452f914-2e06-4a6f-9a66-06b62c7cc333', 'NHẬP HÀNG'),
('5672f914-2e06-4a6f-9a66-06b62c7cc555', 'NƯỚC'),
('6782f914-2e06-4a6f-9a66-06b62c7cc666', 'KHÁC'),
('456b935f-fcc8-4888-b8f7-e056673d3444', 'ĐIỆN');


CREATE TABLE IF NOT EXISTS `bill_product_size` (
    `id` varchar(50) NOT NULL,
    `bill_id` varchar(50),
    `product_size_id` varchar(50),
    `price` float,
    `quantity` float,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `sizes` (
    `id` varchar(50) NOT NULL,
    `name` varchar(50),
    PRIMARY KEY (id)
);
INSERT INTO sizes(id, `name`) VALUES
('a1717691-9af5-43a5-be9c-eef336db6990', 'S'),
('a28ffbb9-bce7-411d-a5ae-d6af899d1f3b', 'M'),
('a372f914-2e06-4a6f-9a66-06b62c7cc020', 'L');

CREATE TABLE IF NOT EXISTS `product_size` (
    `id` varchar(50) NOT NULL,
    `product_id` varchar(50),
    `size_id` varchar(50),
    `price` float,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `materials` (
    `id` varchar(50) NOT NULL,
    `name` varchar(50),
    `unit` varchar(50),
    `price` float,
    `content` varchar(225),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `inventory` (
    `id` varchar(50) NOT NULL,
    `agency_id` varchar(50),
    `material_id` varchar(50),
    `amount` float,
    `amount_check` float,
    `description` varchar(225),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_material` (
    `id` varchar(50) NOT NULL,
    `agency_id` varchar(50),
    `customer_id` varchar(50),
    `code` varchar(50),
    `number` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `amount` float,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_transaction` (
    `id` varchar(50) NOT NULL,
    `importing_material_id` varchar(50),
    `material_id` varchar(50),
    `quantity` float,
    `price` float,
    `amount` float,
    PRIMARY KEY (id)
);

