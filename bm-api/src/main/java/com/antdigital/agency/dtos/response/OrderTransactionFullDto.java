package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

@Data
public class OrderTransactionFullDto {
    private String id;
    private OrderDto order;
    private MerchandiseModel merchandise;
    private Float quantity;
    private Float conversionQuantity;
    private Double price;
    private Double amount;
    private Double conversionPrice;
}
