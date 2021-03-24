package com.antdigital.agency.dtos.response;


import lombok.Data;

@Data
public class ReceiptDetailDto {
    private String id;
    private ReceiptDto receipt;
    private String description;
    private Double amount;
    private ExportingWarehouseDto exportingWarehouse;
    private String creditAccount;
    private String debitAccount;
}
