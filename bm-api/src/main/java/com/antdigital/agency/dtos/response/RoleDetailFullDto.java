package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class RoleDetailFullDto {
    private String id;
    private RoleFullDto role;
    private EmployeesDto employee;
}
