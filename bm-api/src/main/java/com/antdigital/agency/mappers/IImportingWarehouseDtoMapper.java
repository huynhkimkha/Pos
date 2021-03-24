package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ImportingWarehouseDetail;
import com.antdigital.agency.dal.entity.ImportingWarehouse;
import com.antdigital.agency.dtos.response.ImportingWarehouseDetailDto;
import com.antdigital.agency.dtos.response.ImportingWarehouseDto;
import com.antdigital.agency.dtos.response.ImportingWarehouseFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IImportingWarehouseDtoMapper {
    IImportingWarehouseDtoMapper INSTANCE = Mappers.getMapper( IImportingWarehouseDtoMapper.class );

    ImportingWarehouseDto toImportWarehouseDto(ImportingWarehouse importingWarehouse);

    ImportingWarehouse toImportWarehouse(ImportingWarehouseDto importingWarehouseDto);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    ImportingWarehouse toImportWarehouse(ImportingWarehouseFullDto importingWarehouseFullDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ImportingWarehouseFullDto toImportWarehouseFullDto(ImportingWarehouse importingWarehouse);

    List<ImportingWarehouseDto> toImportWarehouseDtoList(List<ImportingWarehouse> importingWarehouseList);

    List<ImportingWarehouseDetailDto> toImportWarehouseDetailDtoList(List<ImportingWarehouseDetail> importingWarehouseDetails);

    List<ImportingWarehouseFullDto> toImportWarehouseFullDtoList(List<ImportingWarehouse> importingWarehouses);
}
