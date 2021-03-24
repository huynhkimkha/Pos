package com.antdigital.agency.dtos.request;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.dtos.response.OrderDetailDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderSearchDto extends BaseSearchDto<List<OrderDetailDto>> {
    private String code;
    private String number;
    private String customerCode;
    private String title;
    private String merchandiseCode;
    private ImportStatusEnum importStatus;
    private DeliveryStatusEnum deliverStatus;
    private Date startDate;
    private Date endDate;
    private Integer startNumber;
    private Integer endNumber;
}
