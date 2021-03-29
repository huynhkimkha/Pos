package com.antdigital.agency.dtos.response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CustomersDto {
    private String id;
    @NotEmpty(message = "Tên khách hàng không được trống")
    private String fullName;
    private String address;
    @NotEmpty(message = "Số điện thoại không được trống")
    private String phone;
    private Date created_date;
    private Date updated_date;
}
