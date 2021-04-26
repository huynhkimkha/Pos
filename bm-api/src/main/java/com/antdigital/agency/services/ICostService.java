package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;

import java.util.List;

public interface ICostService {
    BaseSearchDto<List<CostDto>> findAll(BaseSearchDto<List<CostDto>> searchDto, String agencyId);
    List<CostDto> findAll(String agencyId);
    CostDto getCostById(String id);
    CostDto update(CostDto customersDto);
    CostDto insert(CostDto customersDto);
    boolean deleteCost(String id);
    String getNumber(String createdDate, String agencyId);
    List<MonthCostDetailDto> getMonthCost(RangeDateDto rangeDateDto, String agencyId);
    List<DateCostDetailDto> getDateCost(RangeDateDto rangeDateDto, String agencyId);
    List<YearCostDetailDto> getYearCost(RangeDateDto rangeDateDto, String agencyId);
}
