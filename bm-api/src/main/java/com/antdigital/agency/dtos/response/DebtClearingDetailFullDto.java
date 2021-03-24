package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import lombok.Data;

@Data
public class DebtClearingDetailFullDto {
    private String id;
    private DebtClearingDto debtClearing;
    private String description;
    private Double amount;
    private ExportingWarehouseDto exportingWarehouse;
    private CustomerModel customerDebt;
    private CustomerModel customer;
    private AccountingTableModel creditAccount;
    private AccountingTableModel debitAccount;
}
