package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ReceiptAdviceDetailDto {
    private String id;
    private ReceiptAdviceDto receiptAdvice;
    private String description;
    private Double amount;
    private ExportingWarehouseDto exportingWarehouse;
    private String creditAccount;
    private String debitAccount;
}
