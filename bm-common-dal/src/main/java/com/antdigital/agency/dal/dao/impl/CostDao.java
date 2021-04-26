package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.ICostDao;
import com.antdigital.agency.dal.data.DateCostDetail;
import com.antdigital.agency.dal.data.MonthCostDetail;
import com.antdigital.agency.dal.data.YearCostDetail;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class CostDao extends GenericDao implements ICostDao {
    @Override
    @Transactional
    public List<MonthCostDetail> getMonthCost(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(a.amount) as total " +
                "from cost a " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<MonthCostDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            MonthCostDetail monthCostDetail = new MonthCostDetail();
            monthCostDetail.setMonthDate(((Integer)row[0]));
            monthCostDetail.setYearDate(((Integer)row[1]));
            monthCostDetail.setTotal(((Double)row[2]).floatValue());
            result.add(monthCostDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<DateCostDetail> getDateCost(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select day(a.created_date) as d, month(a.created_date) as monthDate, year(a.created_date) as yearDate, sum(a.amount) as total " +
                "from cost a " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by day(a.created_date), year(a.created_date), month(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<DateCostDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            DateCostDetail dateCostDetail = new DateCostDetail();
            dateCostDetail.setDate(((Integer)row[0]));
            dateCostDetail.setMonth(((Integer)row[1]));
            dateCostDetail.setYear(((Integer)row[2]));
            dateCostDetail.setTotal(((Double)row[3]).floatValue());
            result.add(dateCostDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public List<YearCostDetail> getYearCost(String fromDate, String toDate, String agencyId){
        Session session = getSession();

        String queryStr = "select year(a.created_date) as yearDate, sum(a.amount) as total " +
                "from cost a " +
                "where a.agency_id = :agencyId" + " and a.created_date <= :toDate " + "and a.created_date >= :fromDate " +
                "group by year(a.created_date)";
        Query<Object[]> query = session.createNativeQuery(queryStr);
        query.setParameter("agencyId", agencyId);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        List<YearCostDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            YearCostDetail yearCostDetail = new YearCostDetail();
            yearCostDetail.setYear(((Integer)row[0]));
            yearCostDetail.setTotal(((Double)row[1]).floatValue());
            result.add(yearCostDetail);
        }
        return result;
    }
}
