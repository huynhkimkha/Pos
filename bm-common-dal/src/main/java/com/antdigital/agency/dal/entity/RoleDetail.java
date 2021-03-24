package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "roles_detail")
public class RoleDetail {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employees employee;
}
