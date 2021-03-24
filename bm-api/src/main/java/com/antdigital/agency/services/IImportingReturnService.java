package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IImportingReturnService {
    ImportingReturnSearchDto search(HttpServletRequest request, ImportingReturnSearchDto importingReturnSearchDto, String agencyId) throws IOException, JAXBException;
    List<ImportingReturnDto> findAll(String agencyId);
    BaseSearchDto<List<ImportingReturnDto>> findAll(BaseSearchDto<List<ImportingReturnDto>> searchDto, String agencyId);
    ImportingReturnDto getById(String id);
    String getNumber(String createdDate, String agencyId);
    Double getTotal(String id);
    List<ImportingReturnTransactionDto> getTransactionById(String importReturnId);
    ImportingReturnFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    ImportingReturnDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    int countByExportId(String exportId);
    ImportingReturnFullDto insert(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto);
    ImportingReturnFullDto update(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto);
    boolean delete(HttpServletRequest request, String id);
}
