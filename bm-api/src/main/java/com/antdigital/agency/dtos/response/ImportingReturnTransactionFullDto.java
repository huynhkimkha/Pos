package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

@Data
public class ImportingReturnTransactionFullDto {
    private String id;
    private ImportingReturnDto importingReturn;
    private ExportingWarehouseDto exportingWarehouse;
    private MerchandiseModel merchandise;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
    private Double costOfGoodsSold;
    private AccountingTableModel creditAccount;
    private AccountingTableModel debitAccount;
    private AccountingTableModel creditAccountPurchase;
    private AccountingTableModel debitAccountPurchase;
}
