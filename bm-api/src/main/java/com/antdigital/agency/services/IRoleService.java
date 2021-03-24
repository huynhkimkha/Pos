package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.RoleDto;
import com.antdigital.agency.dtos.response.RoleFullDto;

import java.util.List;

public interface IRoleService {
    List<RoleDto> findAll(String agencyId);
    BaseSearchDto<List<RoleDto>> findAll(BaseSearchDto<List<RoleDto>> searchDto, String agencyId);
    RoleFullDto insert(RoleFullDto roleFullDto);
    RoleFullDto getByName(String name, String agencyId);
    RoleFullDto getRoleFull(String id);
    RoleFullDto update(RoleFullDto roleFullDto);
    boolean delete(String id);
}
