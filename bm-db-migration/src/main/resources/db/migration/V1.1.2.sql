ALTER TABLE `order_transaction`
CHANGE COLUMN `price` `price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `order_transaction`
CHANGE COLUMN `conversion_price` `conversion_price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `order_transaction`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `importing_transaction`
CHANGE COLUMN `price` `price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `importing_transaction`
CHANGE COLUMN `conversion_price` `conversion_price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `importing_transaction`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `exporting_transaction`
CHANGE COLUMN `price` `price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `exporting_transaction`
CHANGE COLUMN `conversion_price` `conversion_price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `exporting_transaction`
CHANGE COLUMN `cost_of_goods_sold` `cost_of_goods_sold` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `exporting_transaction`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `payment_detail`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `payment_advice_detail`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `receipt_detail`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `receipt_advice_detail`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `debt_clearing_detail`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `importing_return_transaction`
CHANGE COLUMN `price` `price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `importing_return_transaction`
CHANGE COLUMN `conversion_price` `conversion_price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `importing_return_transaction`
CHANGE COLUMN `cost_of_goods_sold` `cost_of_goods_sold` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `importing_return_transaction`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `monthly_closing_balance`
CHANGE COLUMN `debit_balance` `debit_balance` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `monthly_closing_balance`
CHANGE COLUMN `credit_balance` `credit_balance` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `exporting_return_transaction`
CHANGE COLUMN `price` `price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `exporting_return_transaction`
CHANGE COLUMN `conversion_price` `conversion_price` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `exporting_return_transaction`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `referral_bonus`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `invoice_commission`
CHANGE COLUMN `min_revenue` `min_revenue` DOUBLE NULL DEFAULT NULL ;
ALTER TABLE `invoice_commission`
CHANGE COLUMN `bonus` `bonus` DOUBLE NULL DEFAULT NULL ;

ALTER TABLE `selling_bonus`
CHANGE COLUMN `amount` `amount` DOUBLE NULL DEFAULT NULL ;
