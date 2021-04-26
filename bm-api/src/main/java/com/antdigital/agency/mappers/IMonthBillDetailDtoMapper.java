package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.MonthBillDetail;
import com.antdigital.agency.dtos.response.MonthBillDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMonthBillDetailDtoMapper {
    IMonthBillDetailDtoMapper INSTANCE = Mappers.getMapper( IMonthBillDetailDtoMapper.class );

    List<MonthBillDetailDto> toMonthBillDtoList(List<MonthBillDetail> monthBillDetails);
}
