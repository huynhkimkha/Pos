package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column
    private String code;
    @Column
    private String number;
    @Column(name="invoice_date")
    private Date invoiceDate;
    @Column(name="invoice_code")
    private String invoiceCode;
    @Column(name="invoice_template")
    private String invoiceTemplate;
    @Column(name="invoice_symbol")
    private String invoiceSymbol;
    @Column(name="invoice_number")
    private String invoiceNumber;
    @Column(name="customer_address")
    private String customerAddress;
    @Column(name="customer_tax_code")
    private String customerTaxCode;
    @Column(name="customer_id")
    private String customerId;
    @Column(name="transaction_customer_id")
    private String transactionCustomerId;
    @Column
    private String description;
    @Column
    private String note;
    @Column(name = "created_date")
    private Date createdDate;
}
