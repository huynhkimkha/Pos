package com.antdigital.agency.dtos.response;
import lombok.Data;

@Data
public class ExportingReturnTransactionDto {
    private String id;
    private ExportingReturnDto exportingReturn;
    private ImportingWarehouseDto importingWarehouse;
    private String merchandiseId;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
    private String creditAccount;
    private String debitAccount;
}
