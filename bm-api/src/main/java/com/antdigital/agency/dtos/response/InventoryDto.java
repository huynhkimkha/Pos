package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class InventoryDto {
    private String id;
    private AgencyDto agency;
    private MaterialDto material;
    private Float amount;
    private Float amountCheck;
    private String description;
}
