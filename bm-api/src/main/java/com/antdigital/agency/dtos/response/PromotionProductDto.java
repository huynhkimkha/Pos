package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class PromotionProductDto {
    private String id;
    private ProductDto product;
    private PromotionDto promotion;
}
