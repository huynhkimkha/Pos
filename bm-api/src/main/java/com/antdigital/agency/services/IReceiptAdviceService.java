package com.antdigital.agency.services;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReceiptAdviceSearchDto;
import com.antdigital.agency.dtos.response.ReceiptAdviceDto;
import com.antdigital.agency.dtos.response.ReceiptAdviceFullDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
public interface IReceiptAdviceService {
    List<ReceiptAdviceDto> findAll(String agencyId);
    BaseSearchDto<List<ReceiptAdviceDto>> findAll(BaseSearchDto<List<ReceiptAdviceDto>> searchDto, String agencyId);
    ReceiptAdviceDto getById(String id, String agencyId);
    ReceiptAdviceFullDto getFullById(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException;
    String getReceiptAdviceNumber(String createdDate, String agencyId);
    ReceiptAdviceDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    int countByExportId(String exportId);
    ReceiptAdviceFullDto insert(ReceiptAdviceFullDto receiptAdviceFullDto, String agencyId);
    ReceiptAdviceFullDto update(ReceiptAdviceFullDto receiptAdviceFullDto, String agencyId);
    boolean delete(String id, String agencyId);
    ReceiptAdviceSearchDto search(HttpServletRequest request, ReceiptAdviceSearchDto receiptAdviceSearchDto, String agencyId) throws JAXBException, IOException;
}
