package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "exporting_return_transaction")
public class ExportingReturnTransaction {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "exporting_return_id")
    private ExportingReturn exportingReturn;
    @ManyToOne
    @JoinColumn(name="import_id")
    private ImportingWarehouse importingWarehouse;
    @Column(name = "merchandise_id")
    private String merchandiseId;
    @Column
    private Float quantity;
    @Column(name = "conversion_quantity")
    private Float conversionQuantity;
    @Column
    private Double price;
    @Column(name ="amount")
    private Double amount;
    @Column(name = "conversion_price")
    private Double conversionPrice;
    @Column(name = "credit_account")
    private String creditAccount;
    @Column(name = "debit_account")
    private String debitAccount;
}
