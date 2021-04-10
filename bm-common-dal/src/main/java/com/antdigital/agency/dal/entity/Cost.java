package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.TypeCostEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "cost")
public class Cost {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Enumerated(EnumType.STRING)
    @Column(name="type_cost")
    private TypeCostEnum typeCost;
    @Column
    private String amount;
    @Column
    private String description;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
