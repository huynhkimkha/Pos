package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.CostCategory;
import com.antdigital.agency.dtos.response.CostCategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ICostCategoryDtoMapper {
    ICostCategoryDtoMapper INSTANCE = Mappers.getMapper(ICostCategoryDtoMapper.class);

    CostCategoryDto toCostCategoryDto(CostCategory CostCategory);

    CostCategory toCostCategory(CostCategoryDto CostCategoryFullDto);

    List<CostCategoryDto> toCostCategoryDtoList(List<CostCategory> CostCategoryList);
}
