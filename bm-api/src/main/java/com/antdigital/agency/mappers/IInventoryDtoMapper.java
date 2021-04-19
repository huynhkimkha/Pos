package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Inventory;
import com.antdigital.agency.dtos.response.InventoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IInventoryDtoMapper {
    IInventoryDtoMapper INSTANCE = Mappers.getMapper( IInventoryDtoMapper.class );

    InventoryDto toInventoryDto(Inventory Inventory);

    Inventory toInventory(InventoryDto InventoryFullDto);

    List<InventoryDto> toInventoryDtoList(List<Inventory> InventoryList);
}
