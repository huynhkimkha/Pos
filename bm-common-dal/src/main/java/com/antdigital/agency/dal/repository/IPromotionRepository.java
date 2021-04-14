package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, String> {
}
