package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.ExportingReturnDto;
import com.antdigital.agency.dtos.response.ExportingReturnFullDto;
import com.antdigital.agency.dtos.response.ExportingReturnSearchDto;
import com.antdigital.agency.dtos.response.ExportingReturnTransactionDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IExportingReturnService {
    ExportingReturnSearchDto search(HttpServletRequest request, ExportingReturnSearchDto exportingReturnSearchDto, String agencyId) throws IOException, JAXBException;
    List<ExportingReturnDto> findAll(String agencyId);
    BaseSearchDto<List<ExportingReturnDto>> findAll(BaseSearchDto<List<ExportingReturnDto>> searchDto, String agencyId);
    ExportingReturnDto getById(String id);
    ExportingReturnFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getNumber(String createdDate, String agencyId);
    ExportingReturnDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    List<ExportingReturnTransactionDto> getByImportingWarehouseId(String importingWarehouseId);
    int countByImportingId(String importId);
    ExportingReturnFullDto insert(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto);
    ExportingReturnFullDto update(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto);
    boolean delete(HttpServletRequest request, String id);
}
