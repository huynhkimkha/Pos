package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.ExportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;

import java.util.Date;
import java.util.List;

public interface IExportingReturnDao {
    List<ExportingReturnSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);
    SearchResult<List<ExportingReturnSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId);
}
