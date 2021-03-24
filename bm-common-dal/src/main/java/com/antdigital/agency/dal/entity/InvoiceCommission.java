package com.antdigital.agency.dal.entity;
import com.antdigital.agency.common.enums.CommissionTypeEnum;
import com.antdigital.agency.common.enums.UserModelEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "invoice_commission")
public class InvoiceCommission {
    @Id
    private String id;
    @Column(name="company_id")
    private String companyId;
    @Column
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name="commission_type")
    private CommissionTypeEnum commissionType;
    @Enumerated(EnumType.STRING)
    @Column(name = "apply_object")
    private UserModelEnum applyObject;
    @Column(name = "min_revenue")
    private Double minRevenue;
    @Column(name = "bonus")
    private Double bonus;
    @Column(name = "created_date")
    private Date createdDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }
}
