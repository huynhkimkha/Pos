package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Bill;
import com.antdigital.agency.dal.entity.Cost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IBillRepository extends JpaRepository<Bill, String> {
    @Query("select u from Bill u where u.agency.id = ?1")
    List<Bill> findAllByAgency(String agencyId);

    @Query(value= "select * from bills u where u.agency_id = ?1" , nativeQuery=true)
    Page<Bill> findAllPageByAgency(Pageable var1, String agencyId);

    @Query(value= "select * from bills u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from bills u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    Bill getBillNumber(Date createdDate, String agencyId);

    @Query( value ="select * from bills u where u.created_date >= ?1 and u.created_date <= ?2 and u.agency_id = ?3", nativeQuery=true)
    List<Bill> findByrangeDate(String fromDate, String toDate, String agencyId);
}
