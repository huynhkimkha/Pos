package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

@Data
public class ExportingTransactionFullDto {
    private String id;
    private ExportingWarehouseDto exportingWarehouse;
    private OrderDto order;
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
