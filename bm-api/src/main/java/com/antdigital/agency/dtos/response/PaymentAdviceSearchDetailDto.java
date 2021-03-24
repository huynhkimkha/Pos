package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class PaymentAdviceSearchDetailDto extends PaymentAdviceDto{
    private String customerCode;
    private String customerName;
    private Double total;
}
