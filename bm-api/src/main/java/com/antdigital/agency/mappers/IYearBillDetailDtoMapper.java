package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.YearBillDetail;
import com.antdigital.agency.dtos.response.YearBillDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IYearBillDetailDtoMapper {
    IYearBillDetailDtoMapper INSTANCE = Mappers.getMapper( IYearBillDetailDtoMapper.class );

    List<YearBillDetailDto> toYearBillDtoList(List<YearBillDetail> yearBillDetails);
}
