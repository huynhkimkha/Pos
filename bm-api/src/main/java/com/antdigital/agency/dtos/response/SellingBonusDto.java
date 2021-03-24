package com.antdigital.agency.dtos.response;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class SellingBonusDto {
    private String id;
    private AgencyDto agency;
    private ExportingWarehouseDto exportingWarehouse;
    private EmployeesDto employeeRef;
    private CollaboratorDto collaboratorRef;
    private EmployeesDto employee;
    private CollaboratorDto collaborator;
    private String description;
    private Double amount;
    private PaymentStatusEnum paymentStatus;
    private Date createdDate;
}
