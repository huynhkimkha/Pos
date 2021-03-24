package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dtos.response.AgencyDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IAgencyDtoMapper {
    IAgencyDtoMapper INSTANCE = Mappers.getMapper( IAgencyDtoMapper.class );

    AgencyDto toAgencyDto(Agency agency);

    Agency toAgency(AgencyDto agencyDto);

    List<AgencyDto> toAgencyDtoList(List<Agency> agencyList);
}
