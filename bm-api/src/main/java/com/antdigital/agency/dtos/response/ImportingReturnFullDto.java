package com.antdigital.agency.dtos.response;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.dal.entity.Agency;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class ImportingReturnFullDto {
    private String id;
    private AgencyDto agency;
    @NotEmpty(message = "Mã chứng từ không được trống")
    private String code;
    private ExportingWarehouseDto exportingWarehouse;
    @NotEmpty(message = "Số chứng từ không được trống")
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private CustomerModel customer;
    private CustomerModel transactionCustomer;
    @NotEmpty(message = "Địa chỉ không được trống")
    private String customerAddress;
    private String customerTaxCode;
    @NotEmpty(message = "Diễn giải không được trống")
    private String description;
    private String note;
    private String foreignCurrency;
    private String foreignCurrencyRate;
    @NotNull(message = "Ngày không được trống")
    private Date createdDate;

    private List<ImportingReturnTransactionFullDto> importReturnTransactionFulls;
}
