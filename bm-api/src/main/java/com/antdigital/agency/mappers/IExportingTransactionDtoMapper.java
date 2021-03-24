package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ExportingTransaction;
import com.antdigital.agency.dtos.response.ExportingTransactionDto;
import com.antdigital.agency.dtos.response.ExportingTransactionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IExportingTransactionDtoMapper {
    IExportingTransactionDtoMapper INSTANCE = Mappers.getMapper( IExportingTransactionDtoMapper.class );

    ExportingTransactionDto toExportingTransactionDto(ExportingTransaction exportingTransaction);

    ExportingTransaction toExportingTransaction(ExportingTransactionDto exportingTransactionDto);

    @Mapping(source = "merchandise.id", target = "merchandiseId")
    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    @Mapping(source = "creditAccountPurchase.id", target = "creditAccountPurchase")
    @Mapping(source = "debitAccountPurchase.id", target = "debitAccountPurchase")
    ExportingTransaction toExportingTransaction(ExportingTransactionFullDto exportingTransactionFullDto);

    @Mapping(source = "merchandiseId", target = "merchandise.id")
    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    @Mapping(source = "creditAccountPurchase", target = "creditAccountPurchase.id")
    @Mapping(source = "debitAccountPurchase", target = "debitAccountPurchase.id")
    ExportingTransactionFullDto toExportingTransactionFullDto(ExportingTransaction exportingTransaction);

    List<ExportingTransactionDto> toExportingTransactionDtoList(List<ExportingTransaction> exportingTransactions);
    List<ExportingTransactionFullDto> toExportingTransactionFullDtos(List<ExportingTransaction> exportingTransactions);
}
