package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ImportingWarehouseSearchDto extends BaseSearchDto<List<ImportingWarehouseDetailDto>> {
    private String code;
    private String number;
    private String customerCode;
    private String customerAddress;
    private String description;
    private String note;
    private String merchandiseCode;
    private PaymentStatusEnum paymentStatus;
    private Date startDate;
    private Date endDate;
    private Integer startNumber;
    private Integer endNumber;
}
