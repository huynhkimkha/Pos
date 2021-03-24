package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.ReceiptDto;
import com.antdigital.agency.dtos.response.ReceiptFullDto;
import com.antdigital.agency.dtos.response.ReceiptSearchDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IReceiptService {
    List<ReceiptDto> findAll(String agencyId);
    BaseSearchDto<List<ReceiptDto>> findAll(BaseSearchDto<List<ReceiptDto>> searchDto, String agencyId);
    ReceiptDto getById(String id);
    ReceiptFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getReceiptNumber(String createdDate, String agencyId);
    ReceiptDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    int countByExportId(String exportId);
    ReceiptFullDto insert(ReceiptFullDto receiptFullDto);
    ReceiptFullDto update(ReceiptFullDto receiptFullDto);
    boolean delete(String id);
    ReceiptSearchDto search(HttpServletRequest request, ReceiptSearchDto receiptSearchDto, String agencyId) throws JAXBException, IOException;

}
