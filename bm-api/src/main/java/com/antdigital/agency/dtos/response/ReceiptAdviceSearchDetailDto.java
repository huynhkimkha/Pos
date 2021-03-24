package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ReceiptAdviceSearchDetailDto extends ReceiptAdviceDto{
    private String customerCode;
    private String customerName;
    private Double total;
}
