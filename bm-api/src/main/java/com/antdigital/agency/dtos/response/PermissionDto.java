package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PermissionRequirementEnum;
import lombok.Data;

import java.util.Date;

@Data
public class PermissionDto {
    private String code;
    private String name;
    private Date createdDate;
    private Date updatedDate;
    private PermissionRequirementEnum requirement;
}
