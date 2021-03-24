package com.antdigital.agency.dal.dao;

import com.antdigital.agency.dal.data.PaymentAdviceSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;

import java.util.Date;
import java.util.List;

public interface IPaymentAdviceDao {
    SearchResult<List<PaymentAdviceSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId);
    List<PaymentAdviceSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId);

}
