package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.Collaborator;
import com.antdigital.agency.dal.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface IOrderRepository extends JpaRepository<Order, String> {
    @Query(value= "select * from orders u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) " +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from orders u where MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1) and u.agency_id = ?2)", nativeQuery=true)
    Order getOrderNumber(Date createdDate, String agencyId);

    @Query(value= "select * from orders u where u.code = ?1 and u.number = ?2 and YEAR(created_date)=?3 " +
            "and u.agency_id = ?4", nativeQuery=true)
    Order getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query(value= "select * from orders o where o.agency_id = ?2 and " +
            "o.import_status <> 'COMPLETED' and concat(o.code, '.', YEAR(o.created_date), '.', o.number) like %?1%", nativeQuery=true)
    List<Order> getNotCompleted(String code, String agencyId);

    @Query(countQuery = "select t from Order t where t.customerId = ?1")
    int countByCustomerId(String id);

    @Query(value= "select count(u.customer_id) from orders u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query("select u from Order u where u.agency.id = ?1")
    List<Order> findAllOrders(String agencyId);
}
