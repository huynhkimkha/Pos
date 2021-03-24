package com.antdigital.agency.services;

import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.dtos.request.DebtReportSearchDto;
import com.antdigital.agency.dtos.response.DebtReportDto;
import com.antdigital.agency.dtos.response.MonthlyClosingBalanceDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface IDebtReportService {
    List<MonthlyClosingBalanceDto> getDebt(HttpServletRequest request, DebtReportSearchDto debtReportSearchDto) throws IOException, JAXBException;
    List<DebtReportDto> getDebtReports(HttpServletRequest request, DebtReportSearchDto debtReportSearchDto, boolean isCollectForGetDebt) throws IOException, JAXBException;
    void saveMonthlyClosingBalance(HttpServletRequest request, Date fromDate, Date toDate, String agencyId) throws IOException, JAXBException, ParseException;
    void updateMonthlyClosingBalance(HttpServletRequest request, Date createdDate, CustomerModel customer, String agencyId) throws IOException, JAXBException;
    void deleteMonthlyClosingBalance(Date toDate, String agencyId);
}
