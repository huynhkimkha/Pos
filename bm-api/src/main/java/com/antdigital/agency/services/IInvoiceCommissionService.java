package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.InvoiceCommissionDto;

import java.util.List;

public interface IInvoiceCommissionService {
    BaseSearchDto<List<InvoiceCommissionDto>> findAll(BaseSearchDto<List<InvoiceCommissionDto>> searchDto, String companyId);
    List<InvoiceCommissionDto> findAll(String companyId);
    InvoiceCommissionDto getById(String id);
    InvoiceCommissionDto getByRevenue(Double minRevenue, String companyId);
    InvoiceCommissionDto getByName(String name, String companyId);
    InvoiceCommissionDto getExactByObjectAndRevenue(String applyObject, Double minRevenue, String companyId);
    List<String> getIdByPrice(Double price, String companyId);
    InvoiceCommissionDto insert(InvoiceCommissionDto invoiceCommissionDto);
    InvoiceCommissionDto update(InvoiceCommissionDto invoiceCommissionDto);
    boolean delete(String id);
}
