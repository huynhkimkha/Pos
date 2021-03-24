package com.antdigital.agency.dal.repository;


import com.antdigital.agency.dal.entity.ImportingReturn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IImportingReturnRepository extends JpaRepository<ImportingReturn, String>{

    @Query(value= "select * from importing_return u where u. MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from importing_return u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    ImportingReturn getImportReturnNumber(Date createdDate, String agencyId);

    @Query(value= "select * from importing_return u where u.code = ?1 and u.number = ?2 and YEAR(created_date) = ?3 and u.agency_id = ?4"
            , nativeQuery=true)
    ImportingReturn getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query(value= "select * from importing_return u where (u.customer_id = ?1 or u.transaction_customer_id = ?1) " +
            "and u.payment_status <> 'COMPLETED' and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?2%", nativeQuery=true)
    List<ImportingReturn> getNotCompleted(String customerId, String code);

    @Query(value= "select count(u.customer_id) from importing_return u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query("select u from ImportingReturn u where u.agency.id = ?1")
    List<ImportingReturn> findAllImportinReturn(String agencyId);

    Page<ImportingReturn> findAllByAgencyId(Pageable var1, String agencyId);
}
