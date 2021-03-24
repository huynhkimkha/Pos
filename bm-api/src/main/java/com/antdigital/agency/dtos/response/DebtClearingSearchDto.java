package com.antdigital.agency.dtos.response;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DebtClearingSearchDto extends BaseSearchDto<List<DebtClearingDetailDto>> {
    private String code;
    private String number;
    private String startNumber;
    private String endNumber;
    private String customerDebtCode;
    private Date startDate;
    private Date endDate;
    private String note;
    private String description;
    private String customerCode;
    private String exportingWarehouseCode;
}
