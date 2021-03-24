package com.antdigital.agency.dtos.response;


import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import lombok.Data;

@Data
public class ReceiptDetailFullDto {
    private String id;
    private ReceiptDto receipt;
    private String description;
    private Double amount;
    private ExportingWarehouseDto exportingWarehouse;
    private AccountingTableModel creditAccount;
    private AccountingTableModel debitAccount;
}
