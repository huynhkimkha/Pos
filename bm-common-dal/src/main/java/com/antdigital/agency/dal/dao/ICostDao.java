package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.DateCostDetail;
import com.antdigital.agency.dal.data.MonthCostDetail;
import com.antdigital.agency.dal.data.YearCostDetail;

import java.util.List;

public interface ICostDao {
    List<MonthCostDetail> getMonthCost(String fromDate, String toDate, String agencyId);
    List<DateCostDetail> getDateCost(String fromDate, String toDate, String agencyId);
    List<YearCostDetail> getYearCost(String fromDate, String toDate, String agencyId);
}
