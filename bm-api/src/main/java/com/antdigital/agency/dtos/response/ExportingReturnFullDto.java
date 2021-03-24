package com.antdigital.agency.dtos.response;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.dal.entity.Agency;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExportingReturnFullDto {
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
    private CustomerModel customer;
    private String customerAddress;
    private String customerTaxCode;
    private CustomerModel transactionCustomer;
    private String description;
    private String note;
    private String foreignCurrency;
    private String foreignCurrencyRate;
    private Date createdDate;
    private Double receiptTotal;

    private List<ExportingReturnTransactionFullDto> exportReturnTransactionFulls;
}
