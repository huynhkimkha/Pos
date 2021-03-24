package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingTransaction;
import com.antdigital.agency.dal.entity.ImportingTransaction;
import com.antdigital.agency.dal.entity.ImportingWarehouse;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IImportingTransactionRepository extends JpaRepository<ImportingTransaction, String> {
    @Query("select t from ImportingTransaction t where t.importingWarehouse.id = ?1")
    List<ImportingTransaction> getByImportingId(String importId);

    @Query(value="select round(sum(t.amount), 2) from importing_transaction t where t.import_id = ?1 ", nativeQuery=true)
    Double getTotal(String importingId);

    @Query(value="select round(sum(t.amount), 2) from importing_transaction t where t.import_id in (:idList)", nativeQuery=true)
    Double getTotalByImportingIdList(@Param("idList") List<String> idList);

    @Query(value="select sum(t.quantity) from ImportingTransaction t where t.order.id = ?1 ")
    Float getTotalQuantityByOrderId(String orderId);

    @Query("select t from ImportingTransaction t where t.order.id = ?1")
    List<ImportingTransaction> getByOrderId(String orderId);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.importingWarehouse.transactionCustomerId = :pCustomerId and (p.importingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ImportingTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.importingWarehouse.transactionCustomerId = :pCustomerId")
    List<ImportingTransaction > getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.agency.id = :pAgencyId and p.importingWarehouse.transactionCustomerId = :pCustomerId and (p.importingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ImportingTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select count(t) from ImportingTransaction t where t.merchandiseId like %?1%")
    Integer countMerchandise(String id);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.agency.id = :pAgencyId and p.importingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ImportingTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.importingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ImportingTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingTransaction p where p.importingWarehouse.transactionCustomerId = ?1 and p.merchandiseId = ?2")
    List<ImportingTransaction> getByCustomerIdAndMerchandiseId(String customerId, String merchandiseId);
}
