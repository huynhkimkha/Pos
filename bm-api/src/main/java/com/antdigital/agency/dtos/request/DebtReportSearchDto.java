package com.antdigital.agency.dtos.request;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class DebtReportSearchDto {
    @NotNull(message = "Vui lòng ngày bắt đầu")
    private Date fromDate;

    @NotNull(message = "Vui lòng nhập ngày kết thúc")
    private Date toDate;

    private AccountingTableModel debtAccount;
    private String customerGroup1Id;
    private String customerGroup2Id;
    private String customerGroup3Id;
    private CustomerModel customer;
    private String agencyId;
    private String companyId;

    public DebtReportSearchDto() {

    }

    public DebtReportSearchDto(DebtReportSearchDto debtReportSearchDto){
        this.fromDate = debtReportSearchDto.getFromDate();
        this.toDate = debtReportSearchDto.getToDate();
        this.debtAccount = debtReportSearchDto.getDebtAccount();
        this.customer = debtReportSearchDto.getCustomer();
    }
}
