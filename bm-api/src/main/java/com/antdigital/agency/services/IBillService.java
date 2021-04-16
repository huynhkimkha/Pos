package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.BillFullDto;
import com.antdigital.agency.dtos.response.BillDto;

import java.util.List;

public interface IBillService {
    List<BillDto> findAll(String agencyId);
    BaseSearchDto<List<BillDto>> findAll(BaseSearchDto<List<BillDto>> searchDto, String agencyId);
    BillFullDto getBillFull(String billId);
    BillFullDto insert(BillFullDto billFullDto);
    BillFullDto update(BillFullDto billFullDto);
    boolean delete(String id);
    String getNumber(String createdDate, String agencyId);
}
