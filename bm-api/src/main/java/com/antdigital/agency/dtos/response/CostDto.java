package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.TypeCostEnum;
import lombok.Data;

import java.util.Date;

@Data

public class CostDto {
    private String id;
    private TypeCostEnum typeCost;
    private float amount;
    private String description;
    private Date createdDate;
    private Date updatedDate;
}
