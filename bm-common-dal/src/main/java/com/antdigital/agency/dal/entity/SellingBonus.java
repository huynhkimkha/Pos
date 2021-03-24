package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "selling_bonus")
public class SellingBonus {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @ManyToOne
    @JoinColumn(name = "exporting_warehouse_id")
    private ExportingWarehouse exportingWarehouse;
    @ManyToOne
    @JoinColumn(name = "employee_ref_id")
    private Employees employeeRef;
    @ManyToOne
    @JoinColumn(name = "collaborator_ref_id")
    private Collaborator collaboratorRef;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employees employee;
    @ManyToOne
    @JoinColumn(name = "collaborator_id")
    private Collaborator collaborator;
    @Column
    private String description;
    @Column(name = "amount")
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusEnum paymentStatus;
    @Column(name = "created_date")
    private Date createdDate;
}
