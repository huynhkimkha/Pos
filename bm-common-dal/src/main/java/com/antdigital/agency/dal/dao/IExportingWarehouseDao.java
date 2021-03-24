package com.antdigital.agency.dal.dao;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dal.data.*;

import java.util.Date;
import java.util.List;

public interface IExportingWarehouseDao {
    SearchResult<List<ExportingWarehouseDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds, PaymentStatusEnum paymentStatus, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId);
    List<ExportingWarehouseDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);
    List<CustomerStatistic> getCustomerBaseOnSpent(String fromDate, String toDate, String agencyId);
    List<MerchandiseStatistic> getMerchandiseBestSold(String fromDate, String toDate, String agencyId);
    List<MonthRevenueDetail> getMonthRevenue(String fromDate, String toDate, String agencyId);
    List<DateRevenueDetail> getDateRevenue(String fromDate, String toDate, String agencyId);
    List<YearRevenueDetail> getYearRevenue(String fromDate, String toDate, String agencyId);
}
