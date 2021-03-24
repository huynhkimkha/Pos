package com.antdigital.agency.dtos.response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class DebtClearingDto {
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
    @NotEmpty(message = "Diễn giải không được trống")
    private String description;
    private String note;
    @NotNull(message = "Ngày không được trống")
    private Date createdDate;
}
