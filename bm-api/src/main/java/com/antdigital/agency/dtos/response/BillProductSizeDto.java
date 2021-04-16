package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class BillProductSizeDto {
    private String id;
    private BillDto bill;
    private ProductSizeDto productSize;
    private Float price;
    private Float quantity;
}
