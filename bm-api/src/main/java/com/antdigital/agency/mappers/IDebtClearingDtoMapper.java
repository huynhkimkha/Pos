package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.DebtClearingSearchDetail;
import com.antdigital.agency.dal.entity.DebtClearing;
import com.antdigital.agency.dtos.response.DebtClearingDetailDto;
import com.antdigital.agency.dtos.response.DebtClearingDto;
import com.antdigital.agency.dtos.response.DebtClearingFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IDebtClearingDtoMapper {
    IDebtClearingDtoMapper INSTANCE = Mappers.getMapper( IDebtClearingDtoMapper.class );

    DebtClearingDto toDebtClearingDto (DebtClearing debtClearing);

    DebtClearing toDebtClearing (DebtClearingDto debtClearingDto);

    DebtClearingFullDto toDebtClearingFullDto(DebtClearing debtClearing);

    DebtClearing toDebtClearing(DebtClearingFullDto debtClearingFullDto);

    List<DebtClearingDto> toDebtClearingDtoList(List<DebtClearing> debtClearings);

    List<DebtClearingDetailDto> toDebtClearingDetailDto(List<DebtClearingSearchDetail> debtClearingSearchDetailList);
}
