package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dal.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, String> {
    @Query(value = "select * from promotion t where t.expired_date >= ?1", nativeQuery=true)
    List<Promotion> findAllPromotion(String now);

    @Query(value= "select * from promotion u where u.expired_date >= ?1" , nativeQuery=true)
    Page<Promotion> findAllPage(Pageable var1, String now);
}
