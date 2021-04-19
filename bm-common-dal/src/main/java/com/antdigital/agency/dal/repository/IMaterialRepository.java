package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMaterialRepository extends JpaRepository<Material, String>  {
}
