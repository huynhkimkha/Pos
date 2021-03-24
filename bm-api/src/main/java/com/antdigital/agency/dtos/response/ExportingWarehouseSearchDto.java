package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExportingWarehouseSearchDto extends BaseSearchDto<List<ExportingWarehouseDetailDto>> {
    private String code;
    private String number;
    private String customerCode;
    private String customerAddress;
    private String description;
    private String note;
    private String merchandiseCode;
    private Date startDate;
    private Date endDate;
    private Integer startNumber;
    private Integer endNumber;
    private PaymentStatusEnum paymentStatus;
}
