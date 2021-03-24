package com.antdigital.agency.dal.repository;
import com.antdigital.agency.dal.entity.ReceiptAdvice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IReceiptAdviceRepository extends JpaRepository<ReceiptAdvice, String> {
    @Query("select p from ReceiptAdvice p where p.agency.id = ?1")
    List<ReceiptAdvice> findAll(String agencyId);

    @Query("select p from ReceiptAdvice p where p.agency.id = ?1")
    Page<ReceiptAdvice> findAll(Pageable var1, String agencyId);

    @Query(value= "select * from receipt_advice u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned)" +
            "and u.agency_id = ?2 from receipt_advice u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    ReceiptAdvice getReceiptAdviceNumber(Date createdDate, String agencyId);

    @Query(value= "select * from receipt_advice u where u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3 and u.agency_id = ?4"
            , nativeQuery=true)
    ReceiptAdvice getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query("select p from ReceiptAdvice p where p.transactionCustomerId = :pCustomerId and (p.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<ReceiptAdvice> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate);

    @Query(value= "select count(u.customer_id) from receipt_advice u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);
}
