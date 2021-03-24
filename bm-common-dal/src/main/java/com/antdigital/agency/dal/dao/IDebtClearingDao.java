package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.DebtClearingSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;

import java.util.Date;
import java.util.List;

public interface IDebtClearingDao {
    List<DebtClearingSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);
    SearchResult<List<DebtClearingSearchDetail>> search(String code, String number, String description, String note, List<String> exportingWarehouseIds, List<String> customerIds
            , List<String> customerDebtIds, Date startDate, Date endDate, String createdDateSort, String startNumber, String endNumber, int start, int size, String agencyId);
}
