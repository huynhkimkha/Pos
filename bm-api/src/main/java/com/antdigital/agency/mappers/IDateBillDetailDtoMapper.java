package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.DateBillDetail;
import com.antdigital.agency.dtos.response.DateBillDetailDto;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface IDateBillDetailDtoMapper {
    IDateBillDetailDtoMapper INSTANCE = Mappers.getMapper( IDateBillDetailDtoMapper.class );

    List<DateBillDetailDto> toDateBillDtoList(List<DateBillDetail> dateBillDetails);
}
