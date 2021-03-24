package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingTransaction;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IExportingTransactionRepository extends JpaRepository<ExportingTransaction, String> {
    @Query("select t from ExportingTransaction t where t.exportingWarehouse.id = ?1")
    List<ExportingTransaction> getByExportingId(String importId);

    @Query(value="select round(sum(t.amount), 2) from exporting_transaction t where t.export_id = ?1 ", nativeQuery=true)
    Double getTotal(String exportingId);

    @Query(value="select round(sum(t.amount), 2) from exporting_transaction t where t.export_id in (:idList)", nativeQuery=true)
    Double getTotalByExportingIdList(@Param("idList") List<String> idList);

    @Query(value="select sum(t.quantity) from ExportingTransaction t where t.order.id = ?1 ")
    Float getTotalQuantityByOrderId(String exportingId);

    @Query("select t from ExportingTransaction t where t.order.id = ?1")
    List<ExportingTransaction> getByOrderId(String orderId);

    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.exportingWarehouse.transactionCustomerId = :pCustomerId and (p.exportingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ExportingTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.exportingWarehouse.transactionCustomerId = :pCustomerId")
    List<ExportingTransaction> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and p.exportingWarehouse.transactionCustomerId = :pCustomerId and (p.exportingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ExportingTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select count(t) from ExportingTransaction t where t.merchandiseId like %?1%")
    Integer countMerchandise(String id);

    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and p.exportingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ExportingTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and p.exportingWarehouse.agency.id = ?3 and p.exportingWarehouse.transactionCustomerId = ?1 and p.merchandiseId = ?2")
    List<ExportingTransaction> getByCustomerIdAndMerchandiseId(String customerId, String merchandiseId, String agencyId);
    @Query("select p from ExportingTransaction p where p.exportingWarehouse.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.exportingWarehouse.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ExportingTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
