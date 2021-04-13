package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductSizeRepository extends JpaRepository<ProductSize, String> {
    @Query("select count(u) from ProductSize u where u.size.id = ?1")
    int countBySize(String id);

    @Query("select t from ProductSize t where t.product.id = ?1")
    List<ProductSize> getByProductId(String id);
}
