package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.YearCostDetail;
import com.antdigital.agency.dtos.response.YearCostDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IYearCostDetailDtoMapper {
    IYearCostDetailDtoMapper INSTANCE = Mappers.getMapper( IYearCostDetailDtoMapper.class );

    List<YearCostDetailDto> toYearCostDtoList(List<YearCostDetail> yearCostDetails);
}
