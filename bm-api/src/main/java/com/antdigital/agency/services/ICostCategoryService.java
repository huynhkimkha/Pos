package com.antdigital.agency.services;

import com.antdigital.agency.dtos.response.CostCategoryDto;

import java.util.List;

public interface ICostCategoryService {
    List<CostCategoryDto> findAll();

}
