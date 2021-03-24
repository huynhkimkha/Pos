package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class GrantPermissionDto {
    private String id;
    private RoleDto role;
    private PermissionDto permission;
}
