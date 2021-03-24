package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.DateCostDetail;
import com.antdigital.agency.dtos.response.DateCostDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IDateCostDetailDtoMapper {
    IDateCostDetailDtoMapper INSTANCE = Mappers.getMapper( IDateCostDetailDtoMapper.class );

    List<DateCostDetailDto> toDateCostDtoList(List<DateCostDetail> dateCostDetails);
}
