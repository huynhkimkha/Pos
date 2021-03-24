package com.antdigital.agency.dal.entity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "importing_return_transaction")
public class ImportingReturnTransaction {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "importing_return_id")
    private ImportingReturn importingReturn;
    @ManyToOne
    @JoinColumn(name="exporting_warehouse_id")
    private ExportingWarehouse exportingWarehouse;
    @Column(name = "merchandise_id")
    private String merchandiseId;
    @Column
    private Float quantity;
    @Column(name = "conversion_quantity")
    private Float conversionQuantity;
    @Column
    private Double price;
    @Column(name = "amount")
    private Double amount;
    @Column(name = "conversion_price")
    private Double conversionPrice;
    @Column(name = "cost_of_goods_sold")
    private Double costOfGoodsSold;
    @Column(name = "credit_account")
    private String creditAccount;
    @Column(name = "debit_account")
    private String debitAccount;
    @Column(name = "credit_account_purchase")
    private String creditAccountPurchase;
    @Column(name = "debit_account_purchase")
    private String debitAccountPurchase;
}
