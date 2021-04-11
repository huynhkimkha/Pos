package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ProductCategoryDto {
    private String id;
    private CategoriesDto category;
    private ProductDto product;
}
