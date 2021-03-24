package com.antdigital.agency.dal.data;

import lombok.Data;

import java.util.Date;

@Data
public class GeneralJournalReport {
    private String id;
    private String fullCode;
    private String code;
    private Date createdDate;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceNumber;
    private String customerCode;
    private String customerName;
    private String description;
    private String contraAccount;
    private Double debitAmount;
    private Double creditAmount;
    private String detailDescription;
}
