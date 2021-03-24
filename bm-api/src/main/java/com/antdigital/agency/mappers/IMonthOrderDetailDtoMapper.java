package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.MonthOrderDetail;
import com.antdigital.agency.dtos.response.MonthOrderDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMonthOrderDetailDtoMapper {
    IMonthOrderDetailDtoMapper INSTANCE = Mappers.getMapper( IMonthOrderDetailDtoMapper.class );

    List<MonthOrderDetailDto> toMonthOrderDtoList(List<MonthOrderDetail> monthOrderDetails);
}
