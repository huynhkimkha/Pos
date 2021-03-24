package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.enums.PermissionRequirementEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    private String code;
    @Column
    private String name;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;
    @Enumerated(EnumType.STRING)
    @Column(name="requirement")
    private PermissionRequirementEnum requirement;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
