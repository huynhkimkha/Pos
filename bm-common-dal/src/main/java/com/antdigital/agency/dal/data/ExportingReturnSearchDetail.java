package com.antdigital.agency.dal.data;

import com.antdigital.agency.dal.entity.ImportingWarehouse;
import lombok.Data;

import java.util.Date;

@Data
public class ExportingReturnSearchDetail {
    private String id;
    private String code;
    private String number;
    private Date invoiceDate;
    ImportingWarehouse importingWarehouse;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private String customerId;
    private String transactionCustomerId;
    private String customerAddress;
    private String customerTaxCode;
    private String description;
    private String note;
    private String foreignCurrency;
    private String foreignCurrencyRate;
    private Date createdDate;
    private String customerCode;
    private String customerName;
    private Double total;
    private Float totalQuantity;
}
