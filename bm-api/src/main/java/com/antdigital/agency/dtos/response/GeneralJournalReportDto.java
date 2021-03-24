package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class GeneralJournalReportDto {
    private String id;
    private String fullCode;
    private String code;
    private Date createdDate;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceNumber;
    private String customerId;
    private String customerCode;
    private String customerName;
    private String merchandiseId;
    private String description;
    private String contraAccountId;
    private String contraAccount;
    private Double debitAmount;
    private Double creditAmount;
    private String detailDescription;
}
