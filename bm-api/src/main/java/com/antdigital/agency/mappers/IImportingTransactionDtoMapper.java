package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ImportingTransaction;
import com.antdigital.agency.dtos.response.ImportingTransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IImportingTransactionDtoMapper {
    IImportingTransactionDtoMapper INSTANCE = Mappers.getMapper(IImportingTransactionDtoMapper.class);

    ImportingTransaction toImportingTransaction(ImportingTransactionDto ImportingTransactionDto);

    ImportingTransactionDto toImportingTransactionDto(ImportingTransaction ImportingTransaction);

    List<ImportingTransactionDto> toImportingTransactionDtoList(List<ImportingTransaction> ImportingTransactionList);
}
