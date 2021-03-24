package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class EmployeeDetailDto extends EmployeesDto{
    private ReferralBonusDto referee;
    private Double unpaidBonus;
}
