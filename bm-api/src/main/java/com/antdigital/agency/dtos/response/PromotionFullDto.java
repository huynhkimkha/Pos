package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.StatusPromotionEnum;
import com.antdigital.agency.common.enums.TypePromotionEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class PromotionFullDto {
    private String id;
    private String name;
    private String description;
    private Float amount;
    private TypePromotionEnum typePromotion;
    private StatusPromotionEnum status;
    private Date expiredDate;
    private Date createdDate;
    private Date updatedDate;
    @NotEmpty(message = "Sản phẩm không được trống")
    @NotNull(message = "Sản phẩm không được trống")
    private List<PromotionProductDto> promotionProductList;
}
