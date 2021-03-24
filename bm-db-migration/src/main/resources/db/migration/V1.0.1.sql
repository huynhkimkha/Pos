ALTER TABLE `order_transaction`
    ADD `amount` float;
ALTER TABLE `importing_transaction`
    ADD `amount` float;
ALTER TABLE `exporting_transaction`
    ADD `amount` float;
ALTER TABLE `importing_return_transaction`
    ADD `amount` float;
ALTER TABLE `exporting_return_transaction`
    ADD `amount` float;
UPDATE order_transaction otr SET otr.amount = (otr.price*otr.quantity) WHERE otr.amount = 0.0 or otr.amount is null;
UPDATE importing_transaction itr SET itr.amount = (itr.price*itr.quantity) WHERE itr.amount = 0.0 or itr.amount is null;
UPDATE exporting_transaction etr SET etr.amount = (etr.price*etr.quantity) WHERE etr.amount = 0.0 or etr.amount is null;
UPDATE importing_return_transaction itr SET itr.amount = (itr.price*itr.quantity) WHERE itr.amount = 0.0 or itr.amount is null;
UPDATE exporting_return_transaction etr SET etr.amount = (etr.price*etr.quantity) WHERE etr.amount = 0.0 or etr.amount is null;
