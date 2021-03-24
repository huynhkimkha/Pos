package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IExportingReturnDao;
import com.antdigital.agency.dal.data.ExportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.ImportingWarehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ExportingReturnDao extends GenericDao implements IExportingReturnDao {

    @Override
    @Transactional
    public List<ExportingReturnSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        Session session = getSession();
        String exportingReturnQueryStr = "select e.*, format(substring(e.number,4,4),0) as exportingReturnNumber from exporting_return e ";
        String countTotalQueryStr = "select sum(etr.quantity) as totalQuantity, sum(etr.amount) as total, c.* from exporting_return_transaction etr INNER JOIN ";
        String transactionQueryStr = "select e.* from exporting_return_transaction t ";
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()){
            conditions.add(String.format("e.agency_id = '%s'", agencyId));
        }

        if (code != null && !code.isEmpty()) {
            conditions.add(String.format("e.code LIKE '%s%s%s'", "%", code, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (fromDate != null) {
            conditions.add(String.format("e.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if (toDate != null) {
            conditions.add(String.format("e.created_date <= '%s'", dateFormat.format(toDate)));
        }

        if (customerIds != null) {
            List<String> customerIdStr = new ArrayList<>();
            for (String id : customerIds) {
                customerIdStr.add(String.format("'%s'", id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("e.customer_id in (%s)", customerStr));
        }

        String exportingReturnWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        // export_transaction where conditions
        List<String> transactionConditions = new ArrayList<>();
        if (merchandiseIds != null) {
            List<String> merchandiseIdStr = new ArrayList<>();
            for (String id : merchandiseIds) {
                merchandiseIdStr.add(String.format("'%s'", id));
            }
            String merchandiseStr = String.join(",", merchandiseIdStr);
            transactionConditions.add(String.format("t.merchandise_id in (%s)", merchandiseStr));
        }

        if (accountingTableId != null && !accountingTableId.isEmpty()) {
            transactionConditions.add(String.format("(t.credit_account = '%s' or t.debit_account = '%s')", accountingTableId, accountingTableId));
        }

        String transactionWhereStr = transactionConditions.size() > 0 ? " where " + String.join(" and ", transactionConditions) : "";

        //query
        String joinStr = " INNER JOIN" + " (" + exportingReturnQueryStr + " " + exportingReturnWhereStr + ") as e ON e.id = t.exporting_return_id" + transactionWhereStr + " group by t.exporting_return_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = etr.exporting_return_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ExportingReturnSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            ExportingReturnSearchDetail exportingReturnSearchDetail = new ExportingReturnSearchDetail();
            exportingReturnSearchDetail.setTotalQuantity(((Double) row[0]).floatValue());
            exportingReturnSearchDetail.setTotal(((Double) row[1]));
            exportingReturnSearchDetail.setId((String) row[2]);
            ImportingWarehouse importingWarehouse = new ImportingWarehouse();
            importingWarehouse.setId((String) row[4]);
            exportingReturnSearchDetail.setImportingWarehouse(importingWarehouse);
            exportingReturnSearchDetail.setCode((String) row[5]);
            exportingReturnSearchDetail.setNumber((String) row[6]);
            exportingReturnSearchDetail.setInvoiceDate((Date) row[7]);
            exportingReturnSearchDetail.setInvoiceCode((String) row[8]);
            exportingReturnSearchDetail.setInvoiceTemplate((String) row[9]);
            exportingReturnSearchDetail.setInvoiceSymbol((String) row[10]);
            exportingReturnSearchDetail.setInvoiceNumber((String) row[11]);
            exportingReturnSearchDetail.setCustomerId((String) row[12]);
            exportingReturnSearchDetail.setTransactionCustomerId((String) row[13]);
            exportingReturnSearchDetail.setCustomerAddress((String) row[14]);
            exportingReturnSearchDetail.setCustomerTaxCode((String) row[15]);
            exportingReturnSearchDetail.setDescription((String) row[16]);
            exportingReturnSearchDetail.setNote((String) row[17]);
            exportingReturnSearchDetail.setForeignCurrency((String) row[18]);
            exportingReturnSearchDetail.setForeignCurrencyRate((String) row[19]);
            exportingReturnSearchDetail.setCreatedDate((Date) row[20]);
            exportingReturnSearchDetail.setCustomerCode("");
            exportingReturnSearchDetail.setCustomerName("");

            result.add(exportingReturnSearchDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public SearchResult<List<ExportingReturnSearchDetail>> search(String code, String number, String customerAddress, String description, String note, List<String> customerIds, List<String> merchandiseIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber, Integer endNumber, int start, int size, String agencyId) {


        SearchResult<List<ExportingReturnSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String exportQueryStr = "select e.*, format(substring(e.number,4,4),0) as exportReturnNumber from exporting_return e ";
        String countTotalQueryStr = "select sum(ert.quantity) as totalQuantity, sum(ert.amount) as total, c.* from exporting_return_transaction ert INNER JOIN ";
        String transactionQueryStr = "select e.* from exporting_return_transaction t ";
        String countTotalRecords = "select count(*) from ";

        // export_return where conditions
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

        if (customerAddress != null && !customerAddress.isEmpty()) {
            conditions.add(String.format("e.customer_address LIKE '%s%s%s'", "%", customerAddress, "%"));
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

        if (customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for (String id : customerIds) {
                customerIdStr.add(String.format("'%s'", id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("e.customer_id in (%s)", customerStr));
        }
        String exportWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

        // exporting_return having conditions
        List<String> havingConditions = new ArrayList<>();
        if (startNumber != null) {
            havingConditions.add(String.format(" exportReturnNumber >= %s ", startNumber));
        }
        if (endNumber != null) {
            havingConditions.add(String.format(" exportReturnNumber <= %s ", endNumber));
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

        // exporting_return_transaction where conditions
        List<String> transactionConditions = new ArrayList<>();
        if (merchandiseIds.size() > 0) {
            List<String> merchandiseIdStr = new ArrayList<>();
            for (String id : merchandiseIds) {
                merchandiseIdStr.add(String.format("'%s'", id));
            }
            String merchandiseStr = String.join(",", merchandiseIdStr);
            transactionConditions.add(String.format("t.merchandise_id in (%s)", merchandiseStr));
        }
        String transactionWhereStr = transactionConditions.size() > 0 ? " where " + String.join(" and ", transactionConditions) : "";

        // query
        String joinStr = " INNER JOIN" + " (" + exportQueryStr + " " + exportWhereStr + havingStr + ") as e ON e.id = t.exporting_return_id" + transactionWhereStr + " group by t.exporting_return_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = ert.exporting_return_id group by c.id" + " " + sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if (start >= 0 && size > 0) {
            query.setFirstResult(start * size);
            query.setMaxResults(size);
        }

        List<ExportingReturnSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            ExportingReturnSearchDetail exportingReturnSearchDetail = new ExportingReturnSearchDetail();
            exportingReturnSearchDetail.setTotalQuantity(((Double) row[0]).floatValue());
            exportingReturnSearchDetail.setTotal(((Double) row[1]));
            exportingReturnSearchDetail.setId((String) row[2]);
            ImportingWarehouse importingWarehouse = new ImportingWarehouse();
            importingWarehouse.setId((String) row[4]);
            exportingReturnSearchDetail.setImportingWarehouse(importingWarehouse);
            exportingReturnSearchDetail.setCode((String) row[5]);
            exportingReturnSearchDetail.setNumber((String) row[6]);
            exportingReturnSearchDetail.setInvoiceDate((Date) row[7]);
            exportingReturnSearchDetail.setInvoiceCode((String) row[8]);
            exportingReturnSearchDetail.setInvoiceTemplate((String) row[9]);
            exportingReturnSearchDetail.setInvoiceSymbol((String) row[10]);
            exportingReturnSearchDetail.setInvoiceNumber((String) row[11]);
            exportingReturnSearchDetail.setCustomerId((String) row[12]);
            exportingReturnSearchDetail.setCustomerAddress((String) row[13]);
            exportingReturnSearchDetail.setCustomerTaxCode((String) row[14]);
            exportingReturnSearchDetail.setTransactionCustomerId((String) row[15]);
            exportingReturnSearchDetail.setDescription((String) row[16]);
            exportingReturnSearchDetail.setNote((String) row[17]);
            exportingReturnSearchDetail.setForeignCurrency((String) row[18]);
            exportingReturnSearchDetail.setForeignCurrencyRate((String) row[19]);
            exportingReturnSearchDetail.setCreatedDate((Date) row[20]);
            exportingReturnSearchDetail.setCustomerCode("");
            exportingReturnSearchDetail.setCustomerName("");

            result.add(exportingReturnSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number) session.createNativeQuery(countTotalRecords + "(" + secondJoinStr + " ) as r").uniqueResult()).longValue());

        return searchResult;
    }
}
