CREATE TABLE IF NOT EXISTS `permissions` (
    `code` varchar(50) NOT NULL,
    `name` varchar(225) NOT NULL,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (code)
);
INSERT INTO permissions(code, name, created_date)
VALUES ('STORE_MANAGEMENT', 'Quản lý kho', '2020-11-04')
,('STORE_SETTING','Quản lý cài đặt kho', '2020-11-04')
,('ROLES_MANAGEMENT','Quản lý quyền', '2020-11-04')
,('EMPLOYEE_MANAGEMENT','Quản lý nhân viên', '2020-11-04')
,('CUSTOMER_GROUP_MANAGEMENT','Quản lý nhóm khách hàng', '2020-11-04')
,('CUSTOMER_MANAGEMENT','Quản lý khách hàng', '2020-11-04')
,('IMPORTING_MANAGEMENT','Quản lý phiếu nhập hàng hóa', '2020-11-04')
,('EXPORTING_MANAGEMENT','Quản lý phiếu xuất hàng hóa', '2020-11-04')
,('PAYMENT_MANAGEMENT','Quản lý phiếu chi tiền mặt', '2020-11-04')
,('RECEIPT_MANAGEMENT','Quản lý phiếu thu tiền mặt', '2020-11-04')
,('PAYMENT_ADVICE_MANAGEMENT','Quản lý giấy báo nợ - chi', '2020-11-04')
,('RECEIPT_ADVICE_MANAGEMENT','Quản lý giấy báo có - thu', '2020-11-04')
,('MERCHANDISE_MANAGEMENT','Quản lý hàng hóa', '2020-11-04')
,('PRODUCT_GROUP_MANAGEMENT','Quản lý nhóm sản phẩm', '2020-11-04')
,('IMPORTING_INTERNAL_MANAGEMENT','Quản lý nhập nội bộ', '2020-11-04')
,('EXPORTING_INTERNAL_MANAGEMENT','Quẩn lý xuất nội bộ', '2020-11-04')
,('UNIT_MANAGEMENT','Quản lý đơn vị', '2020-11-04')
,('ORDER_MANAGEMENT','Quản lý phiếu đặt hàng', '2020-11-04')
,('MERCHANDISE_GROUP_MANAGEMENT','Quản lý nhóm hàng hóa', '2020-11-04')
,('MERCHANDISE_WAREHOUSE_MANAGEMENT','Quản lý tồn kho', '2020-11-04')
,('DEBT_CLEARING_MANAGEMENT','Quản lý bù trừ công nợ', '2020-11-04')
,('REPORT_MANAGEMENT','Báo cáo thống kê', '2020-11-04')
,('IMPORT_DATA', 'Nhập dữ liệu', '2020-11-04')
,('EXPORTING_RETURN_MANAGEMENT', 'Quản lý xuất hàng hóa trả lại người bán', '2020-11-04')
,('IMPORT_RETURN_MANAGEMENT', 'Quản lý nhập hàng bán bị trả lại', '2020-11-04');

CREATE TABLE IF NOT EXISTS `roles` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225) NOT NULL,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
INSERT INTO roles(id, name, created_date)
VALUES('326d6676-43a6-42c2-87d0-64e4a992bc3f', 'Admin', '2020-04-24')
,('r2g4r576-4r36-4eb2-07ro-266tr9r2rc1l', 'Sale', '2020-11-06')
,('r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'Sale Admin', '2020-11-06');

CREATE TABLE IF NOT EXISTS `grant_permissions` (
    `id` varchar(50) NOT NULL,
    `role_id` varchar(50) NOT NULL,
    `permission_code` varchar(50) NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO grant_permissions(id,role_id, permission_code) VALUES
('266b309c-0e1d-405f-b9fe-51adc8f2fa8b','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'STORE_MANAGEMENT')
,('0b07f2c2-66b5-4a15-91dc-a153cc17e7b0','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'STORE_SETTING')
,('454ec9fc-4f9c-44d6-9b8e-bfdfb594faaa','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'ROLES_MANAGEMENT')
,('a221e674-a8f7-4ed6-9197-fa187dc62a11','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'EMPLOYEE_MANAGEMENT')
,('d63c71b1-34dc-497e-95a3-58a69a21d331','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'CUSTOMER_GROUP_MANAGEMENT')
,('067f71b1-34dc-497e-95a3-58a69a21d456','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'CUSTOMER_MANAGEMENT')
,('067r7lp2-34dc-497e-89c4-58a69av5d456','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'IMPORTING_MANAGEMENT')
,('083r7lp2-344d-445e-89c4-58a69ab8fd46','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'EXPORTING_MANAGEMENT')
,('092g7qp3-34kf-d24e-o0c4-38at3av5d446','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'PAYMENT_MANAGEMENT')
,('or347lp2-34dc-487e-89c4-58a69av5d456','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'RECEIPT_MANAGEMENT')
,('0o0r4df3-34yc-4h7e-8pc4-58a6eav5d456','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'PAYMENT_ADVICE_MANAGEMENT')
,('084rfj32-0n3c-eq7e-eu44-58a60av5do06','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'RECEIPT_ADVICE_MANAGEMENT')
,('1t4r3j64-0n3c-e47e-er94-58a6dav5do06','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'MERCHANDISE_MANAGEMENT')
,('0t44pjz4-qn3c-e47e-er14-58e6dab1di06','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'MERCHANDISE_GROUP_MANAGEMENT')
,('0f4lp5nm-mn3c-e47e-er14-51r6lav5do21','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'MERCHANDISE_WAREHOUSE_MANAGEMENT')
,('684r55d4-o33c-r4rd-e894-58a6d4v5do30','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'PRODUCT_GROUP_MANAGEMENT')
,('14vr3j64-k54c-c47e-er94-02b6d5l4do02','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'IMPORTING_INTERNAL_MANAGEMENT')
,('36vt4j0f-k0tc-c47e-el63-023vd5lpd002','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'EXPORTING_INTERNAL_MANAGEMENT')
,('q2lr433f-40t5-p22e-r12r-en3vd5lpd089','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'UNIT_MANAGEMENT')
,('q2lr4t3f-p0j6-l28e-5pv2-ln3vd5l1d00g','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'ORDER_MANAGEMENT')
,('q2lr4t3f-p0j6-l28e-5pv2-ln3vd5l1d10g','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'DEBT_CLEARING_MANAGEMENT')
,('94bf81da-47af-400d-8138-a4ff5af91e13','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'REPORT_MANAGEMENT')
,('fb971741-f45a-4c7a-93af-9bd854316bc2','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'IMPORT_DATA')
,('fb971751-f45a-4c7a-93af-9bd854316bc2','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'EXPORTING_RETURN_MANAGEMENT')
,('0b941741-f45a-4c7a-93af-9bd8543161c2','326d6676-43a6-42c2-87d0-64e4a992bc3f', 'IMPORT_RETURN_MANAGEMENT')

,('067r74f2-34dc-497e-23c4-58a69av5d456','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'IMPORTING_MANAGEMENT')
,('qe3r7232-344d-445e-89c4-58a69ab8fd33','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'EXPORTING_MANAGEMENT')
,('nq2g7qp3-34kf-d24e-o0c4-38at3a2r4236','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'PAYMENT_MANAGEMENT')
,('or347342-34dc-487e-89c4-58a69a45d456','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'RECEIPT_MANAGEMENT')
,('0o034df3-34yc-447e-8pc4-58a6eav5d456','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'PAYMENT_ADVICE_MANAGEMENT')
,('0823fj32-0n3c-e17e-eu44-58a60av5ie06','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'RECEIPT_ADVICE_MANAGEMENT')
,('14vr3j64-k54c-c47e-er94-02b6d5l4dg22','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'IMPORTING_INTERNAL_MANAGEMENT')
,('21v3rj0f-k0tc-c47e-el63-022vd5lrd232','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'EXPORTING_INTERNAL_MANAGEMENT')
,('02v3rj0f-l1pc-167e-zlt6-l2dzd5l7d10m','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'ORDER_MANAGEMENT')
,('q2lr4t3f-p0j6-l283-5pv2-ln3vd561d10g','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'DEBT_CLEARING_MANAGEMENT')
,('02lr433f-p0j6-lo8e-5pv2-ln3vd561d101','r24d6t76-4ra6-4e42-87do-52e4a9r2bc3f', 'DEBT_CLEARING_MANAGEMENT')


,('q233rv51-l11c-02pz-n5t6-n2d1g527d10m','r2g4r576-4r36-4eb2-07ro-266tr9r2rc1l', 'ORDER_MANAGEMENT');



CREATE TABLE IF NOT EXISTS `employees` (
    `id` varchar(50) NOT NULL,
    `full_name` varchar(225) NOT NULL,
    `birth_date` DATE,
    `phone` varchar(50),
    `email` varchar(225) NOT NULL,
    `password` varchar(225) NOT NULL,
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

-- pass: 123456
INSERT INTO employees(id, full_name, birth_date, email, password)
VALUES('f7887a85-1e83-55f1-86d0-b3574b7fe3eb', 'Admin', '2020-04-24', 'admin@vlxdapp.com', '$2a$09$DlFMW.ofkjNQXaNhPp7ug..kWu5i3jSacX7w1HcKdm9cd6xq4C8FC');

CREATE TABLE IF NOT EXISTS `roles_detail` (
    `id` varchar(50) NOT NULL,
    `role_id` varchar(50) NOT NULL,
    `employee_id` varchar(50) NOT NULL,
    PRIMARY KEY (id)
);
INSERT INTO roles_detail(id, role_id, employee_id)
VALUES('86481751-764c-4dd2-a87c-e1697e951950', '326d6676-43a6-42c2-87d0-64e4a992bc3f', 'f7887a85-1e83-55f1-86d0-b3574b7fe3eb');

CREATE TABLE IF NOT EXISTS `orders` (
    `id` varchar(50) NOT NULL,
    `title` varchar(225),
    `customer_id` varchar(50) NOT NULL,
    `code` varchar(225),
    `number` varchar(225),
    `import_status` varchar(50) NULL,
    `deliver_status` varchar(50) NULL,
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `order_transaction` (
    `id` varchar(50) NOT NULL,
    `order_id` varchar(50) NOT NULL,
    `merchandise_id` varchar(50) NOT NULL,
    `quantity` float NOT NULL,
    `conversion_quanity` float,
    `price` float NOT NULL,
    `conversion_price` float,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_warehouse` (
    `id` varchar(50) NOT NULL,
    `order_id` varchar(50),
    `payment_status` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_id` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `foreign_currency` varchar(50),
    `foreign_currency_rate` varchar(50),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_transaction` (
    `id` varchar(50) NOT NULL,
    `import_id` varchar(50) NOT NULL,
    `order_id` varchar(50),
    `merchandise_id` varchar(50) NOT NULL,
    `quantity` float NOT NULL,
    `conversion_quantity` float,
    `price` float NOT NULL,
    `conversion_price` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `exporting_warehouse` (
    `id` varchar(50) NOT NULL,
    `order_id` varchar(50),
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_id` varchar(50),
    `transaction_customer_id` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `foreign_currency` varchar(50),
    `foreign_currency_rate` varchar(50),
    `payment_status` varchar(50) NOT NULL,
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `exporting_transaction` (
    `id` varchar(50) NOT NULL,
    `export_id` varchar(50) NOT NULL,
    `order_id` varchar(50),
    `merchandise_id` varchar(50) NOT NULL,
    `quantity` float NOT NULL,
    `conversion_quantity` float,
    `price` float NOT NULL,
    `conversion_price` float,
    `cost_of_goods_sold` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    `debit_account_purchase` varchar(50),
    `credit_account_purchase` varchar(50),
    `deliver_status` varchar(50),
    `payment_status` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `payment` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `customer_id` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `payment_detail` (
    `id` varchar(50) NOT NULL,
    `payment_id` varchar(50) NOT NULL,
    `importing_warehouse_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `payment_advice` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `customer_id` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `payment_advice_detail` (
    `id` varchar(50) NOT NULL,
    `payment_advice_id` varchar(50) NOT NULL,
    `importing_warehouse_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `receipt` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `customer_id` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `receipt_detail` (
    `id` varchar(50) NOT NULL,
    `receipt_id` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `receipt_advice` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `customer_id` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `receipt_advice_detail` (
    `id` varchar(50) NOT NULL,
    `receipt_advice_id` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `settings` (
    `id` int NOT NULL AUTO_INCREMENT,
    `label` varchar(225),
    `key` varchar(225),
    `value` varchar(225),
    PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS `debt_clearing` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `debt_clearing_detail` (
    `id` varchar(50) NOT NULL,
    `debt_clearing_id` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `customer_debt_id` varchar(50),
    `customer_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_return` (
    `id` varchar(50) NOT NULL,
    `code` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_id` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `foreign_currency` varchar(50),
    `foreign_currency_rate` varchar(50),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `importing_return_transaction` (
    `id` varchar(50) NOT NULL,
    `importing_return_id` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `merchandise_id` varchar(50) NOT NULL,
    `quantity` float NOT NULL,
    `conversion_quantity` float,
    `price` float NOT NULL,
    `conversion_price` float,
    `cost_of_goods_sold` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    `debit_account_purchase` varchar(50),
    `credit_account_purchase` varchar(50),
    PRIMARY KEY (id)
);

INSERT INTO settings(id, label, `key`, `value`) VALUES(1, 'Kho', 'WAREHOUSE', '1275c181-df1b-4f43-b7bc-6179da89a4cf');

CREATE TABLE IF NOT EXISTS `monthly_closing_balance` (
    `id` varchar(50) NOT NULL,
    `customer_id` varchar(50),
    `debit_balance` float,
    `credit_balance` float,
    `closing_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `exporting_return` (
    `id` varchar(50) NOT NULL,
    `import_id` varchar(50),
    `code` varchar(50) NOT NULL,
    `number` varchar(50) NOT NULL,
    `invoice_date` DATETIME,
    `invoice_code` varchar(50),
    `invoice_template` varchar(50),
    `invoice_symbol` varchar(50),
    `invoice_number` varchar(50),
    `customer_id` varchar(50),
    `customer_address` varchar(225),
    `customer_tax_code` varchar(50),
    `transaction_customer_id` varchar(50),
    `description` varchar(225),
    `note` varchar(225),
    `foreign_currency` varchar(50),
    `foreign_currency_rate` varchar(50),
    `created_date` DATE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `exporting_return_transaction` (
    `id` varchar(50) NOT NULL,
    `exporting_return_id` varchar(50) NOT NULL,
    `import_id` varchar(50),
    `merchandise_id` varchar(50) NOT NULL,
    `quantity` float NOT NULL,
    `conversion_quantity` float,
    `price` float NOT NULL,
    `conversion_price` float,
    `credit_account` varchar(50),
    `debit_account` varchar(50),
    PRIMARY KEY (id)
);
