package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IReceiptAdviceDao;
import com.antdigital.agency.dal.data.ReceiptAdviceSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ReceiptAdviceDao extends GenericDao implements IReceiptAdviceDao {

    @Transactional
    public SearchResult<List<ReceiptAdviceSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<ReceiptAdviceSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String receiptQueryStr = "select r.*, format(substring(r.number,4,4),0) as receiptNumber from receipt_advice r ";
        String countTotalQueryStr = "select sum(rd.amount) as total, c.* from receipt_advice_detail rd INNER JOIN ";
        String countTotalRecords = "select count(*) from ( ";

        // where conditions
        List<String> conditions = new ArrayList<>();
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

        if(agencyId != null){
            conditions.add(String.format("r.agency_id = '%s'", agencyId));
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
        String joinStr = " ("+ receiptQueryStr+" "+receiptWhereStr+havingStr + ") as c ON c.id = rd.receipt_advice_id" + " group by rd.receipt_advice_id" +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(countTotalQueryStr + joinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<ReceiptAdviceSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ReceiptAdviceSearchDetail receiptAdviceSearchDetail = new ReceiptAdviceSearchDetail();
            receiptAdviceSearchDetail.setTotal(((Double)row[0]));
            receiptAdviceSearchDetail.setId((String)row[1]);
            receiptAdviceSearchDetail.setCode((String)row[3]);
            receiptAdviceSearchDetail.setNumber((String)row[4]);
            receiptAdviceSearchDetail.setInvoiceDate((Date)row[5]);
            receiptAdviceSearchDetail.setInvoiceCode((String)row[6]);
            receiptAdviceSearchDetail.setInvoiceTemplate((String)row[7]);
            receiptAdviceSearchDetail.setInvoiceSymbol((String)row[8]);
            receiptAdviceSearchDetail.setInvoiceNumber((String)row[9]);
            receiptAdviceSearchDetail.setCustomerAddress((String)row[10]);
            receiptAdviceSearchDetail.setCustomerTaxCode((String)row[11]);
            receiptAdviceSearchDetail.setCustomerId((String)row[12]);
            receiptAdviceSearchDetail.setTransactionCustomerId((String)row[13]);
            receiptAdviceSearchDetail.setDescription((String)row[14]);
            receiptAdviceSearchDetail.setNote((String)row[15]);
            receiptAdviceSearchDetail.setCreatedDate((Date)row[16]);
            receiptAdviceSearchDetail.setCustomerCode("");
            receiptAdviceSearchDetail.setCustomerName("");

            result.add(receiptAdviceSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords + countTotalQueryStr + joinStr+ " ) as v").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<ReceiptAdviceSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        if (merchandiseIds != null) {
            return new ArrayList<>();
        }
        Session session = getSession();
        String receiptAdviceQueryStr = "select p.*, format(substring(p.number,4,4),0) as receiptAdviceNumber from receipt_advice p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from receipt_advice_detail pd INNER JOIN ";
        String transactionQueryStr = "select p.* from receipt_advice_detail t ";

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
        String receiptAdviceWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> detailConditions = new ArrayList<>();
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            detailConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }
        String detailWhereStr = detailConditions.size() > 0 ? " where " + String.join(" and ", detailConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ receiptAdviceQueryStr +" "+ receiptAdviceWhereStr + ") as p ON p.id = t.receipt_advice_id" + detailWhereStr + " group by t.receipt_advice_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = pd.receipt_advice_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ReceiptAdviceSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ReceiptAdviceSearchDetail receiptAdviceSearchDetail = new ReceiptAdviceSearchDetail();
            receiptAdviceSearchDetail.setTotal(((Double)row[0]));
            receiptAdviceSearchDetail.setId((String)row[1]);
            receiptAdviceSearchDetail.setCode((String)row[3]);
            receiptAdviceSearchDetail.setNumber((String)row[4]);
            receiptAdviceSearchDetail.setInvoiceDate((Date)row[5]);
            receiptAdviceSearchDetail.setInvoiceCode((String)row[6]);
            receiptAdviceSearchDetail.setInvoiceTemplate((String)row[7]);
            receiptAdviceSearchDetail.setInvoiceSymbol((String)row[8]);
            receiptAdviceSearchDetail.setInvoiceNumber((String)row[9]);
            receiptAdviceSearchDetail.setCustomerAddress((String)row[10]);
            receiptAdviceSearchDetail.setCustomerTaxCode((String)row[11]);
            receiptAdviceSearchDetail.setCustomerId((String)row[12]);
            receiptAdviceSearchDetail.setTransactionCustomerId((String)row[13]);
            receiptAdviceSearchDetail.setDescription((String)row[14]);
            receiptAdviceSearchDetail.setNote((String)row[15]);
            receiptAdviceSearchDetail.setCreatedDate((Date)row[16]);
            receiptAdviceSearchDetail.setCustomerCode("");
            receiptAdviceSearchDetail.setCustomerName("");

            result.add(receiptAdviceSearchDetail);
        }
        return result;
    }
}
