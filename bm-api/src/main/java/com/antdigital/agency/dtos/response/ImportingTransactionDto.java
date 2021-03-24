package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ImportingTransactionDto {
    private String id;
    private ImportingWarehouseDto importingWarehouse;
    private OrderDto order;
    private String merchandiseId;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
    private String creditAccount;
    private String debitAccount;
}
