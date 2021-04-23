package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CostCategoryDto;

import java.util.List;

public interface ICostCategoryService {
    List<CostCategoryDto> findAll();
    BaseSearchDto<List<CostCategoryDto>> findAll(BaseSearchDto<List<CostCategoryDto>> searchDto);
    CostCategoryDto getCostCategoryById(String id);
    CostCategoryDto update(CostCategoryDto CostCategoryDto);
    CostCategoryDto insert(CostCategoryDto CostCategoryDto);
    boolean deleteCostCategory(String id);
}
