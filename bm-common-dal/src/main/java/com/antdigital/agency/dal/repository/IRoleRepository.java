package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IRoleRepository extends JpaRepository<Role, String> {
    @Query("select r from Role r where r.agency.id = ?2 and r.name = ?1")
    Role getByName(String name, String agencyId);

    @Query("select r from Role r where r.agency.id = ?1")
    List<Role> findAllByAgencyId(String agencyId);

    @Query("select r from Role r where r.agency.id = ?1")
    Page<Role> findAllByAgencyId(Pageable var1, String agencyId);

    @Query("select t from Role t where t.name = ?1 and t.agency.id in ?2")
    List<Role> findAllByNameAndAgencyIdIn(String name, List<String> agencyId);
}
