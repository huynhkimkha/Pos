package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ExportingWarehouseDetailDto extends ExportingWarehouseDto{
    private String customerCode;
    private String customerName;
    private Double total;
    private Float totalQuantity;
    private Double purchaseTotal;
}
