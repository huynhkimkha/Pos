package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Bill;
import com.antdigital.agency.dtos.response.BillFullDto;
import com.antdigital.agency.dtos.response.BillDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IBillDtoMapper {
    IBillDtoMapper INSTANCE = Mappers.getMapper(IBillDtoMapper.class);

    List<BillDto> toBillDtoList(List<Bill> billList);

    Bill toBill(BillFullDto billDto);

    BillFullDto toBillFullDto(Bill bill);
}
