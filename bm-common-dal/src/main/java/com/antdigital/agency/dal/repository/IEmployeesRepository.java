package com.antdigital.agency.dal.repository;

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
}
