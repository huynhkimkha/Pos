package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ReceiptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IReceiptDetailRepository extends JpaRepository<ReceiptDetail, String> {
    @Query("select t from ReceiptDetail t where t.receipt.id = ?1")
    List<ReceiptDetail> getByReceiptId(String receiptId);

    @Query("select t from ReceiptDetail t where t.receipt.id = ?1 and t.exportingWarehouse.id is not NULL ")
    List<ReceiptDetail> getHasExporting(String receiptId);

    @Query(countQuery="select count(t) from ReceiptDetail t where t.exportingWarehouse.id = ?1")
    int countByExportingWarehouseId(String exportId);

    @Query("select sum(t.amount) from ReceiptDetail t where t.exportingWarehouse.id = ?1")
    Double getTotalByExportingWarehouseId(String exportId);

    @Query("select sum(t.amount) from ReceiptDetail t where t.exportingWarehouse.id in :idList")
    Double getTotalByExportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query("select p from ReceiptDetail p where p.receipt.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.receipt.transactionCustomerId = :pCustomerId and (p.receipt.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ReceiptDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptDetail p where p.receipt.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.receipt.transactionCustomerId = :pCustomerId")
    List<ReceiptDetail> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptDetail p where p.receipt.agency.id = :pAgencyId and p.receipt.transactionCustomerId = :pCustomerId and (p.receipt.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ReceiptDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptDetail p where p.receipt.agency.id = :pAgencyId and p.receipt.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ReceiptDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ReceiptDetail p where p.receipt.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.receipt.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ReceiptDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
