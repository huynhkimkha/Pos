package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.ImportingMaterialDto;
import com.antdigital.agency.dtos.response.ImportingMaterialFullDto;

import java.util.List;

public interface IImportingMaterialService {
    List<ImportingMaterialDto> findAll(String agencyId);
    BaseSearchDto<List<ImportingMaterialDto>> findAll(BaseSearchDto<List<ImportingMaterialDto>> searchDto, String agencyId);
    ImportingMaterialFullDto getFullById(String id);
    ImportingMaterialFullDto insert(ImportingMaterialFullDto importingMaterialFullDto);
    ImportingMaterialFullDto update(ImportingMaterialFullDto importingMaterialFullDto);
    boolean delete(String id);
    String getNumber(String createdDate, String agencyId);
}
