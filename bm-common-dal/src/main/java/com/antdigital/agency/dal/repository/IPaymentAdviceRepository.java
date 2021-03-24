package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ImportingReturn;
import com.antdigital.agency.dal.entity.PaymentAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IPaymentAdviceRepository extends JpaRepository<PaymentAdvice, String> {
    @Query(value= "select * from payment_advice u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from payment_advice u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    PaymentAdvice getPaymentAdviceNumber(Date createdDate, String agencyId);

    @Query(value= "select * from payment_advice u where u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3 and u.agency_id = ?4"
            , nativeQuery=true)
    PaymentAdvice getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query("select p from PaymentAdvice p where p.transactionCustomerId = :pCustomerId and (p.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<PaymentAdvice> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate);

    @Query(value= "select count(u.customer_id) from payment_advice u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query("select u from PaymentAdvice u where u.agency.id = ?1")
    List<PaymentAdvice> findAllByAgencyId(String agencyId);
}
