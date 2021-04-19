package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.InventoryDto;

import java.util.List;

public interface IInventoryService {
    List<InventoryDto> findAll(String agencyId);
    BaseSearchDto<List<InventoryDto>> findAll(BaseSearchDto<List<InventoryDto>> searchDto, String agencyId);
    InventoryDto getById(String id);
    InventoryDto insert(InventoryDto materialDto);
    InventoryDto update(InventoryDto materialDto);
    boolean delete(String id);
}
