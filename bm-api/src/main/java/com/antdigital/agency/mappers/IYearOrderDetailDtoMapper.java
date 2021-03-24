package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.YearOrderDetail;
import com.antdigital.agency.dtos.response.YearOrderDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IYearOrderDetailDtoMapper {
    IYearOrderDetailDtoMapper INSTANCE = Mappers.getMapper( IYearOrderDetailDtoMapper.class );

    List<YearOrderDetailDto> toYearOrderDtoList(List<YearOrderDetail> yearOrderDetails);
}
