package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, String> {
    @Query("select p from Permission p where p.code in :idList")
    List<Permission> getByCodeList(@Param("idList") List<String> idList);
}
