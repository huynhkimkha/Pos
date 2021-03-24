package com.antdigital.agency.dal.data;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dal.entity.Order;
import lombok.Data;

import java.util.Date;

@Data
public class ExportingWarehouseDetail {
    private String id;
    private PaymentStatusEnum paymentStatus;
    private String code;
    private String number;
    private Date invoiceDate;
    Order order;
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
    private Double purchaseTotal;
}
