package com.antdigital.agency.dtos.response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class DeliveryDto {
    private String id;
    @NotEmpty(message = "Diễn giải không được trống")
    public String description;
    private ExportingWarehouseDto exportWarehouse;
    private Date createdDate;
}
