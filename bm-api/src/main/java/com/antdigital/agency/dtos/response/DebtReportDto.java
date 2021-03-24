package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class DebtReportDto {
    private String id;
    private String fullCode;
    private String code;
    private Date createdDate;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceNumber;
    private String customerId;
    private String customerDebtId;
    private String customerCode;
    private String customerName;
    private String description;
    private String contraAccount;
    private String contraAccountId;
    private String debitAccountId;
    private String creditAccountId;
    private Double debitAmount;
    private Double creditAmount;
    private Double debitBalance;
    private Double creditBalance;
    private String merchandiseId;
    private String merchandiseCode;
    private Float quantity;
    private Double unitPrice;
    private String detailDescription;
    private String unit;
    private String defaultAccountId;

    public DebtReportDto() {

    }

    public DebtReportDto(DebtReportDto debtReportDto) {
        this.id = debtReportDto.id;
        this.fullCode = debtReportDto.fullCode;
        this.code = debtReportDto.code;
        this.createdDate = debtReportDto.createdDate;
        this.number = debtReportDto.number;
        this.invoiceDate = debtReportDto.invoiceDate;
        this.invoiceCode = debtReportDto.invoiceCode;
        this.invoiceNumber = debtReportDto.invoiceNumber;
        this.customerId = debtReportDto.customerId;
        this.customerDebtId = debtReportDto.customerDebtId;
        this.customerCode = debtReportDto.customerCode;
        this.customerName = debtReportDto.customerName;
        this.description = debtReportDto.description;
        this.contraAccount = debtReportDto.contraAccount;
        this.contraAccountId = debtReportDto.contraAccountId;
        this.debitAccountId = debtReportDto.debitAccountId;
        this.creditAccountId = debtReportDto.creditAccountId;
        this.debitAmount = debtReportDto.debitAmount;
        this.creditAmount = debtReportDto.creditAmount;
        this.debitBalance = debtReportDto.debitBalance;
        this.creditBalance = debtReportDto.creditBalance;
        this.merchandiseId = debtReportDto.merchandiseId;
        this.merchandiseCode = debtReportDto.merchandiseCode;
        this.quantity = debtReportDto.quantity;
        this.unitPrice = debtReportDto.unitPrice;
        this.detailDescription = debtReportDto.detailDescription;
        this.unit = debtReportDto.unit;
        this.defaultAccountId = debtReportDto.defaultAccountId;
    }
}
