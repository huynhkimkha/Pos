package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;

import java.util.List;

public interface IBillService {
    List<BillDto> findAll(String agencyId);
    BaseSearchDto<List<BillDto>> findAll(BaseSearchDto<List<BillDto>> searchDto, String agencyId);
    BillFullDto getBillFull(String billId);
    BillFullDto insert(BillFullDto billFullDto);
    BillFullDto update(BillFullDto billFullDto);
    boolean delete(String id);
    String getNumber(String createdDate, String agencyId);
    List<MonthBillDetailDto> getMonthBill(RangeDateDto rangeDateDto, String agencyId);
    List<DateBillDetailDto> getDateBill(RangeDateDto rangeDateDto, String agencyId);
    List<YearBillDetailDto> getYearBill(RangeDateDto rangeDateDto, String agencyId);
    List<BillDto> getBillStatistic(RangeDateDto rangeDateDto, String agencyId);
}
