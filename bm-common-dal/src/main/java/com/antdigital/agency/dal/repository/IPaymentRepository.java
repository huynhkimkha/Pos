package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IPaymentRepository extends JpaRepository<Payment, String> {
    @Query(value= "select * from payment u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from payment u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    Payment getPaymentNumber(Date createdDate, String agencyId);

    @Query(value= "select * from payment u where u.agency_id = ?4 and u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3"
            , nativeQuery=true)
    Payment getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query("select p from Payment p where p.transactionCustomerId = :pCustomerId and (p.createdDate between DATE(:pFromDate) and DATE(:pToDate))")
    List<Payment> getByCustomerIdAndCreatedDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pToDate") Date toDate);

    @Query(value= "select count(u.customer_id) from payment u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query(value= "select * from payment u where u.agency_id = ?1" , nativeQuery=true)
    List<Payment> findAllByAgencyId(String agencyId);

    @Query(value= "select * from payment u where u.agency_id = ?1" , nativeQuery=true)
    Page<Payment> findAllByAgencyId(Pageable var1, String agencyId);
}
