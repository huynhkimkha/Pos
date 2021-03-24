package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IPaymentDao;
import com.antdigital.agency.dal.data.*;
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
public class PaymentDao extends GenericDao implements IPaymentDao {

    @Transactional
    public SearchResult<List<PaymentSearchDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<PaymentSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String paymentQueryStr = "select p.*, format(substring(p.number,4,4),0) as paymentNumber from payment p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from payment_detail pd INNER JOIN ";
        String countTotalRecords = "select count(*) from ( ";

        // where conditions
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()) {
            conditions.add(String.format("p.agency_id = '%s'", agencyId));
        }

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
        String joinStr = " ("+ paymentQueryStr+" "+paymentWhereStr+havingStr + ") as c ON c.id = pd.payment_id" + " group by pd.payment_id"  +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(countTotalQueryStr + joinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<PaymentSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            PaymentSearchDetail paymentSearchDetail = new PaymentSearchDetail();
            paymentSearchDetail.setTotal(((Double)row[0]));
            paymentSearchDetail.setId((String)row[1]);
            paymentSearchDetail.setCode((String)row[3]);
            paymentSearchDetail.setNumber((String)row[4]);
            paymentSearchDetail.setInvoiceDate((Date)row[5]);
            paymentSearchDetail.setInvoiceCode((String)row[6]);
            paymentSearchDetail.setInvoiceTemplate((String)row[7]);
            paymentSearchDetail.setInvoiceSymbol((String)row[8]);
            paymentSearchDetail.setInvoiceNumber((String)row[9]);
            paymentSearchDetail.setCustomerAddress((String)row[10]);
            paymentSearchDetail.setCustomerTaxCode((String)row[11]);
            paymentSearchDetail.setCustomerId((String)row[12]);
            paymentSearchDetail.setTransactionCustomerId((String)row[13]);
            paymentSearchDetail.setDescription((String)row[14]);
            paymentSearchDetail.setNote((String)row[15]);
            paymentSearchDetail.setCreatedDate((Date)row[16]);
            paymentSearchDetail.setCustomerCode("");
            paymentSearchDetail.setCustomerName("");

            result.add(paymentSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords + countTotalQueryStr + joinStr+ " ) as v").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<PaymentSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        if (merchandiseIds != null) {
            return new ArrayList<>();
        }
        Session session = getSession();
        String paymentQueryStr = "select p.*, format(substring(p.number,4,4),0) as paymentNumber from payment p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from payment_detail pd INNER JOIN ";
        String transactionQueryStr = "select p.* from payment_detail t ";

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
        String paymentWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> detailConditions = new ArrayList<>();
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            detailConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }
        String detailWhereStr = detailConditions.size() > 0 ? " where " + String.join(" and ", detailConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ paymentQueryStr +" "+ paymentWhereStr + ") as p ON p.id = t.payment_id" + detailWhereStr + " group by t.payment_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = pd.payment_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<PaymentSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            PaymentSearchDetail paymentSearchDetail = new PaymentSearchDetail();
            paymentSearchDetail.setTotal(((Double)row[0]));
            paymentSearchDetail.setId((String)row[1]);
            paymentSearchDetail.setCode((String)row[3]);
            paymentSearchDetail.setNumber((String)row[4]);
            paymentSearchDetail.setInvoiceDate((Date)row[5]);
            paymentSearchDetail.setInvoiceCode((String)row[6]);
            paymentSearchDetail.setInvoiceTemplate((String)row[7]);
            paymentSearchDetail.setInvoiceSymbol((String)row[8]);
            paymentSearchDetail.setInvoiceNumber((String)row[9]);
            paymentSearchDetail.setCustomerAddress((String)row[10]);
            paymentSearchDetail.setCustomerTaxCode((String)row[11]);
            paymentSearchDetail.setCustomerId((String)row[12]);
            paymentSearchDetail.setTransactionCustomerId((String)row[13]);
            paymentSearchDetail.setDescription((String)row[14]);
            paymentSearchDetail.setNote((String)row[15]);
            paymentSearchDetail.setCreatedDate((Date)row[16]);
            paymentSearchDetail.setCustomerCode("");
            paymentSearchDetail.setCustomerName("");

            result.add(paymentSearchDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<MonthCostDetail> getMonthCost(String fromDate, String toDate, String accountingId, String agencyId){
        Session session = getSession();

        String paymentMonthCostQueryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment a left join payment_detail b on a.id = b.payment_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by year(a.created_date), month(a.created_date)";

        String paymentAdviceMonthCostQueryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment_advice a left join payment_advice_detail b on a.id = b.payment_advice_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' "  + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by year(a.created_date), month(a.created_date)";

        Query<Object[]> paymentMonthCostQuery = session.createNativeQuery(paymentMonthCostQueryStr);
        Query<Object[]> paymentAdviceMonthCostQuery = session.createNativeQuery(paymentAdviceMonthCostQueryStr);
        List<MonthCostDetail> result = new ArrayList<>();
        List<Object[]> paymentMonthCostResultRows = paymentMonthCostQuery.getResultList();
        List<Object[]> paymentAdviceMonthCostResultRows = paymentAdviceMonthCostQuery.getResultList();
        for(Object[] row: paymentMonthCostResultRows){
            MonthCostDetail monthCostDetail = new MonthCostDetail();
            monthCostDetail.setMonthDate(((Integer)row[0]));
            monthCostDetail.setYearDate(((Integer)row[1]));
            monthCostDetail.setTotal(((Double)row[2]).floatValue());
            result.add(monthCostDetail);
        }
        for(Object[] row: paymentAdviceMonthCostResultRows){
            MonthCostDetail monthCostDetail = new MonthCostDetail();
            monthCostDetail.setMonthDate(((Integer)row[0]));
            monthCostDetail.setYearDate(((Integer)row[1]));
            monthCostDetail.setTotal(((Double)row[2]).floatValue());
            MonthCostDetail monthCostDetailTemp = result.stream().filter(item -> item.getMonthDate() == monthCostDetail.getMonthDate()
                    && item.getYearDate() == monthCostDetail.getYearDate()).findFirst().orElse(null);
            if (monthCostDetailTemp == null){
                result.add(monthCostDetail);
                continue;
            }
            Double total = monthCostDetailTemp.getTotal() + monthCostDetail.getTotal();
            result.stream().filter(item -> item.getMonthDate() == monthCostDetail.getMonthDate()
                    && item.getYearDate() == monthCostDetail.getYearDate()).findFirst().orElseThrow().setTotal(total);
        }

        return result;
    }

    @Override
    @Transactional
    public List<DateCostDetail> getDateCost(String fromDate, String toDate, String accountingId, String agencyId){
        Session session = getSession();

        String paymentDateCostQueryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment a left join payment_detail b on a.id = b.payment_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";

        String paymentAdviceDateCostQueryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment_advice a left join payment_advice_detail b on a.id = b.payment_advice_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";

        Query<Object[]> paymentDateCostQuery = session.createNativeQuery(paymentDateCostQueryStr);
        Query<Object[]> paymentAdviceDateCostQuery = session.createNativeQuery(paymentAdviceDateCostQueryStr);
        List<DateCostDetail> result = new ArrayList<>();
        List<Object[]> paymentDateCostResultRows = paymentDateCostQuery.getResultList();
        List<Object[]> paymentAdviceDateCostResultRows = paymentAdviceDateCostQuery.getResultList();
        for(Object[] row: paymentDateCostResultRows){
            DateCostDetail dateCostDetail = new DateCostDetail();
            dateCostDetail.setDate(((Integer)row[0]));
            dateCostDetail.setMonth(((Integer)row[1]));
            dateCostDetail.setYear(((Integer)row[2]));
            dateCostDetail.setTotal(((Double)row[3]).floatValue());
            result.add(dateCostDetail);
        }
        for(Object[] row: paymentAdviceDateCostResultRows){
            DateCostDetail dateCostDetail = new DateCostDetail();
            dateCostDetail.setDate(((Integer)row[0]));
            dateCostDetail.setMonth(((Integer)row[1]));
            dateCostDetail.setYear(((Integer)row[2]));
            dateCostDetail.setTotal(((Double)row[3]).floatValue());
            DateCostDetail dateCostDetailTemp = result.stream().filter(item -> item.getDate() == dateCostDetail.getDate() && item.getMonth() == dateCostDetail.getMonth()
                    && item.getYear() == dateCostDetail.getYear()).findFirst().orElse(null);
            if (dateCostDetailTemp == null){
                result.add(dateCostDetail);
                continue;
            }
            Double total = dateCostDetailTemp.getTotal() + dateCostDetail.getTotal();
            result.stream().filter(item -> item.getDate() == dateCostDetail.getDate() && item.getMonth() == dateCostDetail.getMonth()
                    && item.getYear() == dateCostDetail.getYear()).findFirst().orElseThrow().setTotal(total);
        }

        return result;
    }

    @Override
    @Transactional
    public List<YearCostDetail> getYearCost(String fromDate, String toDate, String accountingId, String agencyId){
        Session session = getSession();

        String paymentYearCostQueryStr = "select year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment a left join payment_detail b on a.id = b.payment_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by year(a.created_date)";

        String paymentAdviceYearCostQueryStr = "select year(a.created_date) as yearDate, sum(b.amount) as total " +
                "from payment_advice a left join payment_advice_detail b on a.id = b.payment_advice_id " +
                "where a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " + "and a.agency_id = '" + agencyId + "' " +
                "and b.debit_account = '" + accountingId + "' " +
                "group by year(a.created_date)";

        Query<Object[]> paymentYearCostQuery = session.createNativeQuery(paymentYearCostQueryStr);
        Query<Object[]> paymentAdviceYearCostQuery = session.createNativeQuery(paymentAdviceYearCostQueryStr);
        List<YearCostDetail> result = new ArrayList<>();
        List<Object[]> paymentYearCostResultRows = paymentYearCostQuery.getResultList();
        List<Object[]> paymentAdviceYearCostResultRows = paymentAdviceYearCostQuery.getResultList();
        for(Object[] row: paymentYearCostResultRows){
            YearCostDetail yearCostDetail = new YearCostDetail();
            yearCostDetail.setYear(((Integer)row[0]));
            yearCostDetail.setTotal(((Double)row[1]).floatValue());
            result.add(yearCostDetail);
        }
        for(Object[] row: paymentAdviceYearCostResultRows){
            YearCostDetail yearCostDetail = new YearCostDetail();
            yearCostDetail.setYear(((Integer)row[0]));
            yearCostDetail.setTotal(((Double)row[1]).floatValue());
            YearCostDetail yearCostDetailTemp = result.stream().filter(item -> item.getYear() == yearCostDetail.getYear()).findFirst().orElse(null);
            if (yearCostDetailTemp == null){
                result.add(yearCostDetail);
                continue;
            }
            Double total = yearCostDetailTemp.getTotal() + yearCostDetail.getTotal();
            result.stream().filter(item -> item.getYear() == yearCostDetail.getYear()).findFirst().orElseThrow().setTotal(total);
        }

        return result;
    }
}
