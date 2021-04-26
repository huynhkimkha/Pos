package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IBillDao;
import com.antdigital.agency.dal.data.DateBillDetail;
import com.antdigital.agency.dal.data.MonthBillDetail;
import com.antdigital.agency.dal.data.YearBillDetail;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class BillDao extends GenericDao implements IBillDao {

    @Override
    @Transactional
    public List<MonthBillDetail> getMonthBill(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.price * b.quantity) as total " +
                "from bills a left join bill_product_size b on a.id = b.bill_id " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<MonthBillDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            MonthBillDetail monthBillDetail = new MonthBillDetail();
            monthBillDetail.setMonthDate(((Integer)row[0]));
            monthBillDetail.setYearDate(((Integer)row[1]));
            monthBillDetail.setTotal(((Double)row[2]).floatValue());
            result.add(monthBillDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<DateBillDetail> getDateBill(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(b.price * b.quantity) as total " +
                "from bills a left join bill_product_size b on a.id = b.bill_id " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<DateBillDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            DateBillDetail dateBillDetail = new DateBillDetail();
            dateBillDetail.setDate(((Integer)row[0]));
            dateBillDetail.setMonth(((Integer)row[1]));
            dateBillDetail.setYear(((Integer)row[2]));
            dateBillDetail.setTotal(((Double)row[3]).floatValue());
            result.add(dateBillDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<YearBillDetail> getYearBill(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select year(a.created_date) as yearDate, sum(b.price * b.quantity) as total " +
                "from bills a left join bill_product_size b on a.id = b.bill_id " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by year(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<YearBillDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            YearBillDetail yearBillDetail = new YearBillDetail();
            yearBillDetail.setYear(((Integer)row[0]));
            yearBillDetail.setTotal(((Double)row[1]).floatValue());
            result.add(yearBillDetail);
        }
        return result;
    }
}
