package com.antdigital.agency.dtos.response;
import lombok.Data;

@Data
public class DebtClearingDetailDto extends DebtClearingDto {
    private String id;
    private String code;
    private String number;
    private String total;
    private DebtClearingDto debtClearing;
    private String description;
    private Double amount;
    private ExportingWarehouseDto exportingWarehouse;
    private String customerId;
    private String customerCode;
    private String customerName;
    private String customerDebtId;
    private String customerDebtCode;
    private String customerDebtName;
    private String creditAccount;
    private String debitAccount;
}
