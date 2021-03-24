package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Payment;
import com.antdigital.agency.dal.entity.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IPaymentDetailRepository extends JpaRepository<PaymentDetail, String> {
    @Query("select t from PaymentDetail t where t.payment.id = ?1")
    List<PaymentDetail> getByPaymentId(String paymentId);

    @Query("select t from PaymentDetail t where t.payment.id = ?1 and t.importingWarehouse.id is not NULL ")
    List<PaymentDetail> getHasImporting(String paymentId);

    @Query("select t from PaymentDetail t where t.payment.id = ?1 and t.referralBonus.id is not NULL ")
    List<PaymentDetail> getHasReferralBonus(String paymentId);

    @Query("select t from PaymentDetail t where t.payment.id = ?1 and t.sellingBonus.id is not NULL ")
    List<PaymentDetail> getHasSellingBonus(String paymentId);

    @Query("select t from PaymentDetail t where t.importingWarehouse.id = ?1")
    List<PaymentDetail> getByImportingId(String importingId);

    @Query(countQuery= "select count(u) from PaymentDetail u where u.importingWarehouse.id = ?1")
    int countByImportingWarehouseId(String importId);

    @Query("select sum(u.amount) from PaymentDetail u where u.importingWarehouse.id = ?1")
    Double getTotalByImportingWarehouseId(String importId);

    @Query("select sum(t.amount) from PaymentDetail t where t.importingWarehouse.id in :idList")
    Double getTotalByImportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query("select p from PaymentDetail p where p.payment.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.payment.transactionCustomerId = :pCustomerId and (p.payment.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<PaymentDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate,
                                                      @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentDetail p where p.payment.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.payment.transactionCustomerId = :pCustomerId")
    List<PaymentDetail> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentDetail p where p.payment.agency.id = :pAgencyId and p.payment.transactionCustomerId = :pCustomerId and (p.payment.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<PaymentDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentDetail p where p.payment.agency.id = :pAgencyId and p.payment.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<PaymentDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentDetail p where p.payment.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.payment.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<PaymentDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select sum(u.amount) from PaymentDetail u where u.referralBonus.id = ?1")
    Double getTotalByReferralBonusId(String referralBonusId);

    @Query("select sum(u.amount) from PaymentDetail u where u.sellingBonus.id = ?1")
    Double getTotalBySellingBonusId(String sellingBonusId);

    @Query("select p from PaymentDetail p where p.referralBonus.id = ?1")
    List<PaymentDetail> getByRefferalBonusId(String id);

}
