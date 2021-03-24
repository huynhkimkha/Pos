package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ImportingReturnSearchDetail;
import com.antdigital.agency.dal.entity.ImportingReturn;
import com.antdigital.agency.dtos.response.ImportingReturnDetailDto;
import com.antdigital.agency.dtos.response.ImportingReturnDto;
import com.antdigital.agency.dtos.response.ImportingReturnFullDto;
import org.mapstruct.Mapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface IImportingReturnDtoMapper {
    IImportingReturnDtoMapper INSTANCE = Mappers.getMapper( IImportingReturnDtoMapper.class );

    ImportingReturnDto toImportReturnDto(ImportingReturn importingReturn);

    ImportingReturn toImportWarehouse(ImportingReturnDto importingReturnDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    ImportingReturn toImportReturn(ImportingReturnFullDto importingReturnFullDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ImportingReturnFullDto toImportReturnFullDto(ImportingReturn importingReturn);

    List<ImportingReturnDto> toImportReturnDtoList(List<ImportingReturn> importingReturnList);

    List<ImportingReturnDetailDto> toImportingReturnDetailDtoList(List<ImportingReturnSearchDetail> importingReturnSearchDetails);
}
