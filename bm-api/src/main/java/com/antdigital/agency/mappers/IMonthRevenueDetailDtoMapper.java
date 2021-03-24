package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.MonthRevenueDetail;
import com.antdigital.agency.dtos.response.MonthRevenueDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMonthRevenueDetailDtoMapper {
    IMonthRevenueDetailDtoMapper INSTANCE = Mappers.getMapper( IMonthRevenueDetailDtoMapper.class );

    List<MonthRevenueDetailDto> toMonthRevenueDtoList(List<MonthRevenueDetail> monthRevenueDetails);
}
