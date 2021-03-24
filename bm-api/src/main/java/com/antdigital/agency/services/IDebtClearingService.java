package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.DebtClearingDto;
import com.antdigital.agency.dtos.response.DebtClearingFullDto;
import com.antdigital.agency.dtos.response.DebtClearingSearchDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IDebtClearingService {
    List<DebtClearingDto> findAll(String agencyId);
    BaseSearchDto<List<DebtClearingDto>> findAll(BaseSearchDto<List<DebtClearingDto>> searchDto, String agencyId);
    DebtClearingSearchDto search(HttpServletRequest request, DebtClearingSearchDto debtClearingSearchDto, String agencyId) throws IOException, JAXBException;
    DebtClearingDto getById(String id, String agencyId);
    String getDebtClearingNumber(String createdDate, String agencyId);
    DebtClearingFullDto getFullById(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException;
    int countByExportId(String exportId);
    DebtClearingFullDto insert(DebtClearingFullDto debtClearingFullDto, String agencyId);
    DebtClearingFullDto update(DebtClearingFullDto debtClearingFullDto, String agencyId);
    boolean delete(String id, String agencyId);
    DebtClearingDto getByCodeAndNumber(String code, String number, int year, String agencyId);
}
