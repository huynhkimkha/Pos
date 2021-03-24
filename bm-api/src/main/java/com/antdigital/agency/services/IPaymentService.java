package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IPaymentService {
    List<MonthCostDetailDto> getMonthCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException;
    List<DateCostDetailDto> getDateCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException;
    List<YearCostDetailDto> getYearCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException;
    List<PaymentDto> findAll(String agencyId);
    BaseSearchDto<List<PaymentDto>> findAll(BaseSearchDto<List<PaymentDto>> searchDto, String agencyId);
    PaymentDto getById(String id);
    PaymentFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getPaymentNumber(String createdDate, String agencyId);
    PaymentDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    int countByImportingId(String exportId);
    PaymentFullDto insert(PaymentFullDto paymentFullDto);
    PaymentFullDto insertPaymentCommission(PaymentFullDto paymentFullDto);
    PaymentFullDto update(PaymentFullDto paymentFullDto);
    PaymentFullDto updatePaymentCommission(PaymentFullDto paymentFullDto);
    boolean delete(String id);
    PaymentSearchDto search(HttpServletRequest request, PaymentSearchDto paymentSearchDto, String agencyId) throws JAXBException, IOException;
    List<PaymentDetailDto> getByRefferalBonusId(String id);
}
