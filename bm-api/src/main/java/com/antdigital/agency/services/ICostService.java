package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CostDto;

import java.util.List;

public interface ICostService {
    BaseSearchDto<List<CostDto>> findAll(BaseSearchDto<List<CostDto>> searchDto, String agencyId);
    List<CostDto> findAll(String agencyId);
    CostDto getCostById(String id);
    CostDto update(CostDto customersDto);
    CostDto insert(CostDto customersDto);
    boolean deleteCost(String id);
}
