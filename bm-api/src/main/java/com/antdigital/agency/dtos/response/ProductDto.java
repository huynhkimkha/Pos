package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.ProductStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class ProductDto {
    private String id;
    @NotEmpty(message = "Tên không được trống")
    private String name;
    private String nameSlug;
    private String image;
    private ProductStatusEnum status;
    private String content;
    private Date createdDate;
    private Date updatedDate;
}
