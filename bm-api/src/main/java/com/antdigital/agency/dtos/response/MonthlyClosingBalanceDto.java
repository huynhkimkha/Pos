package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import lombok.Data;

import java.util.Date;

@Data
public class MonthlyClosingBalanceDto {
    private String id;
    private AgencyDto agency;
    private String customerId;
    private AccountingTableModel accountingTable;
    private Double debitBalance;
    private Double creditBalance;
    private Date closingDate;
}
