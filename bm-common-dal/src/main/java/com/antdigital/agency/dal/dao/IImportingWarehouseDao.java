package com.antdigital.agency.dal.dao;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dal.data.ImportingWarehouseDetail;
import com.antdigital.agency.dal.data.SearchResult;

import java.util.Date;
import java.util.List;

public interface IImportingWarehouseDao {
    SearchResult<List<ImportingWarehouseDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds, PaymentStatusEnum paymentStatus, Date startDate, Date endDate,String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId);
    List<ImportingWarehouseDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);

}
