package com.antdigital.agency.dal.dao.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dal.dao.IImportingWarehouseDao;
import com.antdigital.agency.dal.data.ImportingWarehouseDetail;
import com.antdigital.agency.dal.data.SearchResult;
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
public class ImportingWarehouseDao extends GenericDao implements IImportingWarehouseDao {

    @Transactional
    public SearchResult<List<ImportingWarehouseDetail>> search(String code, String number, String customerAddress, String description
            , String note, List<String> customerIds, List<String> merchandiseIds,PaymentStatusEnum paymentStatus, Date startDate, Date endDate, String createdDateSort, Integer startNumber
            , Integer endNumber, int start, int size, String agencyId) {
        SearchResult<List<ImportingWarehouseDetail>> searchResult = new SearchResult<>();
        Session session = getSession();
        String importQueryStr = "select i.*, format(substring(i.number,4,4),0) as importNumber from importing_warehouse i ";
        String countTotalQueryStr = "select sum(itr.quantity) as totalQuantity, sum(itr.amount) as total, c.* from importing_transaction itr INNER JOIN ";
        String transactionQueryStr = "select i.* from importing_transaction t ";
        String countTotalRecords = "select count(*) from ";

        // import_warehouse where conditions
        List<String> conditions = new ArrayList<>();
        conditions.add(String.format("i.agency_id = '%s'", agencyId));
        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("i.code LIKE '%s%s%s'", "%", code, "%"));
        }

        if(number != null && !number.isEmpty()) {
            conditions.add(String.format("i.number LIKE '%s%s%s'", "%", number, "%"));
        }

        if(customerAddress != null && !customerAddress.isEmpty()) {
            conditions.add(String.format("i.customer_address LIKE '%s%s%s'", "%", customerAddress, "%"));
        }

        if(description != null && !description.isEmpty()) {
            conditions.add(String.format("i.description LIKE '%s%s%s'", "%", description, "%"));
        }

        if(note != null && !note.isEmpty()) {
            conditions.add(String.format("i.note LIKE '%s%s%s'", "%", note, "%"));
        }

        if(paymentStatus != null) {
            conditions.add(String.format("i.payment_status = '%s'", paymentStatus));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(startDate != null){
            conditions.add(String.format("i.created_date >= '%s'", dateFormat.format(startDate)));
        }
        if(endDate != null){
            conditions.add(String.format("i.created_date <= '%s'", dateFormat.format(endDate)));
        }

        if(customerIds.size() > 0) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("i.customer_id in (%s)", customerStr));
        }
        String importWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

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
        // import_warehouse having conditions
        List<String> havingConditions = new ArrayList<>();
        if(startNumber!=null){
            havingConditions.add(String.format(" importNumber >= %s ", startNumber));
        }
        if(endNumber!=null){
            havingConditions.add(String.format(" importNumber <= %s ", endNumber));
        }
        String havingStr = havingConditions.size() > 0 ? " having " + String.join(" and ", havingConditions) : "";

        // import_transaction where conditions
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
        String joinStr = " INNER JOIN"+" ("+ importQueryStr+" "+importWhereStr+havingStr + ") as i ON i.id = t.import_id" +transactionWhereStr + " group by t.import_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = itr.import_id group by c.id"  +" "+ sortStr;
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        if(start >= 0 && size > 0) {
            query.setFirstResult(start*size);
            query.setMaxResults(size);
        }

        List<ImportingWarehouseDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ImportingWarehouseDetail importingWarehouseDetail = new ImportingWarehouseDetail();
            importingWarehouseDetail.setTotalQuantity(((Double)row[0]).floatValue());
            importingWarehouseDetail.setTotal(((Double)row[1]));
            importingWarehouseDetail.setId((String)row[2]);
            Order order = new Order();
            order.setId((String)row[4]);
            importingWarehouseDetail.setOrder(order);
            importingWarehouseDetail.setPaymentStatus( PaymentStatusEnum.valueOf((String)row[5]));
            importingWarehouseDetail.setCode((String)row[6]);
            importingWarehouseDetail.setNumber((String)row[7]);
            importingWarehouseDetail.setInvoiceDate((Date)row[8]);
            importingWarehouseDetail.setInvoiceCode((String)row[9]);
            importingWarehouseDetail.setInvoiceTemplate((String)row[10]);
            importingWarehouseDetail.setInvoiceSymbol((String)row[11]);
            importingWarehouseDetail.setInvoiceNumber((String)row[12]);
            importingWarehouseDetail.setCustomerId((String)row[13]);
            importingWarehouseDetail.setCustomerAddress((String)row[14]);
            importingWarehouseDetail.setCustomerTaxCode((String)row[15]);
            importingWarehouseDetail.setTransactionCustomerId((String)row[16]);
            importingWarehouseDetail.setDescription((String)row[17]);
            importingWarehouseDetail.setNote((String)row[18]);
            importingWarehouseDetail.setForeignCurrency((String)row[19]);
            importingWarehouseDetail.setForeignCurrencyRate((String)row[20]);
            importingWarehouseDetail.setCreatedDate((Date)row[21]);
            importingWarehouseDetail.setCustomerCode("");
            importingWarehouseDetail.setCustomerName("");

            result.add(importingWarehouseDetail);
        }

        searchResult.setResult(result);
        searchResult.setTotalRecords(((Number)session.createNativeQuery(countTotalRecords +"("+secondJoinStr+" ) as r").uniqueResult()).longValue());

        return searchResult;
    }

    @Override
    @Transactional
    public List<ImportingWarehouseDetail> report(Date fromDate, Date toDate, String code, String accountingTableId, List<String> customerIds, List<String> merchandiseIds, String agencyId) {
        Session session = getSession();
        String exportQueryStr = "select i.*, format(substring(i.number,4,4),0) as exportNumber from importing_warehouse i ";
        String countTotalQueryStr = "select sum(itr.quantity) as totalQuantity, sum(itr.amount) as total, c.* from importing_transaction itr INNER JOIN ";
        String transactionQueryStr = "select i.* from importing_transaction t ";
        List<String> conditions = new ArrayList<>();
        if(agencyId != null && !agencyId.isEmpty()){
            conditions.add(String.format("i.agency_id = '%s'", agencyId));
        }

        if(code != null && !code.isEmpty()) {
            conditions.add(String.format("i.code LIKE '%s%s%s'", "%", code, "%"));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(fromDate != null){
            conditions.add(String.format("i.created_date >= '%s'", dateFormat.format(fromDate)));
        }
        if(toDate != null){
            conditions.add(String.format("i.created_date <= '%s'", dateFormat.format(toDate)));
        }

        if(customerIds != null) {
            List<String> customerIdStr = new ArrayList<>();
            for(String id: customerIds){
                customerIdStr.add(String.format("'%s'",id));
            }
            String customerStr = String.join(",", customerIdStr);
            conditions.add(String.format("i.customer_id in (%s)", customerStr));
        }
        String importWhereStr = conditions.size() > 0 ? "where " + String.join(" and ", conditions) : "";

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
        String joinStr = " INNER JOIN"+" ("+ exportQueryStr + " " + importWhereStr + ") as i ON i.id = t.import_id" +transactionWhereStr + " group by t.import_id ";
        String secondJoinStr = countTotalQueryStr + "(" + transactionQueryStr + joinStr + ") as c" + " ON c.id = itr.import_id group by c.id";
        Query<Object[]> query = session.createNativeQuery(secondJoinStr);

        List<ImportingWarehouseDetail> result = new ArrayList<>();
        List<Object[]> resultRows = query.getResultList();
        for(Object[] row: resultRows){
            ImportingWarehouseDetail importingWarehouseDetail = new ImportingWarehouseDetail();
            importingWarehouseDetail.setTotalQuantity(((Double)row[0]).floatValue());
            importingWarehouseDetail.setTotal(((Double)row[1]));
            importingWarehouseDetail.setId((String)row[2]);
            Order order = new Order();
            order.setId((String)row[4]);
            importingWarehouseDetail.setOrder(order);
            importingWarehouseDetail.setPaymentStatus( PaymentStatusEnum.valueOf((String)row[5]));
            importingWarehouseDetail.setCode((String)row[6]);
            importingWarehouseDetail.setNumber((String)row[7]);
            importingWarehouseDetail.setInvoiceDate((Date)row[8]);
            importingWarehouseDetail.setInvoiceCode((String)row[9]);
            importingWarehouseDetail.setInvoiceTemplate((String)row[10]);
            importingWarehouseDetail.setInvoiceSymbol((String)row[11]);
            importingWarehouseDetail.setInvoiceNumber((String)row[12]);
            importingWarehouseDetail.setCustomerId((String)row[13]);
            importingWarehouseDetail.setTransactionCustomerId((String)row[14]);
            importingWarehouseDetail.setCustomerAddress((String)row[15]);
            importingWarehouseDetail.setCustomerTaxCode((String)row[16]);
            importingWarehouseDetail.setDescription((String)row[17]);
            importingWarehouseDetail.setNote((String)row[18]);
            importingWarehouseDetail.setForeignCurrency((String)row[19]);
            importingWarehouseDetail.setForeignCurrencyRate((String)row[20]);
            importingWarehouseDetail.setCreatedDate((Date)row[21]);
            importingWarehouseDetail.setCustomerCode("");
            importingWarehouseDetail.setCustomerName("");

            result.add(importingWarehouseDetail);
        }
        return result;
    }
}
