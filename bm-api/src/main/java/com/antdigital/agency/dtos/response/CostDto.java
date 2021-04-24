package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class CostDto {
    private String id;
    private AgencyDto agency;
    private CostCategoryDto costCategory;
    private String code;
    private String number;
    private Float amount;
    private String description;
    private Date createdDate;
    private Date updatedDate;
}
