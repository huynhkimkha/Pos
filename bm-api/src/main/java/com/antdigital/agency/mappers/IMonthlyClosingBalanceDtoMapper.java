package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.MonthlyClosingBalance;
import com.antdigital.agency.dtos.response.MonthlyClosingBalanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMonthlyClosingBalanceDtoMapper {
    IMonthlyClosingBalanceDtoMapper INSTANCE = Mappers.getMapper( IMonthlyClosingBalanceDtoMapper.class );

    MonthlyClosingBalance toMonthlyClosingBalance(MonthlyClosingBalanceDto monthlyClosingBalanceDto);
    MonthlyClosingBalanceDto toMonthlyClosingBalanceDto(MonthlyClosingBalance monthlyClosingBalance);
    List<MonthlyClosingBalanceDto> toMonthlyClosingBalanceDtos(List<MonthlyClosingBalance> monthlyClosingBalances);
}
