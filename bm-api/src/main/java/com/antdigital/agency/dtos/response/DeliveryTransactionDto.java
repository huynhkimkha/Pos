package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class DeliveryTransactionDto {
    private String id;
    private DeliveryDto delivery;
    private String merchandiseId;
    private Float quantity;
    private Float conversionQuantity;
}
