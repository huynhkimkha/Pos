package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.MaterialDto;

import java.util.List;

public interface IMaterialService {
    List<MaterialDto> findAll();
    BaseSearchDto<List<MaterialDto>> findAll(BaseSearchDto<List<MaterialDto>> searchDto);
    MaterialDto getById(String id);
    MaterialDto insert(MaterialDto materialDto);
    MaterialDto update(MaterialDto materialDto);
    List<MaterialDto> getLikeName(String name);
    boolean delete(String id);
}
