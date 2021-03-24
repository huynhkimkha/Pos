package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Role;
import com.antdigital.agency.dtos.response.RoleDto;
import com.antdigital.agency.dtos.response.RoleFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IRoleDtoMapper {
    IRoleDtoMapper INSTANCE = Mappers.getMapper( IRoleDtoMapper.class );

    RoleDto toRoleDto(Role role);
    RoleFullDto toRoleFullDto(Role role);
    Role toRole(RoleFullDto roleDto);

    List<RoleDto> toRoleDtoList(List<Role> roleList);
}
