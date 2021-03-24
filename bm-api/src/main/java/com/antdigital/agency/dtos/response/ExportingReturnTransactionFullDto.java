package com.antdigital.agency.dtos.response;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

@Data
public class ExportingReturnTransactionFullDto {
    private String id;
    private ExportingReturnDto exportingReturn;
    private ImportingWarehouseDto importingWarehouse;
    private MerchandiseModel merchandise;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
    private AccountingTableModel creditAccount;
    private AccountingTableModel debitAccount;
}
