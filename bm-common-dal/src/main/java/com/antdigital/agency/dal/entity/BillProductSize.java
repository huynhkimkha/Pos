package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "bill_product_size")
public class BillProductSize {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "bill_id")
    private Bill bill;
    @ManyToOne
    @JoinColumn(name = "product_size_id")
    private ProductSize productSize;
    @Column
    private Float price;
    @Column
    private Float quantity;
}
