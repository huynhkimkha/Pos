package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class OrderTransactionDto {
    private String id;
    private OrderDto order;
    private String merchandiseId;
    private Float quantity;
    private Float conversionRate;
    private Double price;
    private Double amount;
    private Double conversionPrice;
}
