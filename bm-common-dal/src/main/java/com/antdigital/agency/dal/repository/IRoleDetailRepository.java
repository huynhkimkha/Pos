package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.RoleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoleDetailRepository extends JpaRepository<RoleDetail, String> {
    @Query("select t from RoleDetail t where t.employee.id = ?1")
    List<RoleDetail> getDetailsByEmployeeId(String id);
}
