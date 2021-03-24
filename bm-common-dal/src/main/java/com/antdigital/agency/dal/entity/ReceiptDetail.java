package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "receipt_detail")
public class ReceiptDetail {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name="exporting_warehouse_id")
    private ExportingWarehouse exportingWarehouse;
    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;
    @Column
    private String description;
    @Column
    private Double amount;
    @Column(name="credit_account")
    private String creditAccount;
    @Column(name="debit_account")
    private String debitAccount;
}
