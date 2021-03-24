package com.antdigital.agency.dtos.response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Data
public class DeliveryFullDto {
    private String id;
    @NotEmpty(message = "Diễn giải không được trống")
    public String description;
    private ExportingWarehouseDto exportWarehouse;
    private Date createdDate;
    private List<DeliveryTransactionFullDto> deliveryTransactionFulls;
}
