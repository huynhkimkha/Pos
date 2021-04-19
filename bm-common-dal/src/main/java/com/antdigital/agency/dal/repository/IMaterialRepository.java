package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMaterialRepository extends JpaRepository<Material, String>  {
    @Query("select t from Material t where t.name like %?1%")
    List<Material> getLikeName(String name);
}
