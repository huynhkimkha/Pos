package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ImportingWarehouseDetailDto extends ImportingWarehouseDto{
    private String customerCode;
    private String customerName;
    private Double total;
    private Float totalQuantity;
}
