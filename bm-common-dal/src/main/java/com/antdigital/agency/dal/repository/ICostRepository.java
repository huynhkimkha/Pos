package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dal.entity.Employees;
import com.antdigital.agency.dal.entity.ImportingMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ICostRepository extends JpaRepository<Cost, String> {
    @Query("select u from Cost u where u.agency.id = ?1")
    List<Cost> findAllByAgency(String agencyId);

    @Query(value= "select * from cost u where u.agency_id = ?1" , nativeQuery=true)
    Page<Cost> findAllPageByAgency(Pageable var1, String agencyId);

    @Query(value= "select * from cost u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from cost u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    Cost getCostNumber(Date createdDate, String agencyId);

    @Query("select u from Cost u where u.description = ?1")
    Cost getByDescription(String description);

    @Query( value ="select * from cost u where u.created_date >= ?1 and u.created_date <= ?2 and u.agency_id = ?3", nativeQuery=true)
    List<Cost> findByrangeDate(String fromDate, String toDate, String agencyId);
}
