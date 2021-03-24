package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "grant_permissions")
public class GrantPermission {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "permission_code")
    private Permission permission;
}
