package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExportingWarehouseFullDto {
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
    private CustomerModel customer;
    private String customerAddress;
    private String customerTaxCode;
    private CustomerModel transactionCustomer;
    private String description;
    private String note;
    private String foreignCurrency;
    private String foreignCurrencyRate;
    private PaymentStatusEnum paymentStatus;
    private Date createdDate;

    private Double total;
    private Double receiptTotal;
    private Double importingReturnTotal;
    private List<ExportingTransactionFullDto> exportTransactionFulls;

    public Double getTotalByExportingTransactions() {
        if (this.exportTransactionFulls == null) {
            return 0D;
        }
        Double total = 0D;
        for (ExportingTransactionFullDto e : exportTransactionFulls) {
            double amount = e.getAmount() == null ? 0 : e.getAmount();
            total += amount;
        }
        return total;
    }
}
