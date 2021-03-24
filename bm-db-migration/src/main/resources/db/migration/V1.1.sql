
CREATE TABLE IF NOT EXISTS `collaborators` (
    `id` varchar(50) NOT NULL,
    `full_name` varchar(225) NOT NULL,
    `address` varchar(225),
    `district` varchar(225),
    `province` varchar(225),
    `birth_date` DATE,
    `phone` varchar(50),
    `email` varchar(100) NOT NULL,
    `password` varchar(225) NOT NULL,
    `sale_rank_id` varchar(50),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `referral_bonus` (
    `id` varchar(50) NOT NULL,
    `employee_ref_id` varchar(50),
    `collaborator_ref_id` varchar(50),
    `employee_id` varchar(50),
    `collaborator_id` varchar(50),
    `amount` float,
    `payment_status` varchar(50),
    `created_date` DATE,
    PRIMARY KEY (id)
);

ALTER TABLE `collaborators`
ADD `activated_status` varchar(50);

ALTER TABLE `collaborators`
ADD `blocked_status` varchar(50);

ALTER TABLE `referral_bonus`
ADD `activated_status` varchar(50);

CREATE TABLE IF NOT EXISTS `invoice_commission` (
    `id` varchar(50) NOT NULL,
    `name` varchar(225),
    `min_revenue` float,
    `bonus` float,
    `created_date` DATE,
    PRIMARY KEY (id)
);

INSERT INTO permissions(code, name, created_date) VALUES
('INVOICE_COMMISSION_MANAGEMENT', 'Quản lí định mức hoa hồng', '2020-12-30')
,('COLLABORATOR_MANAGEMENT', 'Quản lí cộng tác viên', '2020-1-16');

INSERT INTO grant_permissions(id,role_id, permission_code) VALUES
('qzlr4b3f-49t5-p12e-r32r-en3v5slpdt89', '326d6676-43a6-42c2-87d0-64e4a992bc3f', 'INVOICE_COMMISSION_MANAGEMENT')
,('1plrybzf-10t5-11ee-r32r-ep3z52lprt09', '326d6676-43a6-42c2-87d0-64e4a992bc3f', 'COLLABORATOR_MANAGEMENT');

CREATE TABLE IF NOT EXISTS `selling_bonus` (
    `id` varchar(50) NOT NULL,
    `exporting_warehouse_id` varchar(50),
    `employee_ref_id` varchar(50),
    `employee_id` varchar(50),
    `collaborator_ref_id` varchar(50),
    `collaborator_id` varchar(50),
    `description` varchar(225),
    `amount` float,
    `payment_status` varchar(50),
    `created_date` DATE,
    PRIMARY KEY (id)
);

ALTER TABLE `payment_detail`
ADD `selling_bonus_id` varchar(50), ADD `referral_bonus_id` varchar(50);
ALTER TABLE `payment_advice_detail`
ADD `selling_bonus_id` varchar(50), ADD `referral_bonus_id` varchar(50);
