package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ExportingWarehouseDetail;
import com.antdigital.agency.dal.entity.ExportingWarehouse;
import com.antdigital.agency.dtos.response.ExportingWarehouseDetailDto;
import com.antdigital.agency.dtos.response.ExportingWarehouseDto;
import com.antdigital.agency.dtos.response.ExportingWarehouseFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IExportingWarehouseDtoMapper {
    IExportingWarehouseDtoMapper INSTANCE = Mappers.getMapper( IExportingWarehouseDtoMapper.class );

    ExportingWarehouseDto toExportingWarehouseDto(ExportingWarehouse exportingWarehouse);

    ExportingWarehouse toExportingWarehouse(ExportingWarehouseDto exportingWarehouseDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    ExportingWarehouse toExportingWarehouse(ExportingWarehouseFullDto exportingWarehouseFullDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ExportingWarehouseFullDto toExportingWarehouseFullDto(ExportingWarehouse exportingWarehouse);

    List<ExportingWarehouseDto> toExportingWarehouseDtoList(List<ExportingWarehouse> exportingWarehouses);

    List<ExportingWarehouseDetailDto> toExportWarehouseDetailDtoList(List<ExportingWarehouseDetail> exportingWarehouseDetails);

    List<ExportingWarehouseFullDto> toExportWarehouseFullDtoList(List<ExportingWarehouse> exportingWarehouses);

}
