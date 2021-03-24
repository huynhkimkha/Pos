package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

import java.util.Date;

@Data
public class DeliveryTransactionFullDto {
    private String id;
    private DeliveryDto delivery;
    private MerchandiseModel merchandise;
    private Float quantity;
    private Float conversionQuantity;
}
