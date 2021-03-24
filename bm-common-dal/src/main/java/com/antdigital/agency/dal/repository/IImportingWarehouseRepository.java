package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ImportingWarehouse;
import com.antdigital.agency.dal.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IImportingWarehouseRepository extends JpaRepository<ImportingWarehouse, String>  {
    @Query(value= "select * from importing_warehouse u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from importing_warehouse u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) and u.agency_id = ?2)", nativeQuery=true)
    ImportingWarehouse getImportNumber(Date createdDate, String agencyId);

    @Query(value= "select * from importing_warehouse u where u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3 and u.agency_id = ?4"
            , nativeQuery=true)
    ImportingWarehouse getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query(value= "select * from importing_warehouse u where (u.customer_id = ?1 or u.transaction_customer_id = ?1) " +
            "and u.payment_status <> 'COMPLETED' and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?2%", nativeQuery=true)
    List<ImportingWarehouse> getNotCompleted(String customerId, String code);

    @Query(value= "select * from importing_warehouse u where u.agency_id = ?2 and concat(u.code, '.', YEAR(u.created_date), '.', u.number) like %?1%", nativeQuery=true)
    List<ImportingWarehouse> getLikeCode(String code, String agencyId);

    @Query(value= "select * from importing_warehouse u where u.agency_id = ?2 and u.order_id = ?1 "
            , nativeQuery=true)
    List<ImportingWarehouse> getByOrderId(String orderId, String agencyId);

    @Query(countQuery="select count(*) from importing_warehouse t where t.order_id = ?1 ", nativeQuery=true)
    float countByOrderId(String orderId);

    @Query(value = "select u.id from importing_warehouse u where u.agency_id = ?2 and u.payment_status <> 'COMPLETED' and u.transaction_customer_id = ?1", nativeQuery = true)
    List<String> getIdListByCustomer(String customerId, String agencyId);

    @Query(value= "select count(u.customer_id) from importing_warehouse u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query(value = "select u.* from importing_warehouse u where u.payment_status <> 'COMPLETED' and u.transaction_customer_id = ?1 order by u.created_date asc, u.number asc", nativeQuery = true)
    List<ImportingWarehouse> getUncompletedByCustomer(String customerId);

    @Query("select u from ImportingWarehouse u where u.agency.id = ?1")
    List<ImportingWarehouse> findAllImportingWarehouses(String agencyId);
}
