package com.antdigital.agency.mappers;


import com.antdigital.agency.dal.entity.DebtClearingDetail;
import com.antdigital.agency.dtos.response.DebtClearingDetailDto;
import com.antdigital.agency.dtos.response.DebtClearingDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IDebtClearingDetailDtoMapper {
    IDebtClearingDetailDtoMapper INSTANCE = Mappers.getMapper( IDebtClearingDetailDtoMapper.class );

    DebtClearingDetailDto toDebtClearingDetalDto (DebtClearingDetail debtClearingDetail);

    DebtClearingDetail toDebtClearingDetail(DebtClearingDetailDto debtClearingDetailDto);

    @Mapping(source = "customerDebtId", target = "customerDebt.id")
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    DebtClearingDetailFullDto toDebtClearingDetailFullDto (DebtClearingDetail debtClearingDetail);

    @Mapping(source = "customerDebt.id", target = "customerDebtId")
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    DebtClearingDetail toDebtClearingDetail (DebtClearingDetailFullDto debtClearingDetailFullDto);

    List<DebtClearingDetailDto> toDebtClearingDetailDtoList(List<DebtClearingDetail> debtClearingDetails);

    List<DebtClearingDetailFullDto> toDebtClearingDetailFullDtoList(List<DebtClearingDetail> debtClearingDetails);
}
