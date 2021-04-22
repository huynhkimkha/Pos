package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ImportingTransactionDto {
    private String id;
    private ImportingMaterialDto importingMaterial;
    private MaterialDto material;
    private Float quantity;
    private Float price;
    private Float amount;
}
