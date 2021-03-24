package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "payment_advice_detail")
public class PaymentAdviceDetail {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "payment_advice_id")
    private PaymentAdvice paymentAdvice;
    @ManyToOne
    @JoinColumn(name="importing_warehouse_id")
    private ImportingWarehouse importingWarehouse;
    @Column
    private String description;
    @Column
    private Double amount;
    @Column(name="credit_account")
    private String creditAccount;
    @Column(name="debit_account")
    private String debitAccount;
    @ManyToOne
    @JoinColumn(name="selling_bonus_id")
    private SellingBonus sellingBonus;
    @ManyToOne
    @JoinColumn(name="referral_bonus_id")
    private ReferralBonus referralBonus;
}
