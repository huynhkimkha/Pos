package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ImportingTransaction;
import com.antdigital.agency.dtos.response.ImportingTransactionDto;
import com.antdigital.agency.dtos.response.ImportingTransactionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IImportingTransactionDtoMapper {
    IImportingTransactionDtoMapper INSTANCE = Mappers.getMapper( IImportingTransactionDtoMapper.class );

    ImportingTransactionDto toImportTransactionDto(ImportingTransaction importingTransaction);

    ImportingTransaction toImportTransaction(ImportingTransactionDto importingTransactionDto);

    @Mapping(source = "merchandise.id", target = "merchandiseId")
    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    ImportingTransaction toImportTransaction(ImportingTransactionFullDto importingTransactionFullDto);

    @Mapping(source = "merchandiseId", target = "merchandise.id")
    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    ImportingTransactionFullDto toImportTransactionFullDto(ImportingTransaction importTransaction);

    List<ImportingTransactionDto> toImportTransactionDtoList(List<ImportingTransaction> importingTransactionList);

    List<ImportingTransactionFullDto> toImportTransactionFullDtoList(List<ImportingTransaction> importingTransactionList);
}
