package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Collaborator;
import com.antdigital.agency.dal.entity.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICollaboratorRepository extends JpaRepository<Collaborator, String> {

    @Query("select u from Collaborator u where u.activatedStatus <> 'NONACTIVATED' and u.agency.id = ?1")
    Page<Collaborator> findAllNonActivated(Pageable var, String agencyId);

    @Query("select u from Collaborator u where u.email = ?1 and u.agency.companyId = ?2")
    Collaborator getByEmail(String email, String companyId);

    @Query("select u from Collaborator u where u.phone = ?1 and u.agency.companyId = ?2")
    Collaborator getByPhone(String phone, String companyId);

    @Query("select u from Collaborator u where u.agency.id = ?2 and u.fullName like %?1% and u.activatedStatus <> 'NONACTIVATED'")
    List<Collaborator> getLikeName(String nameOrCode, String agencyId);

    @Query("select u from Collaborator u where u.id in (?1) ")
    List<Collaborator> getCollaboratorsById(List<String> ids);

    @Query(value= "select count(u.id) from collaborators u where u.id = ?1" , nativeQuery=true)
    int countCollaboratorId(String collaboratorId);

    @Query(value= "select count(u.phone) from collaborators u where u.phone = ?1" , nativeQuery=true)
    int countCollaboratorPhone(String phone);

    @Query(value= "select count(u.email) from collaborators u where u.email = ?1" , nativeQuery=true)
    int countCollaboratorEmail(String email);

    @Query(value= "select * from collaborators u where u.agency_id = ?2 and (u.full_name like %?1% or u.email like %?1%) and (u.activated_status <> 'NONACTIVATED' and u.blocked_status <> 'BLOCKED')" , nativeQuery=true)
    List<Collaborator> getCollaboratorLikeNameOrEmail(String collaboratorName, String companyId);

    @Query("select u from Collaborator u where u.agency.id = ?1")
    List<Collaborator> findAllByAgencyId(String agencyId);

    @Query(value= "select * from collaborators u where u.agency_id = ?1" , nativeQuery=true)
    Page<Collaborator> findAllBaseSearchByAgencyId(Pageable var1, String agencyId);
}
