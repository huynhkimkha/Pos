package com.antdigital.agency.dal.repository;


import com.antdigital.agency.dal.entity.DebtClearing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IDebtClearingRepository extends JpaRepository<DebtClearing, String> {
    @Query("select p from DebtClearing p where p.agency.id = ?1")
    List<DebtClearing> findAll(String agencyId);

    @Query("select p from DebtClearing p where p.agency.id = ?1")
    Page<DebtClearing> findAll(Pageable var1, String agencyId);

    @Query(value= "select * from debt_clearing u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned)" +
            "and u.agency_id = ?2 from debt_clearing u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    DebtClearing getDebtClearingNumber(Date createdDate, String agencyId);

    @Query(value= "select * from debt_clearing u where u.code = ?1 and u.number = ?2 and YEAR(created_date) = ?3 and u.agency_id = ?4", nativeQuery=true)
    DebtClearing getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query("select t from DebtClearing t where t.id = ?1")
    List<DebtClearing> getDebtClearingById(String debtClearingId);
}
