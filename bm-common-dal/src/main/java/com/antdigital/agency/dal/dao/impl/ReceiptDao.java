package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IReceiptDao;
import com.antdigital.agency.dal.data.ReceiptSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;

import com.antdigital.agency.dal.entity.Agency;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ReceiptDao extends GenericDao implements IReceiptDao {

    @Transactional
    public SearchResult<List<ReceiptSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<ReceiptSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String receiptQueryStr = "select r.*, format(substring(r.number,4,4),0) as receiptNumber from receipt r ";
        String countTotalQueryStr = "select sum(rd.amount) as total, c.* from receipt_detail rd INNER JOIN ";
        String countTotalRecords = "select count(*) from ( ";

        // where conditions
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()) {
            conditions.add(String.format("r.agency_id = '%s'", agencyId));
        }

        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("r.code LIKE '%s%s%s'", "%", code, "%"));
        }

        if(number != null && !number.isEmpty()) {
            conditions.add(String.format("r.number LIKE '%s%s%s'", "%", number, "%"));
        }

        if(customerAddress != null && !customerAddress.isEmpty()) {
            conditions.add(String.format("r.customer_address LIKE '%s%s%s'", "%", customerAddress, "%"));
        }

        if(description != null && !description.isEmpty()) {
            conditions.add(String.format("r.description LIKE '%s%s%s'", "%", description, "%"));
        }

        if(note != null && !note.isEmpty()) {
            conditions.add(String.format("r.note LIKE '%s%s%s'", "%", note, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null){
            conditions.add(String.format("r.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if(endDate != null){
            conditions.add(String.format("r.created_date <= '%s'", dateFormat.format(endDate)));
        }

        if(customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("r.customer_id in (%s)", customerStr));
        }
        String receiptWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> sortConditions = new ArrayList<>();
        if(createdDateSort != null){
            if(createdDateSort.equals("desc")){
                sortConditions.add(String.format("c.created_date desc, c.number desc"));
            }
            if(createdDateSort.equals("asc")){
                sortConditions.add(String.format("c.created_date asc, c.number asc"));
            }
        }
        else {
            sortConditions.add(String.format("c.created_date desc, c.number desc"));
        }
        String sortStr = sortConditions.size() > 0 ? " order by " + String.join(" and ", sortConditions) : "";

        // having conditions
        List<String> havingConditions = new ArrayList<>();
        if(startNumber!=null){
            havingConditions.add(String.format(" receiptNumber >= %s ", startNumber));
        }
        if(endNumber!=null){
            havingConditions.add(String.format(" receiptNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

        //query
        String joinStr = " ("+ receiptQueryStr+" "+receiptWhereStr+havingStr + ") as c ON c.id = rd.receipt_id" + " group by rd.receipt_id"  +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(countTotalQueryStr + joinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<ReceiptSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ReceiptSearchDetail receiptSearchDetail = new ReceiptSearchDetail();
            receiptSearchDetail.setTotal(((Double)row[0]));
            receiptSearchDetail.setId((String)row[1]);
            receiptSearchDetail.setCode((String)row[3]);
            receiptSearchDetail.setNumber((String)row[4]);
            receiptSearchDetail.setInvoiceDate((Date)row[5]);
            receiptSearchDetail.setInvoiceCode((String)row[6]);
            receiptSearchDetail.setInvoiceTemplate((String)row[7]);
            receiptSearchDetail.setInvoiceSymbol((String)row[8]);
            receiptSearchDetail.setInvoiceNumber((String)row[9]);
            receiptSearchDetail.setCustomerAddress((String)row[10]);
            receiptSearchDetail.setCustomerTaxCode((String)row[11]);
            receiptSearchDetail.setCustomerId((String)row[12]);
            receiptSearchDetail.setTransactionCustomerId((String)row[13]);
            receiptSearchDetail.setDescription((String)row[14]);
            receiptSearchDetail.setNote((String)row[15]);
            receiptSearchDetail.setCreatedDate((Date)row[16]);
            receiptSearchDetail.setCustomerCode("");
            receiptSearchDetail.setCustomerName("");

            result.add(receiptSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords + countTotalQueryStr + joinStr+ " ) as v").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<ReceiptSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        if (merchandiseIds != null) {
            return new ArrayList<>();
        }
        Session session = getSession();
        String receiptQueryStr = "select p.*, format(substring(p.number,4,4),0) as receiptNumber from receipt p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from receipt_detail pd INNER JOIN ";
        String transactionQueryStr = "select p.* from receipt_detail t ";

        // where conditions
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()){
            conditions.add(String.format("p.agency_id = '%s'", agencyId));
        }

        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("p.code LIKE '%s%s%s'", "%", code, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(fromDate != null){
            conditions.add(String.format("p.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if(toDate != null){
            conditions.add(String.format("p.created_date <= '%s'", dateFormat.format(toDate)));
        }

        if(customerIds != null) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("p.customer_id in (%s)", customerStr));
        }
        String receiptWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> detailConditions = new ArrayList<>();
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            detailConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }
        String detailWhereStr = detailConditions.size() > 0 ? " where " + String.join(" and ", detailConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ receiptQueryStr +" "+ receiptWhereStr + ") as p ON p.id = t.receipt_id" + detailWhereStr + " group by t.receipt_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = pd.receipt_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ReceiptSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ReceiptSearchDetail receiptSearchDetail = new ReceiptSearchDetail();
            receiptSearchDetail.setTotal(((Double)row[0]));
            receiptSearchDetail.setId((String)row[1]);
            receiptSearchDetail.setCode((String)row[3]);
            receiptSearchDetail.setNumber((String)row[4]);
            receiptSearchDetail.setInvoiceDate((Date)row[5]);
            receiptSearchDetail.setInvoiceCode((String)row[6]);
            receiptSearchDetail.setInvoiceTemplate((String)row[7]);
            receiptSearchDetail.setInvoiceSymbol((String)row[8]);
            receiptSearchDetail.setInvoiceNumber((String)row[9]);
            receiptSearchDetail.setCustomerAddress((String)row[10]);
            receiptSearchDetail.setCustomerTaxCode((String)row[11]);
            receiptSearchDetail.setCustomerId((String)row[12]);
            receiptSearchDetail.setTransactionCustomerId((String)row[13]);
            receiptSearchDetail.setDescription((String)row[14]);
            receiptSearchDetail.setNote((String)row[15]);
            receiptSearchDetail.setCreatedDate((Date)row[16]);
            receiptSearchDetail.setCustomerCode("");
            receiptSearchDetail.setCustomerName("");

            result.add(receiptSearchDetail);
        }
        return result;
    }
}
