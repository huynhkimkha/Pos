package com.antdigital.agency.dal.data;

import lombok.Data;

import java.util.Date;

@Data
public class DebtReport {
    private String fullCode;
    private String code;
    private Date createdDate;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceNumber;
    private String customerId;
    private String customerDebtId;
    private String description;
    private String contraAccount;
    private String contraAccountId;
    private Double debitAmount;
    private Double creditAmount;
    private Double debitBalance;
    private Double creditBalance;
    private String merchandiseId;
    private Float quantity;
    private Double unitPrice;
    private String detailDescription;
    private String unit;
}
