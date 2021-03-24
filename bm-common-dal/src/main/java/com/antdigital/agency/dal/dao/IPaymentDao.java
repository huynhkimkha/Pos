package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.*;

import java.util.Date;
import java.util.List;

public interface IPaymentDao {
    SearchResult<List<PaymentSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId);
    List<PaymentSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);
    List<MonthCostDetail> getMonthCost(String fromDate, String toDate, String accountingId, String agencyId);
    List<DateCostDetail> getDateCost(String fromDate, String toDate, String accountingId, String agencyId);
    List<YearCostDetail> getYearCost(String fromDate, String toDate, String accountingId, String agencyId);
}
