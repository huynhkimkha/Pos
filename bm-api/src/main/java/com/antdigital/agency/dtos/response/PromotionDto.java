package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.StatusPromotionEnum;
import com.antdigital.agency.common.enums.TypePromotionEnum;
import lombok.Data;

import java.util.Date;

@Data
public class PromotionDto {
    private String id;
    private String name;
    private String description;
    private Float amount;
    private TypePromotionEnum typePromotion;
    private StatusPromotionEnum status;
    private Date expiredDate;
    private Date createdDate;
    private Date updatedDate;
}
