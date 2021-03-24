package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class OrderDto {
    private String id;
    private AgencyDto agency;
    private String customerId;
    @NotEmpty(message = "Mã chứng từ không được trống")
    private String code;
    @NotEmpty(message = "Số chứng từ không được trống")
    private String number;
    @NotEmpty(message = "Nội dung không được trống")
    private String title;
    private ImportStatusEnum importStatus;
    private DeliveryStatusEnum deliverStatus;
    @NotNull(message = "Ngày không được trống")
    private Date createdDate;
}
