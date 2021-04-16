package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employees employee;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;
    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column
    private String code;
    @Column
    private String number;
    @Column
    private String description;
    @Column
    private String note;
    @Column
    private Float amount;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
