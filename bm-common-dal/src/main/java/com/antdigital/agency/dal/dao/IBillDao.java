package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.*;

import java.util.List;

public interface IBillDao {
    List<MonthBillDetail> getMonthBill(String fromDate, String toDate, String agencyId);
    List<DateBillDetail> getDateBill(String fromDate, String toDate, String agencyId);
    List<YearBillDetail> getYearBill(String fromDate, String toDate, String agencyId);
}
