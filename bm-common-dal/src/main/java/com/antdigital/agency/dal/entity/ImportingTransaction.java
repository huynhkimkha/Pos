package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "importing_transaction")
public class ImportingTransaction {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "importing_material_id")
    private ImportingMaterial importingMaterial;
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;
    @Column
    private Float quantity;
    @Column
    private Float price;
    @Column
    private Float amount;
}
