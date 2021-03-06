package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "employees")
public class Employees {
    @Id
    private String id;
    @Column(name="full_name")
    private String fullName;
    @Column
    private String email;
    @Column
    private String password;
    @Column(name="birth_date")
    private Date birthDate;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
