package com.antdigital.agency.dal.repository;
import com.antdigital.agency.dal.entity.PaymentAdviceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
@Repository
public interface IPaymentAdviceDetailRepository extends JpaRepository<PaymentAdviceDetail, String>{
    @Query("select t from PaymentAdviceDetail t where t.paymentAdvice.id = ?1")
    List<PaymentAdviceDetail> getByPaymentAdviceId(String paymentAdviceId);

    @Query("select p from PaymentAdviceDetail p where p.referralBonus.id = ?1")
    List<PaymentAdviceDetail> getByRefferalBonusId(String id);

    @Query("select t from PaymentAdviceDetail t where t.paymentAdvice.id = ?1 and t.importingWarehouse.id is not NULL ")
    List<PaymentAdviceDetail> getHasImporting(String paymentAdviceId);

    @Query("select t from PaymentAdviceDetail t where t.paymentAdvice.id = ?1 and t.referralBonus.id is not NULL ")
    List<PaymentAdviceDetail> getHasReferralBonus(String paymentId);

    @Query("select t from PaymentAdviceDetail t where t.paymentAdvice.id = ?1 and t.sellingBonus.id is not NULL ")
    List<PaymentAdviceDetail> getHasSellingBonus(String paymentId);

    @Query("select t from PaymentAdviceDetail t where t.importingWarehouse.id = ?1")
    List<PaymentAdviceDetail> getByImportingId(String importingId);

    @Query(countQuery= "select count(u) from PaymentAdviceDetail u where u.importingWarehouse.id = ?1")
    int countByImportingWarehouseId(String importId);

    @Query("select sum(u.amount) from PaymentAdviceDetail u where u.importingWarehouse.id = ?1")
    Double getTotalByImportingWarehouseId(String importId);

    @Query("select sum(t.amount) from PaymentAdviceDetail t where t.importingWarehouse.id in :idList")
    Double getTotalByImportingWarehouseIdList(@Param("idList") List<String> idList);

    @Query("select p from PaymentAdviceDetail p where p.paymentAdvice.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.paymentAdvice.transactionCustomerId = :pCustomerId and (p.paymentAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<PaymentAdviceDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate,
                                                            @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentAdviceDetail p where p.paymentAdvice.agency.id = :pAgencyId and (p.creditAccount != :pAccountingId and p.debitAccount != :pAccountingId) " +
            "and p.paymentAdvice.transactionCustomerId = :pCustomerId")
    List<PaymentAdviceDetail> getByCustomerIdNotDefaultAccount(@Param("pCustomerId") String customerId, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentAdviceDetail p where p.paymentAdvice.agency.id = :pAgencyId and p.paymentAdvice.transactionCustomerId = :pCustomerId and (p.paymentAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<PaymentAdviceDetail> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentAdviceDetail p where p.paymentAdvice.agency.id = :pAgencyId and p.paymentAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<PaymentAdviceDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAgencyId") String agencyId);

    @Query("select p from PaymentAdviceDetail p where p.paymentAdvice.agency.id = :pAgencyId and (p.creditAccount = :pAccountingId or p.debitAccount = :pAccountingId) " +
            "and p.paymentAdvice.createdDate between DATE(:pFromDate) and DATE(:pToDate)")
    List<PaymentAdviceDetail> getByCreatedDate(@Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate, @Param("pAccountingId") String accountingId, @Param("pAgencyId") String agencyId);

    @Query("select sum(u.amount) from PaymentAdviceDetail u where u.referralBonus.id = ?1")
    Double getTotalByReferralBonusId(String referralBonusId);

    @Query("select sum(u.amount) from PaymentAdviceDetail u where u.sellingBonus.id = ?1")
    Double getTotalBySellingBonusId(String sellingBonusId);
}
