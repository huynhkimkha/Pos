package com.antdigital.agency.services;
import com.antdigital.agency.dal.entity.PaymentAdvice;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.PaymentAdviceSearchDto;
import com.antdigital.agency.dtos.response.PaymentAdviceDetailDto;
import com.antdigital.agency.dtos.response.PaymentAdviceDto;
import com.antdigital.agency.dtos.response.PaymentAdviceFullDto;
import com.antdigital.agency.dtos.response.PaymentDetailDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
public interface IPaymentAdviceService {
    List<PaymentAdviceDto> findAll(String agencyId);
    PaymentAdviceDto getById(String id);
    PaymentAdviceFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getPaymentAdviceNumber(String createdDate, String agencyId);
    PaymentAdviceDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    int countByImportingId(String exportId);
    PaymentAdviceFullDto insert(PaymentAdviceFullDto paymentAdviceFullDto);
    PaymentAdviceFullDto insertPaymentCommission(PaymentAdviceFullDto paymentAdviceFullDto);
    PaymentAdviceFullDto update(PaymentAdviceFullDto paymentAdviceFullDto);
    PaymentAdviceFullDto updatePaymentCommission(PaymentAdviceFullDto paymentAdviceFullDto);
    boolean delete(String id);
    PaymentAdviceSearchDto search(HttpServletRequest request, PaymentAdviceSearchDto paymentAdviceSearchDto, String agencyId) throws JAXBException, IOException;
    List<PaymentAdviceDetailDto> getByRefferalBonusId(String id);
}
