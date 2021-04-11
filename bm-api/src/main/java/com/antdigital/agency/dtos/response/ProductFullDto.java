package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.ProductStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ProductFullDto {
    private String id;
    @NotEmpty(message = "Tên không được trống")
    private String name;
    private String nameSlug;
    private Float price;
    private String image;
    private ProductStatusEnum status;
    private String content;
    private Date createdDate;
    private Date updatedDate;
    @NotEmpty(message = "Danh mục không được trống")
    @NotNull(message = "Danh mục không được trống")
    private List<ProductCategoryDto> productCategoryList;
}
