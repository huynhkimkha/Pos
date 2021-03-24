package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import lombok.Data;

@Data
public class MerchandiseStatisticDto {
    private MerchandiseModel merchandise;
    private Double totalRevenue;
}
