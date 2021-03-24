package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.GrantPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IGrantPermissionRepository extends JpaRepository<GrantPermission, String> {
    @Query("select u from GrantPermission u where u.role.id = ?1")
    List<GrantPermission> getRoleId(String roleId);
}
