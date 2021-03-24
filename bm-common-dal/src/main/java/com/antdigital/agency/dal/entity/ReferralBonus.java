package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.PaymentStatusEnum;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "referral_bonus")
public class ReferralBonus {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "activated_status")
    private ActivatedStatusEnum activatedStatus;
    @Column(name = "amount")
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatusEnum paymentStatus;
    @Column(name = "created_date")
    private Date createdDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }
}
