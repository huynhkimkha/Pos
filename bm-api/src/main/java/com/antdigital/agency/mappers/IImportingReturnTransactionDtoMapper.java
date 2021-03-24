package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ImportingReturnTransaction;
import com.antdigital.agency.dtos.response.ImportingReturnTransactionDto;
import com.antdigital.agency.dtos.response.ImportingReturnTransactionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IImportingReturnTransactionDtoMapper {
    IImportingReturnTransactionDtoMapper INSTANCE = Mappers.getMapper( IImportingReturnTransactionDtoMapper.class );

    ImportingReturnTransactionDto toImportReturnTransactionDto(ImportingReturnTransaction importingReturnTransaction);

    ImportingReturnTransaction toImportReturnTransaction(ImportingReturnTransactionDto importingReturnTransactionDto);

    @Mapping(source = "merchandise.id", target = "merchandiseId")
    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    @Mapping(source = "creditAccountPurchase.id", target = "creditAccountPurchase")
    @Mapping(source = "debitAccountPurchase.id", target = "debitAccountPurchase")
    ImportingReturnTransaction toImportReturnTransaction(ImportingReturnTransactionFullDto importingReturnTransactionFullDto);

    @Mapping(source = "merchandiseId", target = "merchandise.id")
    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    @Mapping(source = "creditAccountPurchase", target = "creditAccountPurchase.id")
    @Mapping(source = "debitAccountPurchase", target = "debitAccountPurchase.id")
    ImportingReturnTransactionFullDto toImportReturnTransactionFullDto(ImportingReturnTransaction importReturnTransaction);

    List<ImportingReturnTransactionDto> toImportReturnTransactionDtoList(List<ImportingReturnTransaction> importingReturnTransactionList);

    List<ImportingReturnTransactionFullDto> toImportingReturnTransactionFullDtos(List<ImportingReturnTransaction> importingReturnTransactionList);
}
