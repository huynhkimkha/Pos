package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.CategoryStatusEnum;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CategoriesDto {
    private String id;
    @NotEmpty(message = "Tên không được trống")
    private String name;
    private CategoryStatusEnum status;
    private Date createdDate;
    private Date updatedDate;
}
