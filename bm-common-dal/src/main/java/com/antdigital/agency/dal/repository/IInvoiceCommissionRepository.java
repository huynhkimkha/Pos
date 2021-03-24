package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.InvoiceCommission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IInvoiceCommissionRepository extends JpaRepository<InvoiceCommission, String>{
    @Query(value= "select * from invoice_commission u where u.min_revenue = ?1 and u.company_id = ?2", nativeQuery = true)
    InvoiceCommission findByMinRevenue(Double min_revenue, String companyId);

    @Query(value= "select * from invoice_commission u where u.name like %?1% and u.company_id = ?2 limit 1", nativeQuery = true)
    InvoiceCommission findByName(String name, String companyId);

    @Query(value = "select employee_bonus from invoice_commission", nativeQuery = true)
    List<Float> getEmployeeBonus();

    @Query(value = "select collaborator_bonus from invoice_commission", nativeQuery = true)
    List<Float> getCollaboratorBonus();

    @Query(value = "select * from invoice_commission u where u.min_revenue <= ?1 order by u.min_revenue desc limit 0,1", nativeQuery = true)
    InvoiceCommission getCommissionByMinRevenue(Double min_revenue);

    @Query(value = "select * from invoice_commission u where (u.apply_object like %?1%) and (u.min_revenue <= ?2) and (u.company_id = ?3) order by u.min_revenue desc limit 0,1", nativeQuery = true)
    InvoiceCommission getByObjectAndRevenue(String applyObject, Double minRevenue, String companyId);

    @Query(value = "select * from invoice_commission u where (u.apply_object like %?1%) and (u.min_revenue = ?2) and (u.company_id = ?3) order by u.min_revenue desc limit 0,1", nativeQuery = true)
    InvoiceCommission getExactByObjectAndRevenue(String applyObject, Double minRevenue, String companyId);

    @Query("select t from InvoiceCommission t where t.companyId = ?1")
    List<InvoiceCommission> findAll(String companyId);

    @Query("select t from InvoiceCommission t where t.companyId = ?1")
    Page<InvoiceCommission> findAll(Pageable var1, String companyId);
}
