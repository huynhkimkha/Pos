package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "order_transaction")
public class OrderTransaction {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "merchandise_id")
    private String merchandiseId;
    @Column
    private Float quantity;
    @Column(name = "conversion_quanity")
    private Float conversionQuantity;
    @Column
    private Double price;
    @Column
    private Double amount;
    @Column(name = "conversion_price")
    private Double conversionPrice;
}
