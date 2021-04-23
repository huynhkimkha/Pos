package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ImportingMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IImportingMaterialRepository extends JpaRepository<ImportingMaterial, String> {
    @Query("select u from ImportingMaterial u where u.agency.id = ?1")
    List<ImportingMaterial> findAllByAgency(String agencyId);

    @Query(value= "select * from importing_material u where u.agency_id = ?1" , nativeQuery=true)
    Page<ImportingMaterial> findAllPageByAgency(Pageable var1, String agencyId);

    @Query(value= "select * from importing_material u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from importing_material u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    ImportingMaterial getImportingMaterialNumber(Date createdDate, String agencyId);
}
