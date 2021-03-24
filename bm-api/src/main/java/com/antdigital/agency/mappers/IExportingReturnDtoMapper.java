package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ExportingReturnSearchDetail;
import com.antdigital.agency.dal.entity.ExportingReturn;
import com.antdigital.agency.dtos.response.ExportingReturnDetailDto;
import com.antdigital.agency.dtos.response.ExportingReturnDto;
import com.antdigital.agency.dtos.response.ExportingReturnFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IExportingReturnDtoMapper {
    IExportingReturnDtoMapper INSTANCE = Mappers.getMapper( IExportingReturnDtoMapper.class );

    ExportingReturnDto toExportingReturnDto(ExportingReturn exportingReturn);

    ExportingReturn toExportingReturn(ExportingReturnDto exportingReturnDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    ExportingReturn toExportingReturn(ExportingReturnFullDto exportingReturnFullDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ExportingReturnFullDto toExportingReturnFullDto(ExportingReturn exportingReturn);

    List<ExportingReturnDto> toExportingReturnDtoList(List<ExportingReturn> exportingReturns);

    List<ExportingReturnDetailDto> toExportReturnDetailDtoList(List<ExportingReturnSearchDetail> exportingReturnSearchDetailList);
}
