package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPromotionProductRepository extends JpaRepository<PromotionProduct, String>  {
    @Query("select t from PromotionProduct t where t.promotion.id = ?1")
    List<PromotionProduct> getByPromotionId(String id);
}
