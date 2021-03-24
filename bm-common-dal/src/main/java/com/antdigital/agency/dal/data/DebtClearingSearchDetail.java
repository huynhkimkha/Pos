package com.antdigital.agency.dal.data;

import com.antdigital.agency.dal.entity.ExportingWarehouse;
import lombok.Data;

import java.util.Date;

@Data
public class DebtClearingSearchDetail {
    private String id;
    private String code;
    private String number;
    ExportingWarehouse exportingWarehouse;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private String customerAddress;
    private String customerTaxCode;
    private String customerId;
    private String customerDebtId;
    private String transactionCustomerId;
    private String description;
    private String note;
    private Date createdDate;
    private String customerCode;
    private String customerName;
    private String customerDebtCode;
    private String customerDebtName;
    private Double total;
}
