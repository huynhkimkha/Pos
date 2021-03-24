package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ReferralBonus;
import com.antdigital.agency.dal.entity.SellingBonus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISellingBonusRepository extends JpaRepository<SellingBonus, String> {
    @Query("select u from SellingBonus u where u.exportingWarehouse.id = ?1")
    List<SellingBonus> getByExportingWarehouseId(String id);

    @Query(value="select round(sum(t.amount), 2) from selling_bonus t where t.id = ?1 ", nativeQuery=true)
    Double getTotal(String id);

    @Query("select r from SellingBonus r where r.collaborator.id = ?1 or r.collaboratorRef.id = ?1")
    List<SellingBonus> getByCollaboratorRef(String collaboratorRefId);

    @Query("select r from SellingBonus r where r.employee.id = ?1 or r.employeeRef.id = ?1")
    List<SellingBonus> getByEmployeeRef(String employeeRefId);

    @Query("select r from SellingBonus r where (r.collaborator.id = ?1 or r.collaboratorRef.id = ?1) and r.agency.id = ?2")
    Page<SellingBonus> getByCollaboratorRefPaging(Pageable var1, String collaboratorRefId, String agencyId);

    @Query("select r from SellingBonus r where r.employee.id = ?1 or r.employeeRef.id = ?1")
    Page<SellingBonus> getByEmployeeRefPaging(Pageable var1, String employeeRefId);

    @Query("select u from SellingBonus u where ((u.collaborator.id in :idList) or (u.collaboratorRef.id in :idList)) and u.paymentStatus <> 'COMPLETED'")
    List<SellingBonus> getByCollaboratorsUnpaid(@Param("idList") List<String> idList);

    @Query("select u from SellingBonus u where ((u.employee.id in :idList) or (u.employeeRef.id in :idList)) and u.paymentStatus <> 'COMPLETED'")
    List<SellingBonus> getByEmployeesUnpaid(@Param("idList") List<String> idList);

    @Query("select r from SellingBonus r where (r.collaboratorRef.id = ?1 or r.collaborator.id = ?1) and r.paymentStatus <> 'COMPLETED'")
    List<SellingBonus> getBonusNotCompleteByCollaboratorRef(String collaboratorRefId);

    @Query("select r from SellingBonus r where (r.employeeRef.id = ?1 or r.employee.id = ?1) and r.paymentStatus <> 'COMPLETED'")
    List<SellingBonus> getBonusNotCompleteByEmployeeRef(String employeeRefId);

    @Query(value = "select * from selling_bonus where amount = %?1%", nativeQuery = true)
    List<SellingBonus> getByAmount(Float amount);
    
    @Query(value="select round(sum(t.amount), 2) from selling_bonus t where t.created_date >= ?1 and t.created_date <= ?2 and (t.employee_ref_id = ?3 or t.employee_id = ?3)", nativeQuery=true)
    Double getTotalByRangeDateAndEmployeeRefId(String fromDate, String toDate, String employeeRefId);

    @Query(value="select round(sum(t.amount), 2) from selling_bonus t where t.created_date >= ?1 and t.created_date <= ?2 and (t.collaborator_ref_id = ?3 or t.collaborator_id = ?3)", nativeQuery=true)
    Double getTotalByRangeDateAndCollaboratorRefId(String fromDate, String toDate, String collaboratorRefId);
}
