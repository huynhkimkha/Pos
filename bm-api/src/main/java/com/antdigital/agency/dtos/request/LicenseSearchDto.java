package com.antdigital.agency.dtos.request;

import com.antdigital.agency.common.deserializers.TrimStringDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Date;

@Data
public class LicenseSearchDto {
    private Date fromDate;
    private Date toDate;
    private String code;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String customerCode;
    private String customerGroup1Id;
    private String customerGroup2Id;
    private String customerGroup3Id;
    private String accountingTableId;
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String merchandiseCode;
    private String merchandiseGroup1Id;
    private String merchandiseGroup2Id;
    private String merchandiseGroup3Id;
    private String productGroupId;
    private String agencyId;
    private String companyId;
}
