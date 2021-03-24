package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ExportingReturnTransaction;
import com.antdigital.agency.dtos.response.ExportingReturnTransactionDto;
import com.antdigital.agency.dtos.response.ExportingReturnTransactionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IExportingReturnTransactionDtoMapper {
    IExportingReturnTransactionDtoMapper INSTANCE = Mappers.getMapper( IExportingReturnTransactionDtoMapper.class );

    ExportingReturnTransactionDto toExportingReturnTransactionDto(ExportingReturnTransaction exportingReturnTransaction);

    ExportingReturnTransaction toExportingReturnTransaction(ExportingReturnTransactionDto exportingReturnTransactionDto);

    @Mapping(source = "merchandise.id", target = "merchandiseId")
    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    ExportingReturnTransaction toExportingReturnTransaction(ExportingReturnTransactionFullDto exportingReturnTransactionFullDto);

    @Mapping(source = "merchandiseId", target = "merchandise.id")
    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    ExportingReturnTransactionFullDto toExportingReturnTransactionFullDto(ExportingReturnTransaction exportingReturnTransaction);

    List<ExportingReturnTransactionDto> toExportingReturnTransactionDtoList(List<ExportingReturnTransaction> exportingReturnTransactions);
    List<ExportingReturnTransactionFullDto> toExportingReturnTransactionFullDtos(List<ExportingReturnTransaction> exportingReturnTransactions);

}
