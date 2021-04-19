package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class MaterialDto {
    private String id;
    private String name;
    private String unit;
    private Float price;
    private String content;
}
