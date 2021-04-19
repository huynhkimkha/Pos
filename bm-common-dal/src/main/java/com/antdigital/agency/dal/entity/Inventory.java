package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
    @Column
    private String amount;
    @Column(name = "amount_check")
    private String amountCheck;
    @Column(name = "description")
    private String description;
}
