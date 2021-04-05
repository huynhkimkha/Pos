package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICostRepository extends JpaRepository<Cost, String> {
}
