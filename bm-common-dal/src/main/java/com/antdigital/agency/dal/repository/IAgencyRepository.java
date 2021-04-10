package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAgencyRepository extends JpaRepository<Agency, String> {

}
