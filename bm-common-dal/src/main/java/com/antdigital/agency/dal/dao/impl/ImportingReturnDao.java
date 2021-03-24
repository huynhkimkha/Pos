package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.dal.dao.IImportingReturnDao;
import com.antdigital.agency.dal.data.ImportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.ExportingWarehouse;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ImportingReturnDao extends GenericDao implements IImportingReturnDao {

    @Override
    @Transactional
    public List<ImportingReturnSearchDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        Session session = getSession();
        String importingReturnQueryStr = "select e.*, format(substring(e.number,4,4),0) as exportingReturnNumber from importing_return e ";
        String countTotalQueryStr = "select sum(etr.quantity) as totalQuantity, sum(etr.quantity*etr.price) as total, c.* from importing_return_transaction etr INNER JOIN ";
        String transactionQueryStr = "select e.* from importing_return_transaction t ";
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

        String importingReturnWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

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
        String joinStr = " INNER JOIN"+" ("+ importingReturnQueryStr + " " + importingReturnWhereStr + ") as e ON e.id = t.importing_return_id" +transactionWhereStr + " group by t.importing_return_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = etr.importing_return_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ImportingReturnSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ImportingReturnSearchDetail importingReturnSearchDetail = new ImportingReturnSearchDetail();
            importingReturnSearchDetail.setTotalQuantity(((Double)row[0]).floatValue());
            importingReturnSearchDetail.setTotal(((Double)row[1]));
            importingReturnSearchDetail.setId((String)row[2]);
            ExportingWarehouse exportingWarehouse = new ExportingWarehouse();
            exportingWarehouse.setId((String)row[4]);
            importingReturnSearchDetail.setExportingWarehouse(exportingWarehouse);
            importingReturnSearchDetail.setCode((String)row[5]);
            importingReturnSearchDetail.setNumber((String)row[6]);
            importingReturnSearchDetail.setInvoiceDate((Date)row[7]);
            importingReturnSearchDetail.setInvoiceCode((String)row[8]);
            importingReturnSearchDetail.setInvoiceTemplate((String)row[9]);
            importingReturnSearchDetail.setInvoiceSymbol((String)row[10]);
            importingReturnSearchDetail.setInvoiceNumber((String)row[11]);
            importingReturnSearchDetail.setCustomerId((String)row[12]);
            importingReturnSearchDetail.setTransactionCustomerId((String)row[13]);
            importingReturnSearchDetail.setCustomerAddress((String)row[14]);
            importingReturnSearchDetail.setCustomerTaxCode((String)row[15]);
            importingReturnSearchDetail.setDescription((String)row[16]);
            importingReturnSearchDetail.setNote((String)row[17]);
            importingReturnSearchDetail.setForeignCurrency((String)row[18]);
            importingReturnSearchDetail.setForeignCurrencyRate((String)row[19]);
            importingReturnSearchDetail.setCreatedDate((Date)row[20]);
            importingReturnSearchDetail.setCustomerCode("");
            importingReturnSearchDetail.setCustomerName("");

            result.add(importingReturnSearchDetail);
        }
        return result;
    }

    @Override
    @Transactional
    public SearchResult<List<ImportingReturnSearchDetail>> search(String code, String number, String customerAddress, String description, String note, List<String> customerIds, List<String> merchandiseIds, Date startDate, Date endDate, String createdDateSort, Integer startNumber, Integer endNumber, int start, int size, String agencyId) {

        SearchResult<List<ImportingReturnSearchDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String importReturnQueryStr = "select e.*, format(substring(e.number,4,4),0) as importReturnNumber from importing_return e ";
        String countTotalQueryStr = "select sum(irt.quantity) as totalQuantity, sum(irt.amount) as total, c.* from importing_return_transaction irt INNER JOIN ";
        String transactionQueryStr = "select e.* from importing_return_transaction t ";
        String countTotalRecords = "select count(*) from ";

        // importing_return where conditions
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            conditions.add(String.format("e.created_date >= '%s'", simpleDateFormat.format(startDate)));
        }
        if (endDate != null) {
            conditions.add(String.format("e.created_date <= '%s'", simpleDateFormat.format(endDate)));
        }

        if (customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for (String id : customerIds) {
                customerIdStr.add(String.format("'%s'", id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("e.customer_id in (%s)", customerStr));
        }
        String importReturnWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

//        importing_return having conditions
        List<String> havingConditions = new ArrayList<>();
        if (startNumber != null) {
            havingConditions.add(String.format(" importReturnNumber >= %s ", startNumber));
        }
        if (endNumber != null) {
            havingConditions.add(String.format(" importReturnNumber <= %s ", endNumber));
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

        // importing_return_transaction
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

        // Query
        String joinStr = " INNER JOIN (" + importReturnQueryStr + " " + importReturnWhereStr + havingStr + ") as e ON e.id = t.importing_return_id" + transactionWhereStr + " group by t.importing_return_id";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c ON c.id = irt.importing_return_id group by c.id " + sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if (start >= 0 && size > 0) {
            query.setFirstResult(start * size);
            query.setMaxResults(size);
        }

        List<ImportingReturnSearchDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for (Object[] row : resultRows) {
            ImportingReturnSearchDetail importingReturnSearchDetail = new ImportingReturnSearchDetail();
            importingReturnSearchDetail.setTotalQuantity(((Double) row[0]).floatValue());
            importingReturnSearchDetail.setTotal((Double) row[1]);
            importingReturnSearchDetail.setId((String) row[2]);
            importingReturnSearchDetail.setCode((String) row[4]);
            ExportingWarehouse exportingWarehouse = new ExportingWarehouse();
            exportingWarehouse.setId((String) row[5]);
            importingReturnSearchDetail.setExportingWarehouse(exportingWarehouse);
            importingReturnSearchDetail.setNumber((String) row[6]);
            importingReturnSearchDetail.setInvoiceDate((Date) row[7]);
            importingReturnSearchDetail.setInvoiceCode((String) row[8]);
            importingReturnSearchDetail.setInvoiceTemplate((String) row[9]);
            importingReturnSearchDetail.setInvoiceSymbol((String) row[10]);
            importingReturnSearchDetail.setInvoiceNumber((String) row[11]);
            importingReturnSearchDetail.setCustomerId((String) row[12]);
            importingReturnSearchDetail.setCustomerAddress((String) row[13]);
            importingReturnSearchDetail.setCustomerTaxCode((String) row[14]);
            importingReturnSearchDetail.setTransactionCustomerId((String) row[15]);
            importingReturnSearchDetail.setDescription((String) row[16]);
            importingReturnSearchDetail.setNote((String) row[17]);
            importingReturnSearchDetail.setForeignCurrency((String) row[18]);
            importingReturnSearchDetail.setForeignCurrencyRate((String) row[19]);
            importingReturnSearchDetail.setCreatedDate((Date) row[20]);
            importingReturnSearchDetail.setCustomerCode("");
            importingReturnSearchDetail.setCustomerName("");

            result.add(importingReturnSearchDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number) session.createNativeQuery(countTotalRecords + "(" + secondJoinStr + " ) as r").uniqueResult()).longValue());

        return searchResult;
    }
}
