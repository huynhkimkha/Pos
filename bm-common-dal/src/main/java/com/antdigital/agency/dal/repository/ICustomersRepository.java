package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Customers;
import com.antdigital.agency.dal.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomersRepository extends JpaRepository<Customers, String> {
    @Query("select u from Customers u where u.phone = ?1")
    Customers getCustomerByPhone(String phone);
}
