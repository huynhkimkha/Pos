package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.dal.dao.IOrderDao;

import com.antdigital.agency.dal.data.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class OrderDao extends GenericDao implements IOrderDao {

    @Transactional
    public SearchResult<List<OrderDetail>> search(String code, String number, String title,
              List<String> customerIds, List<String> merchandiseIds,
              ImportStatusEnum importStatus, DeliveryStatusEnum deliverStatus,Date startDate, Date endDate, Integer startNumber, Integer endNumber,String createdDateSort, int start, int size, String agencyId) {
        SearchResult<List<OrderDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String orderQueryStr = "select o.*, format(substring(o.number,4,4),0) as orderNumber from orders o ";
        String countTotalQueryStr = "select sum(otr.quantity) as totalQuantity, sum(otr.amount) as total, c.* from order_transaction otr INNER JOIN ";
        String transactionQueryStr = "select o.* from order_transaction t ";
        String countTotalRecords = "select count(*) from ";

        // order_warehouse where conditions
        List<String> conditions = new ArrayList<>();
        conditions.add(String.format("o.agency_id = '%s'", agencyId));
        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("o.code LIKE '%s%s%s'", "%", code, "%"));
        }

        if(number != null && !number.isEmpty()) {
            conditions.add(String.format("o.number LIKE '%s%s%s'", "%", number, "%"));
        }

        if(title != null && !title.isEmpty()) {
            conditions.add(String.format("o.title LIKE '%s%s%s'", "%", title, "%"));
        }

        if(importStatus != null) {
            conditions.add(String.format("o.import_status = '%s'", importStatus));
        }

        if(deliverStatus != null) {
            conditions.add(String.format("o.deliver_status = '%s'", deliverStatus));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null){
            conditions.add(String.format("o.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if(endDate != null){
            conditions.add(String.format("o.created_date <= '%s'", dateFormat.format(endDate)));
        }

        if(customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("o.customer_id in (%s)", customerStr));
        }
        String orderWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

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

        // order_warehouse having conditions
        List<String> havingConditions = new ArrayList<>();
        if(startNumber!=null){
            havingConditions.add(String.format("orderNumber >= %s ", startNumber));
        }
        if(endNumber!=null){
            havingConditions.add(String.format("orderNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

        // order_transaction where conditions
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
        String joinStr = " INNER JOIN"+" ("+ orderQueryStr+" "+orderWhereStr+havingStr + ") as o ON o.id = t.order_id" +transactionWhereStr + " group by t.order_id";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = otr.order_id group by c.id " +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<OrderDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setTotalQuantity(((Double)row[0]).floatValue());
            orderDetail.setTotal(((Double)row[1]));
            orderDetail.setId((String)row[2]);
            orderDetail.setTitle((String)row[4]);
            orderDetail.setCustomerId((String)row[5]);
            orderDetail.setCode((String)row[6]);
            orderDetail.setNumber((String)row[7]);
            orderDetail.setImportStatus( ImportStatusEnum.valueOf((String)row[8]));
            orderDetail.setDeliverStatus( DeliveryStatusEnum.valueOf((String)row[9]));
            orderDetail.setCreatedDate((Date)row[10]);

            result.add(orderDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords +"("+secondJoinStr+" ) as r").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<MonthOrderDetail> getMonthOrder(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.price*b.quantity) as total " +
                "from orders a left join order_transaction b on a.id = b.order_id " +
                "where a.agency_id = " + "'" + agencyId + "'" + " and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<MonthOrderDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            MonthOrderDetail monthOrderDetail = new MonthOrderDetail();
            monthOrderDetail.setMonthDate(((Integer)row[0]));
            monthOrderDetail.setYearDate(((Integer)row[1]));
            monthOrderDetail.setTotal(((Double)row[2]).floatValue());
            result.add(monthOrderDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<DateOrderDetail> getDateOrder(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.price*b.quantity) as total " +
                "from orders a left join order_transaction b on a.id = b.order_id " +
                "where a.agency_id = " + "'" + agencyId + "'" + " and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<DateOrderDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            DateOrderDetail dateOrderDetail = new DateOrderDetail();
            dateOrderDetail.setDate(((Integer)row[0]));
            dateOrderDetail.setMonth(((Integer)row[1]));
            dateOrderDetail.setYear(((Integer)row[2]));
            dateOrderDetail.setTotal(((Double)row[3]).floatValue());
            result.add(dateOrderDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<YearOrderDetail> getYearOrder(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select year(a.created_date) as yearDate, sum(b.price*b.quantity) as total " +
                "from orders a left join order_transaction b on a.id = b.order_id " +
                "where a.agency_id = " + "'" + agencyId + "'" + " and a.created_date <= '" + toDate + "' "  +  "and a.created_date >= '" + fromDate + "' " +
                "group by year(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        List<YearOrderDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            YearOrderDetail yearOrderDetail = new YearOrderDetail();
            yearOrderDetail.setYear(((Integer)row[0]));
            yearOrderDetail.setTotal(((Double)row[1]).floatValue());
            result.add(yearOrderDetail);
        }
        return result;
    }
}
