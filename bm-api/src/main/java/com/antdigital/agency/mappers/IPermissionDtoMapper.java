package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Permission;
import com.antdigital.agency.dtos.response.PermissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPermissionDtoMapper {
    IPermissionDtoMapper INSTANCE = Mappers.getMapper( IPermissionDtoMapper.class );

    PermissionDto toPermissionDto(Permission permission);

    Permission toPermission(PermissionDto permissionDto);

    List<PermissionDto> toPermissionDtoList(List<Permission> permissionList);
}
