package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductCategoryRepository extends JpaRepository<ProductCategory, String> {
    @Query("select count(u) from ProductCategory u where u.category.id = ?1")
    int countByCategory(String id);

    @Query("select t from ProductCategory t where t.product.id = ?1")
    List<ProductCategory> getByProductId(String id);
}
