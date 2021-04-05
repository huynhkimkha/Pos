package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dtos.response.CostDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ICostDtoMapper {
    ICostDtoMapper INSTANCE = Mappers.getMapper(ICostDtoMapper.class );

    CostDto toCostDto(Cost cost);

    Cost toCost(CostDto costFullDto);

    List<CostDto> toCostDtoList(List<Cost> costList);
}
