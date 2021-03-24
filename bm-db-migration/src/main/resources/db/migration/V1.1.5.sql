UPDATE debt_clearing_detail dcd SET dcd.amount = ROUND(dcd.amount, 0);
UPDATE exporting_return_transaction ert set ert.amount = ROUND(ert.amount, 0), ert.conversion_price = ROUND(ert.conversion_price, 0), ert.price = ROUND(ert.price, 0);
UPDATE exporting_transaction et set et.amount = ROUND(et.amount, 0), et.conversion_price = ROUND(et.conversion_price, 0), et.price = ROUND(et.price, 0);
UPDATE importing_return_transaction irt SET irt.amount = ROUND(irt.amount, 0), irt.conversion_price = ROUND(irt.conversion_price, 0), irt.price = ROUND(irt.price, 0);
UPDATE importing_transaction it set it.amount = ROUND(it.amount, 0), it.conversion_price = ROUND(it.conversion_price, 0), it.price = ROUND(it.price, 0);
UPDATE order_transaction ot SET ot.amount = ROUND(ot.amount, 0), ot.conversion_price = ROUND(ot.conversion_price, 0), ot.price = ROUND(ot.price, 0);
UPDATE payment_detail pd SET pd.amount = ROUND(pd.amount, 0);
UPDATE payment_advice_detail padt SET padt.amount = ROUND(padt.amount, 0);
UPDATE receipt_detail rd SET rd.amount = ROUND(rd.amount, 0);
UPDATE receipt_advice_detail radt Set radt.amount = ROUND(radt.amount, 0);
UPDATE referral_bonus rb SET rb.amount = ROUND(rb.amount, 0);
UPDATE selling_bonus sb SET sb.amount = ROUND(sb.amount, 0);
UPDATE monthly_closing_balance mcb SET mcb.debit_balance = ROUND(mcb.debit_balance, 0), mcb.credit_balance = ROUND(mcb.credit_balance, 0);

INSERT INTO settings(`label`, `key`, `value`) VALUES
('Vị trí thập phân muốn làm tròn', 'DECIMAL_PLACE', '0');
