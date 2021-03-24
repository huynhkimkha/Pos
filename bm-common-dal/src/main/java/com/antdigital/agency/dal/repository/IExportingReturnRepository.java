package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.ExportingReturn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IExportingReturnRepository extends JpaRepository<ExportingReturn, String> {
    @Query(value= "select * from exporting_return u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1)" +
            "and cast(format(substring(u.number,4,4),0) as unsigned) >= ALL(select cast(format(substring(u.number,4,4),0) as unsigned) from exporting_return u where u.agency_id = ?2 and MONTH(u.created_date) = MONTH(?1) and YEAR(u.created_date) = YEAR(?1))", nativeQuery=true)
    ExportingReturn getExportNumber(Date createdDate, String agencyId);

    @Query(value= "select * from exporting_return u where u.code = ?1 and u.number = ?2 and YEAR(u.created_date)=?3 and u.agency_id = ?4"
            , nativeQuery=true)
    ExportingReturn getByCodeAndNumber(String code, String number, int year, String agencyId);

    @Query(value= "select count(u.customer_id) from exporting_return u where u.customer_id = ?1" , nativeQuery=true)
    int countCustomerId(String customerId);

    @Query(value= "select * from exporting_return u where u.agency_id = ?1" , nativeQuery=true)
    List<ExportingReturn> findAllByAgencyId(String agencyId);

    @Query(value= "select * from exporting_return u where u.agency_id = ?1" , nativeQuery=true)
    Page<ExportingReturn> findAllByAgencyId(Pageable var1, String agencyId);
}
