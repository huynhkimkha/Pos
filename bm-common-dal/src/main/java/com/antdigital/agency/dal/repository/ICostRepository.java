package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dal.entity.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICostRepository extends JpaRepository<Cost, String> {
    @Query("select u from Cost u where u.agency.id = ?1")
    List<Cost> findAllByAgency(String agencyId);

    @Query(value= "select * from cost u where u.agency_id = ?1" , nativeQuery=true)
    Page<Cost> findAllPageByAgency(Pageable var1, String agencyId);
}
