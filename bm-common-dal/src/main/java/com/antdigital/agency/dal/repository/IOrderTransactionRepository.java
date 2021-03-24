package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderTransactionRepository extends JpaRepository<OrderTransaction, String> {
    @Query("select t from OrderTransaction t where t.order.id = ?1")
    List<OrderTransaction> getByOrderId(String orderId);

    @Query("select sum(t.quantity) from OrderTransaction t where t.order.id = ?1")
    Float getTotalQuantityByOrderId(String orderId);

    @Query("select count(t) from OrderTransaction  t where t.merchandiseId like %?1%")
    Integer countMerchandise(String id);
}
