package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "monthly_closing_balance")
public class MonthlyClosingBalance {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column(name="customer_id")
    private String customerId;
    @Column(name="debit_balance")
    private Double debitBalance;
    @Column(name="credit_balance")
    private Double creditBalance;
    @Column(name = "closing_date")
    private Date closingDate;
}
