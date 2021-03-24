package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.RoleDetail;
import com.antdigital.agency.dtos.response.RoleDetailDto;
import com.antdigital.agency.dtos.response.RoleDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IRoleDetailDtoMapper {
    IRoleDetailDtoMapper INSTANCE = Mappers.getMapper( IRoleDetailDtoMapper.class );

    RoleDetailDto toRoleDetailDto(RoleDetail roleDetail);
    RoleDetailFullDto toRoleDetailFullDto(RoleDetail roleDetail);

    RoleDetail toRoleDetail(RoleDetailDto roleDetailDto);
    RoleDetail toRoleDetailFull(RoleDetailFullDto roleDetailDto);

    List<RoleDetailDto> toRoleDetailListDto(List<RoleDetail> roleDetail);
    List<RoleDetailFullDto> toRoleDetailFullListDto(List<RoleDetail> roleDetail);
}
