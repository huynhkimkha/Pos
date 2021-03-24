package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.DebtClearingDetail;
import com.antdigital.agency.dal.entity.ExportingTransaction;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IDebtClearingDetailRepository extends JpaRepository<DebtClearingDetail, String>{
    @Query("select t from DebtClearingDetail t where t.debtClearing.id = ?1")
    List<DebtClearingDetail> getDebtClearingById(String debtClearingId);

    @Query("select t from DebtClearingDetail t where t.debtClearing.id = ?1 and t.exportingWarehouse.id is not NULL")
    List<DebtClearingDetail> getHasExporting (String debtClearingId);

    @Query(countQuery = "select count(t) from DebtClearingDetail t where t.exportingWarehouse,id = ?1")
    int countByExportingWarehouseId(String exportingId);

    @Query("select sum(t.amount) from DebtClearingDetail t where t.exportingWarehouse.id = ?1")
    Double getTotalByExportingWarehouseId(String exportId);

    @Query("select sum(t.amount) from DebtClearingDetail t where t.exportingWarehouse.id in :idList")
    Double getTotalByExportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query("select p from DebtClearingDetail p where p.debtClearing.agency.id = :pAgencyId and ((p.customerDebtId = :pCustomerId and p.debitAccount = :pAccountingId) " +
            "or (p.creditAccount = :pAccountingId and p.customerId = :pCustomerId)) and (p.debtClearing.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<DebtClearingDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from DebtClearingDetail p where p.debtClearing.agency.id = :pAgencyId and ((p.customerDebtId = :pCustomerId and p.debitAccount != :pAccountingId) " +
            "or (p.creditAccount != :pAccountingId and p.customerId = :pCustomerId))")
    List<DebtClearingDetail > getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from DebtClearingDetail p where p.debtClearing.agency.id = :pAgencyId and (p.customerDebtId = :pCustomerId or p.customerId = :pCustomerId) " +
            "and (p.debtClearing.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<DebtClearingDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query(value= "select count(u.customer_id) from debt_clearing_detail u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query(value= "select count(u.customer_debt_id) from debt_clearing_detail u where u.customer_debt_id = ?1" , nativeQuery=true)
    int countDebtCustomerId(String customerId);

    @Query("select p from DebtClearingDetail p where p.debtClearing.agency.id = :pAgencyId and p.debtClearing.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<DebtClearingDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from DebtClearingDetail p where p.debtClearing.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.debtClearing.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<DebtClearingDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);
}
