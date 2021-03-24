package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.DebtReport;

import java.util.Date;
import java.util.List;

public interface IDebtReportingDao {
    List<DebtReport> getDebtReports(Date fromDate, Date toDate, String customerId);
}
