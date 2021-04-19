package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.CustomerTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CustomersDto {
    private String id;
    private CustomerTypeEnum customerType;
    @NotEmpty(message = "Tên khách hàng không được trống")
    private String fullName;
    private String address;
    @NotEmpty(message = "Số điện thoại không được trống")
    private String phone;
    private Date birthDate;
    private Date createdDate;
    private Date updatedDate;
}
