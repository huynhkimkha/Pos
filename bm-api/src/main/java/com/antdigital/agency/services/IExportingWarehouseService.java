package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IExportingWarehouseService {
    List<ExportingWarehouseDto> findAll(String agencyId);
    List<MonthRevenueDetailDto> getMonthRevenue(RangeDateDto rangeDateDto, String agencyId);
    List<DateRevenueDetailDto> getDateRevenue(RangeDateDto rangeDateDto, String agencyId);
    List<YearRevenueDetailDto> getYearRevenue(RangeDateDto rangeDateDto, String agencyId);
    BaseSearchDto<List<ExportingWarehouseDto>> findAll(BaseSearchDto<List<ExportingWarehouseDto>> searchDto, String agencyId);
    ExportingWarehouseDto getById(String id);
    List<ExportingTransactionFullDto> GetByOrderId(HttpServletRequest request, String id) throws IOException, JAXBException;
    ExportingWarehouseFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException;
    String getNumber(String createdDate, String agencyId);
    List<ExportingWarehouseDto> getLikeCode(String code, String agencyId);
    List<ExportingWarehouseFullDto> getForReceipt(String customerId, Double amount, String agencyId);
    List<ExportingTransactionDto> getExportingTransactionForReturn(String customerId, String merchandiseId, Float quantity, String agencyId);
    List<CustomerStatisticDto> getCustomerBaseOnSpent(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId)throws JAXBException, IOException;
    List<MerchandiseStatisticDto> getMerchandiseBestSold(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId)throws JAXBException, IOException;
    ExportingWarehouseDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    List<ExportingWarehouseDto> getPaymentNotCompleted(String customerId, String code, String agencyId);
    List<ExportingWarehouseDto> getPaymentNotCompletedCode(String code, String agencyId);
    Double getTotal(String id);
    Double getDebt(String customerId, String agencyId);
    int countByOrder(String orderId);
    ExportingWarehouseFullDto insert(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto, String companyId);
    ExportingWarehouseFullDto update(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto, String companyId);
    boolean delete(HttpServletRequest request, String id);
    ExportingWarehouseSearchDto search(HttpServletRequest request, ExportingWarehouseSearchDto exportingWarehouseSearchDto, String agencyId) throws JAXBException, IOException;
    List<ExportingWarehouseDetailDto> revenueReport(HttpServletRequest request, LicenseSearchDto licenseSearchDto)
            throws IOException, JAXBException;
}
