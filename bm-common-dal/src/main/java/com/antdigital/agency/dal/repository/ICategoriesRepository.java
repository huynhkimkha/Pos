package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoriesRepository extends JpaRepository<Categories, String> {
    @Query("select u from Categories u where u.name = ?1")
    Categories getByName(String name);

    @Query("select u from Categories u where u.name Like %?1%")
    List<Categories> getLikeName(String name);

}
