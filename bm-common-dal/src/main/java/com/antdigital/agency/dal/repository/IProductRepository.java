package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, String> {
    @Query("select t from Product t where t.nameSlug = ?1")
    Product getBySlug(String nameSlug);

    @Query("select t from Product t where t.name = ?1")
    Product getByName(String name);

    @Query("select t from Product t where t.nameSlug like %?1% or t.name like %?1%")
    List<Product> getLikeSlugOrName(String name);

    @Query("select t from Product t where t.nameSlug like %?1%")
    List<Product> getLikeSlug(String nameSlug);

    @Query("select t from Product t where t.id in (?1)")
    List<Product> getProductsById(List<String> ids);
}
