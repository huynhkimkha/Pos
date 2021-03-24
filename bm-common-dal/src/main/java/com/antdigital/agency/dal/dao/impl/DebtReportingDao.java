package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IDebtReportingDao;
import com.antdigital.agency.dal.data.DebtReport;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DebtReportingDao extends GenericDao implements IDebtReportingDao {
    @Override
    @Transactional
    public List<DebtReport> getDebtReports(Date fromDate, Date toDate, String customerId) {
        Session session = getSession();
        return null;
    }

    private String createPaymentDetailQuery(Date fromDate, Date toDate, String customerId) {
        List<String> conditions = new ArrayList<>();
        if (customerId != null && !customerId.isEmpty()) {
            conditions.add(String.format("p.transaction_customer_id = '%s'", customerId));
        }
        if (fromDate != null) {
            conditions.add(String.format("p.transaction_customer_id = '%s'", customerId));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(fromDate != null){
            conditions.add(String.format("p.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if(toDate != null){
            conditions.add(String.format("p.created_date <= '%s'", dateFormat.format(toDate)));
        }

        String debtClearingWhereStr = conditions.size() > 0 ? "WHERE " + String.join(" and ", conditions) : "";
        String querySrt = "SELECT pd.id as payment_detail_id, " +
                "p.transaction_customer_id, " +
                "p.code, " +
                "p.created_date, " +
                "p.number, " +
                "p.invoice_date, " +
                "p.invoice_code, " +
                "p.invoice_number, " +
                "pd.description, " +
                "pd.credit_account, " +
                "pd.amount " +
                "FROM payment_detail pd " +
                "INNER JOIN payment p ON p.id = pd.payment_id ";
        return querySrt + debtClearingWhereStr;
    }

    private String createPaymentAdviceDetailQuery(Date fromDate, Date toDate, String customerId) {
        List<String> conditions = new ArrayList<>();
        if (customerId != null && !customerId.isEmpty()) {
            conditions.add(String.format("p.transaction_customer_id = '%s'", customerId));
        }
        if (fromDate != null) {
            conditions.add(String.format("p.transaction_customer_id = '%s'", customerId));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(fromDate != null){
            conditions.add(String.format("p.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if(toDate != null){
            conditions.add(String.format("p.created_date <= '%s'", dateFormat.format(toDate)));
        }

        String debtClearingWhereStr = conditions.size() > 0 ? "WHERE " + String.join(" and ", conditions) : "";
        String querySrt = "SELECT pd.id as payment_advice_detail_id, " +
                "p.transaction_customer_id, " +
                "p.code, " +
                "p.created_date, " +
                "p.number, " +
                "p.invoice_date, " +
                "p.invoice_code, " +
                "p.invoice_number, " +
                "pd.description, " +
                "pd.credit_account, " +
                "pd.amount " +
                "FROM payment_detail pd " +
                "INNER JOIN payment p ON p.id = pd.payment_id ";
        return querySrt + debtClearingWhereStr;
    }
}
