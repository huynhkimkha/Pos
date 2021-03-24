package com.antdigital.agency.dtos.request;

import com.antdigital.agency.dtos.response.PaymentAdviceSearchDetailDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PaymentAdviceSearchDto extends BaseSearchDto<List<PaymentAdviceSearchDetailDto>> {
    private String code;
    private String number;
    private String customerCode;
    private String customerAddress;
    private String description;
    private String note;
    private Date startDate;
    private Date endDate;
    private Integer startNumber;
    private Integer endNumber;
}
