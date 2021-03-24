package com.antdigital.agency.dtos.response;

import java.util.Date;
import lombok.Data;

@Data
public class ExportingReturnDto {
    private String id;
    private AgencyDto agency;
    private ImportingWarehouseDto importingWarehouse;
    private String code;
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private String customerId;
    private String customerAddress;
    private String customerTaxCode;
    private String transactionCustomerId;
    private String description;
    private String note;
    private String foreignCurrency;
    private String foreignCurrencyRate;
    private Date createdDate;
}
