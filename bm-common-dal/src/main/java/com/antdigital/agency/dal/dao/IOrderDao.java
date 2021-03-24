package com.antdigital.agency.dal.dao;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.dal.data.*;

import java.util.Date;
import java.util.List;

public interface IOrderDao {
    SearchResult<List<OrderDetail>> search(String code, String number, String title
            , List<String> customerIds, List<String> merchandiseIds,ImportStatusEnum importStatus, DeliveryStatusEnum deliverStatus,Date startDate, Date endDate, Integer startNumber
            , Integer endNumber,String createdDateSort, int start, int size, String agencyId);
    List<MonthOrderDetail> getMonthOrder(String fromDate, String toDate, String agencyId);
    List<DateOrderDetail> getDateOrder(String fromDate, String toDate, String agencyId);
    List<YearOrderDetail> getYearOrder(String fromDate, String toDate, String agencyId);
}
