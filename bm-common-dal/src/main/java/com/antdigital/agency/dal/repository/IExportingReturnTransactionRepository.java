package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingReturnTransaction;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface IExportingReturnTransactionRepository extends JpaRepository<ExportingReturnTransaction, String> {
    @Query("select t from ExportingReturnTransaction t where t.exportingReturn.id = ?1")
    List<ExportingReturnTransaction> getByExportingReturnId(String exportingReturnId);

    @Query("select t from ExportingReturnTransaction t where t.exportingReturn.id = ?1 and t.importingWarehouse.id is not NULL ")
    List<ExportingReturnTransaction> getHasImporting(String exportingReturnId);

    @Query("select t from ExportingReturnTransaction t where t.importingWarehouse.id = ?1")
    List<ExportingReturnTransaction> getByImportingWarehouseId(String importingWarehouseId);

    @Query("select p from ExportingReturnTransaction p where p.exportingReturn.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.exportingReturn.transactionCustomerId = :pCustomerId and (p.exportingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ExportingReturnTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingReturnTransaction p where p.exportingReturn.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.exportingReturn.transactionCustomerId = :pCustomerId")
    List<ExportingReturnTransaction> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingReturnTransaction p where p.exportingReturn.agency.id = :pAgencyId and p.exportingReturn.transactionCustomerId = :pCustomerId and (p.exportingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ExportingReturnTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select count(t) from ExportingReturnTransaction t where t.merchandiseId like %?1%")
    Integer countMerchandise(String id);

    @Query(value = "select * from exporting_return_transaction t where t.import_id in (:idList)", nativeQuery=true)
    List<ExportingReturnTransaction> getByImportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query(countQuery= "select count(u) from ExportingReturnTransaction u where u.importingWarehouse.id = ?1")
    int countByImportingWarehouseId(String importId);

    @Query(value="select round(sum(t.quantity * t.price), 2) from exporting_return_transaction t where t.import_id = ?1 ", nativeQuery=true)
    Double getTotalByImportingWarehouseId(String importingId);

    @Query("select p from ExportingReturnTransaction p where p.exportingReturn.agency.id = :pAgencyId and p.exportingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ExportingReturnTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ExportingReturnTransaction p where p.exportingReturn.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.exportingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ExportingReturnTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
