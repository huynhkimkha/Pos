package com.antdigital.agency.dal.repository;
import com.antdigital.agency.dal.entity.ReceiptAdviceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IReceiptAdviceDetailRepository extends JpaRepository<ReceiptAdviceDetail, String> {
    @Query("select t from ReceiptAdviceDetail t where t.receiptAdvice.id = ?1")
    List<ReceiptAdviceDetail> getByReceiptAdviceId(String receiptAdviceId);

    @Query("select t from ReceiptAdviceDetail t where t.receiptAdvice.id = ?1 and t.exportingWarehouse.id is not NULL ")
    List<ReceiptAdviceDetail> getHasExporting(String receiptAdviceId);

    @Query(countQuery="select count(t) from ReceiptAdviceDetail t where t.exportingWarehouse.id = ?1")
    int countByExportingWarehouseId(String exportId);

    @Query("select sum(t.amount) from ReceiptAdviceDetail t where t.exportingWarehouse.id = ?1")
    Double getTotalByExportingWarehouseId(String exportId);

    @Query("select sum(t.amount) from ReceiptAdviceDetail t where t.exportingWarehouse.id in :idList")
    Double getTotalByExportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query("select p from ReceiptAdviceDetail p where p.receiptAdvice.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.receiptAdvice.transactionCustomerId = :pCustomerId and (p.receiptAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ReceiptAdviceDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptAdviceDetail p where p.receiptAdvice.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.receiptAdvice.transactionCustomerId = :pCustomerId")
    List<ReceiptAdviceDetail > getByCustomerIdNotDefault(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptAdviceDetail p where p.receiptAdvice.agency.id = :pAgencyId and p.receiptAdvice.transactionCustomerId = :pCustomerId and (p.receiptAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ReceiptAdviceDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptAdviceDetail p where p.receiptAdvice.agency.id = :pAgencyId and p.receiptAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ReceiptAdviceDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptAdviceDetail p where p.receiptAdvice.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.receiptAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ReceiptAdviceDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
