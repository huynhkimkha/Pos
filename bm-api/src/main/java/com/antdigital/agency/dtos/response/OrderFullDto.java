package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class OrderFullDto {
    private String id;
    private AgencyDto agency;
    private CustomerModel customer;
    @NotEmpty(message = "Mã chứng từ không được trống")
    private String code;
    @NotEmpty(message = "Số chứng từ không được trống")
    private String number;
    private ImportStatusEnum importStatus;
    private DeliveryStatusEnum deliverStatus;
    @NotEmpty(message = "Nội dung không được trống")
    private String title;
    @NotNull(message = "Ngày không được trống")
    private Date createdDate;
    private List<OrderTransactionFullDto> orderTransactions;
    private List<ImportingTransactionDto> importingTransactions;
    private List<ExportingTransactionDto> exportingTransactions;
}
