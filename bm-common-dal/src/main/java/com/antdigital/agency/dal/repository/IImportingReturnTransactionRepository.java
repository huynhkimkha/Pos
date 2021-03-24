package com.antdigital.agency.dal.repository;
import com.antdigital.agency.dal.entity.ExportingReturnTransaction;
import com.antdigital.agency.dal.entity.ImportingReturnTransaction;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IImportingReturnTransactionRepository extends JpaRepository<ImportingReturnTransaction, String>{

    @Query("select t from ImportingReturnTransaction t where t.importingReturn.id = ?1")
    List<ImportingReturnTransaction> getByImportingId(String importId);

    @Query("select t from ImportingReturnTransaction t where t.importingReturn.id = ?1 and t.exportingWarehouse.id is not NULL ")
    List<ImportingReturnTransaction> getHasExporting(String importingReturnId);

    @Query(value="select sum(t.amount) from importing_return_transaction t where t.import_return_id = ?1 ", nativeQuery=true)
    Double getTotal(String importingId);

    @Query(value="select * from importing_return_transaction t where t.exporting_warehouse_id = ?1", nativeQuery=true)
    List<ImportingReturnTransaction> getTransactionById(String exportId);

    @Query("select p from ImportingReturnTransaction p where p.importingReturn.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.importingReturn.transactionCustomerId = :pCustomerId and (p.importingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ImportingReturnTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingReturnTransaction p where p.importingReturn.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.importingReturn.transactionCustomerId = :pCustomerId")
    List<ImportingReturnTransaction> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingReturnTransaction p where p.importingReturn.agency.id = :pAgencyId and p.importingReturn.transactionCustomerId = :pCustomerId and (p.importingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ImportingReturnTransaction> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select count(t) from ImportingReturnTransaction t where t.merchandiseId like %?1%")
    Integer countMerchandise(String id);

    @Query(value = "select * from importing_return_transaction t where t.exporting_warehouse_id in (:idList)", nativeQuery=true)
    List<ImportingReturnTransaction> getByExportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query(countQuery= "select count(u) from ImportingReturnTransaction u where u.exportingWarehouse.id = ?1")
    int countByExportingWarehouseId(String exportId);

    @Query(value="select round(sum(t.quantity * t.price), 2) from importing_return_transaction t where t.exporting_warehouse_id = ?1 ", nativeQuery=true)
    Double getTotalByExportingWarehouseId(String exportingId);

    @Query("select p from ImportingReturnTransaction p where p.importingReturn.agency.id = :pAgencyId and p.importingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ImportingReturnTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from ImportingReturnTransaction p where p.importingReturn.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.importingReturn.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<ImportingReturnTransaction> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
