package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class ExportingWarehouseDto {
    private String id;
    private AgencyDto agency;
    private OrderDto order;
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
    private PaymentStatusEnum paymentStatus;
    private Date createdDate;
}
