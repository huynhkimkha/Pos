package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ReceiptSearchDetailDto extends ReceiptDto{
    private String customerCode;
    private String customerName;
    private Double total;
}

