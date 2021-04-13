package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ProductSizeDto {
    private String id;
    private ProductDto product;
    private SizeDto size;
    private Float price;
}
