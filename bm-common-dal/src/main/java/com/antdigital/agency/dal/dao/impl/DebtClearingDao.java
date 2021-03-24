package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IDebtClearingDao;
import com.antdigital.agency.dal.data.DebtClearingSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.ExportingWarehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DebtClearingDao extends GenericDao implements IDebtClearingDao {

    @Override
    @Transactional
    public List<DebtClearingSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        if (merchandiseIds != null) {
            return new ArrayList<>();
        }
        Session session = getSession();
        String debtClearingQueryStr = "select p.*, format(substring(p.number,4,4),0) as debtClearingNumber from debt_clearing p ";
        String countTotalQueryStr = "select sum(pd.amount) as total, c.* from debt_clearing_detail pd INNER JOIN ";
        String transactionQueryStr = "select p.* from debt_clearing_detail t ";

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

        String debtClearingWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        List<String> detailConditions = new ArrayList<>();
        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            detailConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }

        if(customerIds != null) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            detailConditions.add(String.format("(t.customer_id in (%s) or t.customer_debt_id in (%s))", customerStr, customerStr));
        }
        String detailWhereStr = detailConditions.size() > 0 ? " where " + String.join(" and ", detailConditions) : "";

        //query
        String joinStr = " INNER JOIN"+" ("+ debtClearingQueryStr +" "+ debtClearingWhereStr + ") as p ON p.id = t.debt_clearing_id" + detailWhereStr + " group by t.debt_clearing_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = pd.debt_clearing_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<DebtClearingSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            DebtClearingSearchDetail debtClearingSearchDetail = new DebtClearingSearchDetail();
            debtClearingSearchDetail.setTotal(((Double)row[0]));
            debtClearingSearchDetail.setId((String)row[1]);
            debtClearingSearchDetail.setCode((String)row[3]);
            debtClearingSearchDetail.setNumber((String)row[4]);
            debtClearingSearchDetail.setInvoiceDate((Date)row[5]);
            debtClearingSearchDetail.setInvoiceCode((String)row[6]);
            debtClearingSearchDetail.setInvoiceTemplate((String)row[7]);
            debtClearingSearchDetail.setInvoiceSymbol((String)row[8]);
            debtClearingSearchDetail.setInvoiceNumber((String)row[9]);
            debtClearingSearchDetail.setDescription((String)row[10]);
            debtClearingSearchDetail.setNote((String)row[11]);
            debtClearingSearchDetail.setCreatedDate((Date)row[12]);

            result.add(debtClearingSearchDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public SearchResult<List<DebtClearingSearchDetail>> search(String code, String number, String description, String note, List<String> exportingWarehouseIds, List<String> customerIds, List<String> customerDebtIds, Date startDate, Date endDate, String createdDateSort, String startNumber, String endNumber, int start, int size, String agencyId) {
        SearchResult<List<DebtClearingSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String exportQueryStr = "select e.*, format(substring(e.number,4,4),0) as debtNumber from debt_clearing e ";
        String countTotalQueryStr = "select sum(dcd.amount) as total, c.* from debt_clearing_detail dcd INNER JOIN ";
        String transactionQueryStr = "select e.* from debt_clearing_detail t ";
        String countTotalRecords = "select count(*) from ";

        // debt_clearing where condition
        List<String> conditions = new ArrayList<>();
        if (agencyId != null && !agencyId.isEmpty()) {
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }
        if (code != null && !code.isEmpty()) {
            conditions.add(String.format("e.code LIKE '%s%s%s'", "%", code, "%"));
        }
        if (number != null && !number.isEmpty()) {
            conditions.add(String.format("e.number LIKE '%s%s%s'", "%", number, "%"));
        }
        if (description != null && !description.isEmpty()) {
            conditions.add(String.format("e.description LIKE '%s%s%s'", "%", description, "%"));
        }
        if (note != null && !note.isEmpty()) {
            conditions.add(String.format("e.note LIKE '%s%s%s'", "%", note, "%"));
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            conditions.add(String.format("e.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if (endDate != null) {
            conditions.add(String.format("e.created_date <= '%s'", dateFormat.format(endDate)));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        // debt_clearing having conditions
        List<String> havingConditions = new ArrayList<>();
        if (startNumber != null) {
            havingConditions.add(String.format(" debtNumber >= %s ", startNumber));
        }
        if (endNumber != null) {
            havingConditions.add(String.format(" debtNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

        List<String> sortConditions = new ArrayList<>();
        if (createdDateSort != null) {
            if (createdDateSort.equals("desc")) {
                sortConditions.add(String.format("c.created_date desc, c.number desc"));
            }
            if (createdDateSort.equals("asc")) {
                sortConditions.add(String.format("c.created_date asc, c.number asc"));
            }
        } else {
            sortConditions.add(String.format("c.created_date desc, c.number desc"));
        }
        String sortStr = sortConditions.size() > 0 ? " order by " + String.join(" and ", sortConditions) : "";

        // debt_clearing_detail where conditions
        List<String> transactionConditions = new ArrayList<>();
        if (exportingWarehouseIds.size() > 0) {
            List<String> exportWarehouseIdStr = new ArrayList<>();
            for (String id : exportingWarehouseIds) {
                exportWarehouseIdStr.add(String.format("'%s'", id));
            }
            String exportWarehouseStr = String.join(",", exportWarehouseIdStr);
            transactionConditions.add(String.format("t.exporting_warehouse_id in (%s)", exportWarehouseStr));
        }
        if (customerIds.size() > 0) {
            List<String> customreIdStr = new ArrayList<>();
            for (String id : customerIds) {
                customreIdStr.add(String.format("'%s'", id));
            }
            String customerStr = String.join(",", customreIdStr);
            transactionConditions.add(String.format("t.customer_id in(%s)", customerStr));
        }
        if (customerDebtIds.size() > 0) {
            List<String> customerDebtIdStr = new ArrayList<>();
            for (String id : customerDebtIds) {
                customerDebtIdStr.add(String.format("'%s'", id));
            }
            String customerDebtStr = String.join(",", customerDebtIdStr);
            transactionConditions.add(String.format("t.customer_debt_id in(%s)", customerDebtStr));
        }
        String transactionWhereStr = transactionConditions.size() > 0 ? " where " + String.join(" and ", transactionConditions) : "";

        // query
        String joinStr = " INNER JOIN (" + exportQueryStr + " " + exportWhereStr + havingStr + ") as e ON e.id = t.debt_clearing_id" + transactionWhereStr + " group by t.debt_clearing_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = dcd.debt_clearing_id group by c.id" + " " + sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if (start >= 0 && size > 0) {
            query.setFirstResult(start * size);
            query.setMaxResults(size);
        }

        List<DebtClearingSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            DebtClearingSearchDetail debtClearingSearchDetail = new DebtClearingSearchDetail();
            debtClearingSearchDetail.setTotal((Double) row[0]);
            debtClearingSearchDetail.setId((String) row[1]);
            debtClearingSearchDetail.setCode((String) row[3]);
            debtClearingSearchDetail.setNumber((String) row[4]);
            debtClearingSearchDetail.setInvoiceDate((Date) row[5]);
            debtClearingSearchDetail.setInvoiceCode((String) row[6]);
            debtClearingSearchDetail.setInvoiceTemplate((String) row[7]);
            debtClearingSearchDetail.setInvoiceSymbol((String) row[8]);
            debtClearingSearchDetail.setInvoiceNumber((String) row[9]);
            debtClearingSearchDetail.setDescription((String) row[10]);
            debtClearingSearchDetail.setNote((String) row[11]);
            debtClearingSearchDetail.setCreatedDate((Date) row[12]);

            debtClearingSearchDetail.setCustomerName("");
            debtClearingSearchDetail.setCustomerCode("");
            debtClearingSearchDetail.setCustomerDebtCode("");
            debtClearingSearchDetail.setCustomerDebtName("");

            result.add(debtClearingSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number) session.createNativeQuery(countTotalRecords + "(" + secondJoinStr + ") as r").uniqueResult()).longValue());

        return searchResult;
    }

}
