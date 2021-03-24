package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.dal.entity.Agency;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class PaymentFullDto {
    private String id;
    private AgencyDto agency;
    @NotEmpty(message = "Mã chứng từ không được trống")
    private String code;
    @NotEmpty(message = "Số chứng từ không được trống")
    private String number;
    private Date invoiceDate;
    private String invoiceCode;
    private String invoiceTemplate;
    private String invoiceSymbol;
    private String invoiceNumber;
    private String customerAddress;
    private String customerTaxCode;
    private CustomerModel customer;
    private CustomerModel transactionCustomer;
    @NotEmpty(message = "Nội dung không được trống")
    private String description;
    private String note;
    @NotNull(message = "Ngày không được trống")
    private Date createdDate;
    private List<PaymentDetailFullDto> paymentDetails;
}
