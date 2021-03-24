package com.antdigital.agency.dtos.response;
import java.util.Date;

import com.antdigital.agency.common.enums.CommissionTypeEnum;
import com.antdigital.agency.common.enums.UserModelEnum;
import lombok.Data;

@Data
public class InvoiceCommissionDto {
    private String id;
    private String companyId;
    private String name;
    private CommissionTypeEnum commissionType;
    private UserModelEnum applyObject;
    private Double minRevenue;
    private Double bonus;
    private Date createdDate;
}
