package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingWarehouse;
import com.antdigital.agency.dal.entity.ReferralBonus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReferralBonusRepository extends JpaRepository<ReferralBonus, String> {
    @Query("select r from ReferralBonus r where r.collaborator.id = ?1")
    ReferralBonus getByCollaborator(String collaboratorId);

    @Query("select r from ReferralBonus r where r.collaborator.id = ?1 and r.activatedStatus <> 'NONACTIVATED'")
    ReferralBonus getByCollaboratorActivated(String collaboratorId);

    @Query("select r from ReferralBonus r where r.collaboratorRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getByCollaboratorRef(String collaboratorRefId);

    @Query("select r from ReferralBonus r where r.collaboratorRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED' and r.paymentStatus is not null")
    List<ReferralBonus> getByCollaboratorRefWithPayment(String collaboratorRefId);

    @Query("select r from ReferralBonus r where r.collaboratorRef.id = ?1 and r.paymentStatus <> 'COMPLETED' and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getBonusNotCompleteByCollaboratorRef(String collaboratorRefId);

    @Query("select r from ReferralBonus r where r.collaboratorRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED' and r.agency.id = ?2")
    Page<ReferralBonus> getByCollaboratorRefPaging(Pageable var1, String collaboratorRefId, String agencyId);

    @Query("select r from ReferralBonus r where r.employeeRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED'")
    Page<ReferralBonus> getByEmployeeRefPaging(Pageable var1, String employeeRefId);

    @Query("select r from ReferralBonus r where r.employee.id = ?1")
    ReferralBonus getByEmployee(String employeeId);

    @Query("select r from ReferralBonus r where r.employee.id = ?1 and r.activatedStatus <> 'NONACTIVATED'")
    ReferralBonus getByEmployeeActivated(String employeeId);

    @Query("select r from ReferralBonus r where r.employeeRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getByEmployeeRef(String employeeRefId);

    @Query("select r from ReferralBonus r where r.employeeRef.id = ?1 and r.activatedStatus <> 'NONACTIVATED' and r.paymentStatus is not null")
    List<ReferralBonus> getByEmployeeRefWithPayment(String employeeRefId);

    @Query("select r from ReferralBonus r where r.employeeRef.id = ?1 and r.paymentStatus <> 'COMPLETED' and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getBonusNotCompleteByEmployeeRef(String employeeRefId);

    @Query(value="select round(sum(t.amount), 2) from referral_bonus t where t.id = ?1 ", nativeQuery=true)
    Double getTotal(String id);

    @Query("select distinct r from ReferralBonus r where (r.collaboratorRef.id in :idList or r.collaborator.id in :idList) and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getByCollaboratorAndCollaboratorRef(@Param("idList") List<String> idList);

    @Query("select distinct r from ReferralBonus r where (r.employeeRef.id in :idList or r.employee.id in :idList) and r.activatedStatus <> 'NONACTIVATED'")
    List<ReferralBonus> getByEmployeeAndEmployeeRef(@Param("idList") List<String> idList);

    @Query(value="select round(sum(t.amount), 2) from referral_bonus t where t.created_date >= ?1 and t.created_date <= ?2 and t.employee_ref_id = ?3 and activated_status <> 'NONACTIVATED'", nativeQuery=true)
    Double getTotalByRangeDateAndEmployeeRefId(String fromDate, String toDate, String employeeRefId);

    @Query(value="select round(sum(t.amount), 2) from referral_bonus t where t.created_date >= ?1 and t.created_date <= ?2 and t.collaborator_ref_id = ?3 and activated_status <> 'NONACTIVATED'", nativeQuery=true)
    Double getTotalByRangeDateAndCollaboratorRefId(String fromDate, String toDate, String collaboratorRefId);
}
