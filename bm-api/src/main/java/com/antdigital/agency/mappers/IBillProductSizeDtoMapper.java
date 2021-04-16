package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.BillProductSize;
import com.antdigital.agency.dtos.response.BillProductSizeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IBillProductSizeDtoMapper {
    IBillProductSizeDtoMapper INSTANCE = Mappers.getMapper(IBillProductSizeDtoMapper.class);

    BillProductSize toBillProductSize(BillProductSizeDto billProductSizeDto);

    BillProductSizeDto toBillProductSizeDto(BillProductSize billProductSize);

    List<BillProductSizeDto> toBillProductSizeDtoList(List<BillProductSize> billProductSizeList);
}
