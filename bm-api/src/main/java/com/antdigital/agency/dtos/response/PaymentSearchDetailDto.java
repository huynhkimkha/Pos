package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class PaymentSearchDetailDto extends PaymentDto{
    private String customerCode;
    private String customerName;
    private Double total;
}
