package com.antdigital.agency.dal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "importing_transaction")
public class ImportingTransaction {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "import_id")
    private ImportingWarehouse importingWarehouse;
    @ManyToOne
    @JoinColumn(name="order_id")
    Order order;
    @Column(name = "merchandise_id")
    private String merchandiseId;
    @Column
    private Float quantity;
    @Column(name = "conversion_quantity")
    private Float conversionQuantity;
    @Column
    private Double price;
    @Column
    private Double amount;
    @Column(name = "conversion_price")
    private Double conversionPrice;
    @Column(name = "credit_account")
    private String creditAccount;
    @Column(name = "debit_account")
    private String debitAccount;
}
