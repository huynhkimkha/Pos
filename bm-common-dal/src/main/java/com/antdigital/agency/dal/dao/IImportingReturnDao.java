package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.ImportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;

import java.util.Date;
import java.util.List;

public interface IImportingReturnDao {
    List<ImportingReturnSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);
    SearchResult<List<ImportingReturnSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds, Date startDate, Date endDate, String createdDateSort
            , Integer startNumber, Integer endNumber, int start, int size, String agencyId);
}
