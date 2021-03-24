package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingWarehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IExportingWarehouseRepository extends JpaRepository<ExportingWarehouse, String> {
    @Query(value= "select * from exporting_warehouse u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from exporting_warehouse u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    ExportingWarehouse getExportNumber(Date createdDate, String agencyId);

    @Query(value= "select * from exporting_warehouse u where u.agency_id = ?4 and u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3"
            , nativeQuery=true)
    ExportingWarehouse getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query(value= "select * from exporting_warehouse u where u.agency_id = ?3 and (u.customer_id = ?1 or u.transaction_customer_id = ?1) " +
            "and u.payment_status <> 'COMPLETED' and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?2%", nativeQuery=true)
    List<ExportingWarehouse> getPaymentNotCompleted(String customerId, String code, String agencyId);

    @Query(value= "select * from exporting_warehouse u where u.agency_id = ?3 and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?1%", nativeQuery=true)
    List<ExportingWarehouse> getLikeCode(String code, String agencyId);

    @Query(value= "select * from exporting_warehouse u where u.agency_id = ?2 and u.payment_status <> 'COMPLETED' and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?1%", nativeQuery=true)
    List<ExportingWarehouse> getPaymentNotCompletedCode(String code, String agencyId);

    @Query(value= "select * from exporting_warehouse u where u.order_id = ?1 "
            , nativeQuery=true)
    List<ExportingWarehouse> getByOrderId(String orderId);

    @Query(countQuery="select count(*) from exporting_warehouse t where t.order_id = ?1 ", nativeQuery=true)
    int countByOrderId(String orderId);

    @Query(value = "select u.id from exporting_warehouse u where u.agency_id = ?2 and u.payment_status <> 'COMPLETED' and u.transaction_customer_id = ?1", nativeQuery = true)
    List<String> getIdListByCustomer(String customerId, String agencyId);

    @Query(value= "select count(u.customer_id) from exporting_warehouse u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query(value = "select * from exporting_warehouse u where u.agency_id = ?1", nativeQuery=true)
    List<ExportingWarehouse> findAllByAgencyId(String agencyId);

    @Query(value = "select * from exporting_warehouse u where u.agency_id = ?1", nativeQuery=true)
    Page<ExportingWarehouse> findAllByAgencyId(Pageable var1, String agencyId);

    @Query(value = "select u.* from exporting_warehouse u where u.agency_id = ?2 and u.payment_status <> 'COMPLETED' and u.transaction_customer_id = ?1 order by u.created_date asc, u.number asc", nativeQuery = true)
    List<ExportingWarehouse> getUncompletedByCustomer(String customerId, String agencyId);
}
