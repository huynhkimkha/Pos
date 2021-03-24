package com.antdigital.agency.services;

import com.antdigital.agency.dtos.response.GrantPermissionDto;

import java.util.List;
public interface IGrantPermissionService {
    List<GrantPermissionDto> getByRoleId(String id);
}
