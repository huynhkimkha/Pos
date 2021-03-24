package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.DateOrderDetail;
import com.antdigital.agency.dtos.response.DateOrderDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IDateOrderDetailDtoMapper {
    IDateOrderDetailDtoMapper INSTANCE = Mappers.getMapper( IDateOrderDetailDtoMapper.class );

    List<DateOrderDetailDto> toDateOrderDtoList(List<DateOrderDetail> dateOrderDetails);
}
