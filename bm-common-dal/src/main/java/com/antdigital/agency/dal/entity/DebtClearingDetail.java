package com.antdigital.agency.dal.entity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "debt_clearing_detail")
public class DebtClearingDetail {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "debt_clearing_id")
    private DebtClearing debtClearing;
    @Column
    private String description;
    @Column
    private Double amount;
    @ManyToOne
    @JoinColumn(name="exporting_warehouse_id")
    private ExportingWarehouse exportingWarehouse;
    @Column(name="customer_debt_id")
    private String customerDebtId;
    @Column(name="customer_id")
    private String customerId;
    @Column(name="credit_account")
    private String creditAccount;
    @Column(name="debit_account")
    private String debitAccount;
}
