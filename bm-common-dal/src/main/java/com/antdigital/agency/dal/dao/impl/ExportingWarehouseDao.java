package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dal.dao.IExportingWarehouseDao;
import com.antdigital.agency.dal.data.*;
import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dal.entity.Order;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExportingWarehouseDao extends GenericDao implements IExportingWarehouseDao {

    @Transactional
    public SearchResult<List<ExportingWarehouseDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds,PaymentStatusEnum paymentStatus, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<ExportingWarehouseDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String exportQueryStr = "select e.*, format(substring(e.number,4,4),0) as exportNumber from exporting_warehouse e ";
        String countTotalQueryStr = "select sum(etr.quantity) as totalQuantity, sum(etr.amount) as total, c.* from exporting_transaction etr INNER JOIN ";
        String transactionQueryStr = "select e.* from exporting_transaction t ";
        String countTotalRecords = "select count(*) from ";

        // export_warehouse where conditions
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()){
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }

        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("e.code LIKE '%s%s%s'", "%", code, "%"));
        }

        if(number != null && !number.isEmpty()) {
            conditions.add(String.format("e.number LIKE '%s%s%s'", "%", number, "%"));
        }

        if(customerAddress != null && !customerAddress.isEmpty()) {
            conditions.add(String.format("e.customer_address LIKE '%s%s%s'", "%", customerAddress, "%"));
        }

        if(description != null && !description.isEmpty()) {
            conditions.add(String.format("e.description LIKE '%s%s%s'", "%", description, "%"));
        }

        if(note != null && !note.isEmpty()) {
            conditions.add(String.format("e.note LIKE '%s%s%s'", "%", note, "%"));
        }

        if(paymentStatus != null) {
            conditions.add(String.format("e.payment_status = '%s'", paymentStatus));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null){
            conditions.add(String.format("e.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if(endDate != null){
            conditions.add(String.format("e.created_date <= '%s'", dateFormat.format(endDate)));
        }

        if(customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("e.customer_id in (%s)", customerStr));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        // export_warehouse having conditions
        List<String> havingConditions = new ArrayList<>();
        if(startNumber!=null){
            havingConditions.add(String.format(" exportNumber >= %s ", startNumber));
        }
        if(endNumber!=null){
            havingConditions.add(String.format(" exportNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

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

        // export_transaction where conditions
        List<String> transactionConditions = new ArrayList<>();
        if(merchandiseIds.size() > 0) {
            List<String> merchandiseIdStr = new ArrayList<>();
            for(String id: merchandiseIds){
                merchandiseIdStr.add(String.format("'%s'",id));
            }
            String merchandiseStr = String.join(",", merchandiseIdStr);
            transactionConditions.add(String.format("t.merchandise_id in (%s)", merchandiseStr));
        }
        String transactionWhereStr = transactionConditions.size() > 0 ? " where " + String.join(" and ", transactionConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ exportQueryStr+" "+exportWhereStr+havingStr + ") as e ON e.id = t.export_id" +transactionWhereStr + " group by t.export_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = etr.export_id group by c.id" +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<ExportingWarehouseDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ExportingWarehouseDetail exportingWarehouseDetail = new ExportingWarehouseDetail();
            exportingWarehouseDetail.setTotalQuantity(((Double)row[0]).floatValue());
            exportingWarehouseDetail.setTotal(((Double)row[1]));
            exportingWarehouseDetail.setId((String)row[2]);
            Order order = new Order();
            order.setId((String)row[4]);
            exportingWarehouseDetail.setOrder(order);
            exportingWarehouseDetail.setCode((String)row[5]);
            exportingWarehouseDetail.setNumber((String)row[6]);
            exportingWarehouseDetail.setInvoiceDate((Date)row[7]);
            exportingWarehouseDetail.setInvoiceCode((String)row[8]);
            exportingWarehouseDetail.setInvoiceTemplate((String)row[9]);
            exportingWarehouseDetail.setInvoiceSymbol((String)row[10]);
            exportingWarehouseDetail.setInvoiceNumber((String)row[11]);
            exportingWarehouseDetail.setCustomerId((String)row[12]);
            exportingWarehouseDetail.setTransactionCustomerId((String)row[13]);
            exportingWarehouseDetail.setCustomerAddress((String)row[14]);
            exportingWarehouseDetail.setCustomerTaxCode((String)row[15]);
            exportingWarehouseDetail.setDescription((String)row[16]);
            exportingWarehouseDetail.setNote((String)row[17]);
            exportingWarehouseDetail.setForeignCurrency((String)row[18]);
            exportingWarehouseDetail.setForeignCurrencyRate((String)row[19]);
            exportingWarehouseDetail.setPaymentStatus( PaymentStatusEnum.valueOf((String)row[20]));
            exportingWarehouseDetail.setCreatedDate((Date)row[21]);
            exportingWarehouseDetail.setCustomerCode("");
            exportingWarehouseDetail.setCustomerName("");

            result.add(exportingWarehouseDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords +"("+secondJoinStr+" ) as r").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<ExportingWarehouseDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        Session session = getSession();
        String exportQueryStr = "select e.*, format(substring(e.number,4,4),0) as exportNumber from exporting_warehouse e ";
        String countTotalQueryStr = "select sum(etr.quantity) as totalQuantity, sum(etr.amount) as total, sum(etr.quantity*etr.cost_of_goods_sold) as purchaseTotal, c.* from exporting_transaction etr INNER JOIN ";
        String transactionQueryStr = "select e.* from exporting_transaction t ";
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()){
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }

        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("e.code LIKE '%s%s%s'", "%", code, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(fromDate != null){
            conditions.add(String.format("e.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if(toDate != null){
            conditions.add(String.format("e.created_date <= '%s'", dateFormat.format(toDate)));
        }

        if(customerIds != null) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("e.customer_id in (%s)", customerStr));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        // export_transaction where conditions
        List<String> transactionConditions = new ArrayList<>();
        if(merchandiseIds != null) {
            List<String> merchandiseIdStr = new ArrayList<>();
            for(String id: merchandiseIds){
                merchandiseIdStr.add(String.format("'%s'",id));
            }
            String merchandiseStr = String.join(",", merchandiseIdStr);
            transactionConditions.add(String.format("t.merchandise_id in (%s)", merchandiseStr));
        }
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            transactionConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }
        String transactionWhereStr = transactionConditions.size() > 0 ? " where " + String.join(" and ", transactionConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ exportQueryStr + " " + exportWhereStr + ") as e ON e.id = t.export_id" +transactionWhereStr + " group by t.export_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = etr.export_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ExportingWarehouseDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ExportingWarehouseDetail exportingWarehouseDetail = new ExportingWarehouseDetail();
            exportingWarehouseDetail.setTotalQuantity(((Double)row[0]).floatValue());
            exportingWarehouseDetail.setTotal(((Double)row[1]));
            exportingWarehouseDetail.setPurchaseTotal(((Double)row[2]));
            exportingWarehouseDetail.setId((String)row[3]);
            Order order = new Order();
            order.setId((String)row[5]);
            exportingWarehouseDetail.setOrder(order);
            exportingWarehouseDetail.setCode((String)row[6]);
            exportingWarehouseDetail.setNumber((String)row[7]);
            exportingWarehouseDetail.setInvoiceDate((Date)row[8]);
            exportingWarehouseDetail.setInvoiceCode((String)row[9]);
            exportingWarehouseDetail.setInvoiceTemplate((String)row[10]);
            exportingWarehouseDetail.setInvoiceSymbol((String)row[11]);
            exportingWarehouseDetail.setInvoiceNumber((String)row[12]);
            exportingWarehouseDetail.setCustomerId((String)row[13]);
            exportingWarehouseDetail.setTransactionCustomerId((String)row[14]);
            exportingWarehouseDetail.setCustomerAddress((String)row[15]);
            exportingWarehouseDetail.setCustomerTaxCode((String)row[16]);
            exportingWarehouseDetail.setDescription((String)row[17]);
            exportingWarehouseDetail.setNote((String)row[18]);
            exportingWarehouseDetail.setForeignCurrency((String)row[19]);
            exportingWarehouseDetail.setForeignCurrencyRate((String)row[20]);
            exportingWarehouseDetail.setPaymentStatus( PaymentStatusEnum.valueOf((String)row[21]));
            exportingWarehouseDetail.setCreatedDate((Date)row[22]);
            exportingWarehouseDetail.setCustomerCode("");
            exportingWarehouseDetail.setCustomerName("");

            result.add(exportingWarehouseDetail);
        }
        return result;
    }
    @Override
    @Transactional
    public List<CustomerStatistic> getCustomerBaseOnSpent(String fromDate, String toDate, String agencyId) {
        Session session = getSession();
        String exportQueryStr = "select e.* from exporting_warehouse e ";
        String countTotalQueryStr = "select e.customer_id, sum(etr.quantity*etr.price) as totalSpent from exporting_transaction etr ";
        List<String> conditions = new ArrayList<>();
        if(agencyId != null){
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }
        if(fromDate != null){
            conditions.add(String.format("e.created_date >= '%s'", fromDate));
        }
        if(toDate != null){
            conditions.add(String.format("e.created_date <= '%s'", toDate));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        String joinStr = " INNER JOIN" + " (" + exportQueryStr + " " + exportWhereStr + ")";
        String secondJoinStr = countTotalQueryStr + joinStr + "e" + " ON e.id = etr.export_id group by e.customer_id order by totalSpent desc limit 0,10";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<CustomerStatistic> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            CustomerStatistic customer = new CustomerStatistic();
            customer.setCustomerId((String) row[0]);
            customer.setTotalSpent(((Double) row[1]));
            result.add(customer);
        }
    return result;
    }

    @Override
    @Transactional
    public List<MerchandiseStatistic> getMerchandiseBestSold (String fromDate, String toDate, String agencyId) {
        Session session = getSession();
        String exportQueryStr = "select e.* from exporting_warehouse e ";
        List<String> conditions = new ArrayList<>();
        if(agencyId != null){
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }
        if(fromDate != null){
            conditions.add(String.format("e.created_date >= '%s'", fromDate));
        }
        if(toDate != null){
            conditions.add(String.format("e.created_date <= '%s'", toDate));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        String joinStr = " INNER JOIN" + " (" + exportQueryStr + " " + exportWhereStr + ")";
        String secondJoinStr = "select etr.merchandise_id, sum(etr.quantity * etr.price) as totalRevenue from exporting_transaction etr" + joinStr + " e" + " ON e.id = etr.export_id group by etr.merchandise_id order by totalRevenue desc limit 0,10";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<MerchandiseStatistic> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            MerchandiseStatistic merchandise = new MerchandiseStatistic();
            merchandise.setMerchandiseId((String) row[0]);
            merchandise.setTotalRevenue(((Double) row[1]));
            result.add(merchandise);
        }
        return result;
    }

    @Override
    @Transactional
    public List<MonthRevenueDetail> getMonthRevenue(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, b.amount " +
                "from exporting_warehouse a left join exporting_transaction b on a.id = b.export_id " +
                "where a.agency_id = '" + agencyId + "' " +
                "and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<MonthRevenueDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            MonthRevenueDetail monthRevenueDetail = new MonthRevenueDetail();
            monthRevenueDetail.setMonthDate(((Integer)row[0]));
            monthRevenueDetail.setYearDate(((Integer)row[1]));
            monthRevenueDetail.setTotal(((Double)row[2]).floatValue());
            result.add(monthRevenueDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<DateRevenueDetail> getDateRevenue(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, b.amount " +
                "from exporting_warehouse a left join exporting_transaction b on a.id = b.export_id " +
                "where a.agency_id = '" + agencyId + "' " +
                "and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<DateRevenueDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            DateRevenueDetail dateRevenueDetail = new DateRevenueDetail();
            dateRevenueDetail.setDate(((Integer)row[0]));
            dateRevenueDetail.setMonth(((Integer)row[1]));
            dateRevenueDetail.setYear(((Integer)row[2]));
            dateRevenueDetail.setTotal(((Double)row[3]).floatValue());
            result.add(dateRevenueDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<YearRevenueDetail> getYearRevenue(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select year(a.created_date) as yearDate, b.amount " +
                "from exporting_warehouse a left join exporting_transaction b on a.id = b.export_id " +
                "where a.agency_id = '" + agencyId + "' " +
                "and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by year(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<YearRevenueDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            YearRevenueDetail yearRevenueDetail = new YearRevenueDetail();
            yearRevenueDetail.setYear(((Integer)row[0]));
            yearRevenueDetail.setTotal(((Double)row[1]).floatValue());
            result.add(yearRevenueDetail);
        }
        return result;
    }
}
