package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.BillProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBillProductSizeRepository extends JpaRepository<BillProductSize, String> {
    @Query("select t from BillProductSize t where t.bill.id = ?1")
    List<BillProductSize> getByBillId(String billId);
}
