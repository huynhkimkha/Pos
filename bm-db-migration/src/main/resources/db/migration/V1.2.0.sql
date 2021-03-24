CREATE TABLE IF NOT EXISTS `agency` (
    `id` varchar(50) NOT NULL,
    `name` varchar(50) NOT NULL,
    `address` varchar(225),
    `org_code` varchar(50) NOT NULL,
    `base_url` varchar(225),
    `created_date` DATETIME,
    `updated_date` DATETIME,
    PRIMARY KEY (id)
);
INSERT INTO agency(id, `name`, address, org_code, base_url) VALUES
('bfbd7f9d-f880-4076-a342-eeceba11dcfa', 'Hoà Phát Nhà Bè', '32 Nguyễn Bình, Xã Phú Xuân, Nhà Bè, TPHCM', 'HPNHABE', '');

ALTER TABLE `agency`
ADD COLUMN `company_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `invoice_commission`
ADD COLUMN `company_id` VARCHAR(50) NULL AFTER `id`;

UPDATE `agency` SET `company_id` = '1a9e8a77-29bf-45c8-906a-02f2df4c475e';
UPDATE `invoice_commission` SET `company_id` = '1a9e8a77-29bf-45c8-906a-02f2df4c475e';

ALTER TABLE `roles`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `employees`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `collaborators`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `settings`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `monthly_closing_balance`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `orders`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `importing_warehouse`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `exporting_warehouse`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `payment`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `receipt`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `importing_return`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `exporting_return`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `debt_clearing`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `payment_advice`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `receipt_advice`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `selling_bonus`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;
ALTER TABLE `referral_bonus`
ADD COLUMN `agency_id` VARCHAR(50) NULL AFTER `id`;

UPDATE `roles` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `employees` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `collaborators` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `settings` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `monthly_closing_balance` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `orders` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `importing_warehouse` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `exporting_warehouse` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `payment` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `receipt` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `importing_return` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `exporting_return` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `debt_clearing` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `payment_advice` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `receipt_advice` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `selling_bonus` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';
UPDATE `referral_bonus` set `agency_id` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa';

UPDATE `settings` SET `label` = 'Đại lý', `key` = 'AGENCY', `value` = 'bfbd7f9d-f880-4076-a342-eeceba11dcfa' WHERE (`id` = '1');

ALTER TABLE `permissions` ADD COLUMN `requirement` VARCHAR(50) NULL;
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'COLLABORATOR_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'CUSTOMER_GROUP_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'CUSTOMER_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'EMPLOYEE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'STORE_SETTING');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'UNIT_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'STORE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'ROLES_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'MERCHANDISE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'DEBT_CLEARING_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'IMPORT_DATA');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'EXPORTING_INTERNAL_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'IMPORT_RETURN_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'EXPORTING_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'ORDER_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'IMPORTING_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'MERCHANDISE_GROUP_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'MERCHANDISE_WAREHOUSE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'EXPORTING_RETURN_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'IMPORTING_INTERNAL_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'INVOICE_COMMISSION_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'PAYMENT_ADVICE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'PAYMENT_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'RECEIPT_ADVICE_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'RECEIPT_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'OPTIONAL' WHERE (`code` = 'REPORT_MANAGEMENT');
UPDATE `permissions` SET `requirement` = 'REQUIRED' WHERE (`code` = 'PRODUCT_GROUP_MANAGEMENT');

INSERT INTO `settings`(`agency_id`, `label`, `key`, `value`)
VALUES ('bfbd7f9d-f880-4076-a342-eeceba11dcfa', 'Chế độ kế toán', 'ACCOUNTANT_MODE', '1');
