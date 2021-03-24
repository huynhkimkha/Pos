package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ImportingReturnTransactionDto {
    private String id;
    private ImportingReturnDto importingReturn;
    private ExportingWarehouseDto exportingWarehouse;
    private String merchandiseId;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
    private Double costOfGoodsSold;
    private String creditAccount;
    private String debitAccount;
    private String creditAccountPurchase;
    private String debitAccountPurchase;
}
