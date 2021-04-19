package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInventoryRepository extends JpaRepository<Inventory, String>  {
    @Query("select u from Inventory u where u.agency.id = ?1")
    List<Inventory> findAllByAgency(String agencyId);

    @Query(value= "select * from inventory u where u.agency_id = ?1" , nativeQuery=true)
    Page<Inventory> findAllPageByAgency(Pageable var1, String agencyId);
}
