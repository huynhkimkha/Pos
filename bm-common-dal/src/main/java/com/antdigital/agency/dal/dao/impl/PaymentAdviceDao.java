package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IPaymentAdviceDao;
import com.antdigital.agency.dal.data.MonthCostDetail;
import com.antdigital.agency.dal.data.PaymentAdviceSearchDetail;
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
public class PaymentAdviceDao extends GenericDao implements IPaymentAdviceDao {

    @Transactional
    public SearchResult<List<PaymentAdviceSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<PaymentAdviceSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String paymentQueryStr = "select p.*, format(substring(p.number,4,4),0) as paymentNumber from payment_advice p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from payment_advice_detail pd INNER JOIN ";
        String countTotalRecords = "select count(*) from ( ";

        // where conditions
        List<String> conditions = new ArrayList<>();
        conditions.add(String.format("p.agency_id = '%s'", agencyId));
        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("p.code LIKE '%s%s%s'", "%", code, "%"));
        }

        if(number != null && !number.isEmpty()) {
            conditions.add(String.format("p.number LIKE '%s%s%s'", "%", number, "%"));
        }

        if(customerAddress != null && !customerAddress.isEmpty()) {
            conditions.add(String.format("p.customer_address LIKE '%s%s%s'", "%", customerAddress, "%"));
        }

        if(description != null && !description.isEmpty()) {
            conditions.add(String.format("p.description LIKE '%s%s%s'", "%", description, "%"));
        }

        if(note != null && !note.isEmpty()) {
            conditions.add(String.format("p.note LIKE '%s%s%s'", "%", note, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null){
            conditions.add(String.format("p.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if(endDate != null){
            conditions.add(String.format("p.created_date <= '%s'", dateFormat.format(endDate)));
        }

        if(customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("p.customer_id in (%s)", customerStr));
        }
        String paymentWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

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
            havingConditions.add(String.format(" paymentNumber >= %s ", startNumber));
        }
        if(endNumber!=null){
            havingConditions.add(String.format(" paymentNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

        //query
        String joinStr = " ("+ paymentQueryStr+" "+paymentWhereStr+havingStr + ") as c ON c.id = pd.payment_advice_id" + " group by pd.payment_advice_id" +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(countTotalQueryStr + joinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<PaymentAdviceSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            PaymentAdviceSearchDetail paymentAdviceSearchDetail = new PaymentAdviceSearchDetail();
            paymentAdviceSearchDetail.setTotal(((Double)row[0]));
            paymentAdviceSearchDetail.setId((String)row[1]);
            paymentAdviceSearchDetail.setCode((String)row[3]);
            paymentAdviceSearchDetail.setNumber((String)row[4]);
            paymentAdviceSearchDetail.setInvoiceDate((Date)row[5]);
            paymentAdviceSearchDetail.setInvoiceCode((String)row[6]);
            paymentAdviceSearchDetail.setInvoiceTemplate((String)row[7]);
            paymentAdviceSearchDetail.setInvoiceSymbol((String)row[8]);
            paymentAdviceSearchDetail.setInvoiceNumber((String)row[9]);
            paymentAdviceSearchDetail.setCustomerAddress((String)row[10]);
            paymentAdviceSearchDetail.setCustomerTaxCode((String)row[11]);
            paymentAdviceSearchDetail.setCustomerId((String)row[12]);
            paymentAdviceSearchDetail.setTransactionCustomerId((String)row[13]);
            paymentAdviceSearchDetail.setDescription((String)row[14]);
            paymentAdviceSearchDetail.setNote((String)row[15]);
            paymentAdviceSearchDetail.setCreatedDate((Date)row[16]);
            paymentAdviceSearchDetail.setCustomerCode("");
            paymentAdviceSearchDetail.setCustomerName("");

            result.add(paymentAdviceSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords + countTotalQueryStr + joinStr+ " ) as v").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<PaymentAdviceSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        if (merchandiseIds != null) {
            return new ArrayList<>();
        }
        Session session = getSession();
        String paymentAdviceQueryStr = "select p.*, format(substring(p.number,4,4),0) as paymentAdviceNumber from payment_advice p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from payment_advice_detail pd INNER JOIN ";
        String transactionQueryStr = "select p.* from payment_advice_detail t ";

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
        String paymentAdviceWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> detailConditions = new ArrayList<>();
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            detailConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }
        String detailWhereStr = detailConditions.size() > 0 ? " where " + String.join(" and ", detailConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ paymentAdviceQueryStr +" "+ paymentAdviceWhereStr + ") as p ON p.id = t.payment_advice_id" + detailWhereStr + " group by t.payment_advice_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = pd.payment_advice_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<PaymentAdviceSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            PaymentAdviceSearchDetail paymentAdviceSearchDetail = new PaymentAdviceSearchDetail();
            paymentAdviceSearchDetail.setTotal(((Double)row[0]));
            paymentAdviceSearchDetail.setId((String)row[1]);
            paymentAdviceSearchDetail.setCode((String)row[3]);
            paymentAdviceSearchDetail.setNumber((String)row[4]);
            paymentAdviceSearchDetail.setInvoiceDate((Date)row[5]);
            paymentAdviceSearchDetail.setInvoiceCode((String)row[6]);
            paymentAdviceSearchDetail.setInvoiceTemplate((String)row[7]);
            paymentAdviceSearchDetail.setInvoiceSymbol((String)row[8]);
            paymentAdviceSearchDetail.setInvoiceNumber((String)row[9]);
            paymentAdviceSearchDetail.setCustomerAddress((String)row[10]);
            paymentAdviceSearchDetail.setCustomerTaxCode((String)row[11]);
            paymentAdviceSearchDetail.setCustomerId((String)row[12]);
            paymentAdviceSearchDetail.setTransactionCustomerId((String)row[13]);
            paymentAdviceSearchDetail.setDescription((String)row[14]);
            paymentAdviceSearchDetail.setNote((String)row[15]);
            paymentAdviceSearchDetail.setCreatedDate((Date)row[16]);
            paymentAdviceSearchDetail.setCustomerCode("");
            paymentAdviceSearchDetail.setCustomerName("");

            result.add(paymentAdviceSearchDetail);
        }
        return result;
    }

}
