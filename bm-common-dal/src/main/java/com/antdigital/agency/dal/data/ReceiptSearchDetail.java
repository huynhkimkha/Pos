package com.antdigital.agency.dal.data;

import lombok.Data;

import java.util.Date;

@Data
public class ReceiptSearchDetail {
    private String id;
    private String code;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private String customerAddress;
    private String customerTaxCode;
    private String customerId;
    private String transactionCustomerId;
    private String description;
    private String note;
    private Date createdDate;
    private String customerCode;
    private String customerName;
    private Double total;
}
