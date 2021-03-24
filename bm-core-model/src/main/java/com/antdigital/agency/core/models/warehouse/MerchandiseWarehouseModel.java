package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

@Data
public class MerchandiseWarehouseModel {
    private String id;
    private MerchandiseModel merchandise;
    private String agencyId;
    private Float quantity;
    private Float conversionQuantity;
}
