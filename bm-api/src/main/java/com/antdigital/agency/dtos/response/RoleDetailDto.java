package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class RoleDetailDto {
    private String id;
    private RoleDto role;
    private EmployeesDto employee;
}
