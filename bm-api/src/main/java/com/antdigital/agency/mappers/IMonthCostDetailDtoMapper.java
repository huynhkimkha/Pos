package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.MonthCostDetail;
import com.antdigital.agency.dtos.response.MonthCostDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMonthCostDetailDtoMapper {
    IMonthCostDetailDtoMapper INSTANCE = Mappers.getMapper( IMonthCostDetailDtoMapper.class );

    List<MonthCostDetailDto> toMonthCostDtoList(List<MonthCostDetail> monthCostDetails);
}
