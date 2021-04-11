package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CategoriesDto;

import java.util.List;

public interface ICategoriesService {
    List<CategoriesDto> findAll();
    BaseSearchDto<List<CategoriesDto>> findAll(BaseSearchDto<List<CategoriesDto>> searchDto);
    CategoriesDto getByName(String name);
    CategoriesDto getById(String id);
    List<CategoriesDto> getLikeName(String name);
    CategoriesDto insert(CategoriesDto categoriesDto);
    CategoriesDto update(CategoriesDto categoriesDto);
    boolean delete(String id);
    boolean isCategoryUsed(String id);
}
