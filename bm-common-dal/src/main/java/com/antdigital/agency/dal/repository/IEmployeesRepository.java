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
public interface IEmployeesRepository extends JpaRepository<Employees, String> {
    @Query("select u from Employees u where u.email = ?1")
    Employees getEmployeeByEmail(String email);

    @Query("select u from Employees u where u.agency.id = ?1")
    List<Employees> findAllByAgency(String agencyId);

    @Query(value= "select * from employees u where u.agency_id = ?1" , nativeQuery=true)
    Page<Employees> findAllPageByAgency(Pageable var1, String agencyId);
}
