package com.antdigital.agency.dal.repository;

import com.antdigital.agency.dal.entity.MonthlyClosingBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IMonthlyClosingBalanceRepository extends JpaRepository<MonthlyClosingBalance, String> {
    @Query("select p from MonthlyClosingBalance p where p.agency.id = :pAgencyId and p.customerId = :pCustomerId and (MONTH(p.closingDate) = MONTH(:pFromDate) and YEAR(p.closingDate) = YEAR(:pFromDate))")
    MonthlyClosingBalance getByCustomerIdAndClosingDate(@Param("pCustomerId") String customerId, @Param("pFromDate") Date fromDate, @Param("pAgencyId") String agencyId);

    @Query("select p from MonthlyClosingBalance p where p.agency.id = :pAgencyId and p.closingDate = (select MAX(m.closingDate) from MonthlyClosingBalance m where p.customerId = :pCustomerId)")
    MonthlyClosingBalance getLatestClosingBalance(@Param("pCustomerId") String customerId, @Param("pAgencyId") String agencyId);

    @Query("select p from MonthlyClosingBalance p where p.agency.id = :pAgencyId and MONTH(p.closingDate) = MONTH(:pFromDate) and YEAR(p.closingDate) = YEAR(:pFromDate)")
    List<MonthlyClosingBalance> getByClosingDate(@Param("pFromDate") Date fromDate, @Param("pAgencyId") String agencyId);

    @Modifying
    @Query("delete from MonthlyClosingBalance p where p.agency.id = :pAgencyId and MONTH(p.closingDate) = MONTH(:pFromDate) and YEAR(p.closingDate) = YEAR(:pFromDate)")
    void deleteByClosingDate(@Param("pFromDate") Date fromDate, @Param("pAgencyId") String agencyId);
}
