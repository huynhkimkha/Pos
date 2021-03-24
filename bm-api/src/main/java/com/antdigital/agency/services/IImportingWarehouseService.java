package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IImportingWarehouseService {
    List<ImportingWarehouseDto> findAll(String agencyId);
    List<ImportingTransactionFullDto> GetByOrderId(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException;
    ImportingWarehouseDto getById(String id);
    ImportingWarehouseFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getNumber(String createdDate, String agencyId);
    ImportingWarehouseDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    List<ImportingWarehouseDto> getNotCompleted(String customerId, String code);
    List<ImportingWarehouseFullDto> getForPayment(String customerId, Double amount);
    List<ImportingWarehouseDto> getLikeCode(String code, String agencyId);
    List<ImportingTransactionDto> getImportingTransactionForReturn(String customerId, String merchandiseId, Float quantity);
    Double getTotal(String id);
    Double getDebt(String customerId, String agencyId);
    float countByOrder(String orderId);
    ImportingWarehouseFullDto insert(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto);
    ImportingWarehouseFullDto update(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto);
    boolean delete(HttpServletRequest request, String id);
    ImportingWarehouseSearchDto search(HttpServletRequest request, ImportingWarehouseSearchDto importingWarehouseSearchDto, String agencyId) throws JAXBException, IOException;
}
