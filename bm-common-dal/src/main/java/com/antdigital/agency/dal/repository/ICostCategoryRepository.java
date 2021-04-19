package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.CostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICostCategoryRepository extends JpaRepository<CostCategory, String> {
}
