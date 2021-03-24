package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

import java.util.Date;

@Data
public class MerchandiseModel {
    private String id;
    private String companyId;
    private String code;
    private String name;
    private UnitModel unit;
    private UnitModel conversionUnit;
    private Float conversionRate;
    private Float kgUnit;
    private String type;
    private Float discountPercent;
    private Boolean warehouseManagement;
    private Float discountPaymentPercent;
    private Double tax;
    private TaxTableModel taxTable;
    private Double purchasePrice;
    private Double conversionPurchasePrice;
    private Double price1;
    private Double price2;
    private Double price3;
    private Double price4;
    private Double price5;
    private Double price6;
    private Double price7;
    private Double price8;
    private Double price9;
    private Double price10;
    private AccountingTableModel warehouseDebitAccount;
    private AccountingTableModel manufacturingDebitAccount;
    private AccountingTableModel debitAccountSold;
    private AccountingTableModel creditAccountRevenue;
    private AccountingTableModel creditAccountReturn;
    private MerchandiseGroupModel group1;
    private MerchandiseGroupModel group2;
    private CustomerModel group3;
    private ProductGroupModel productGroup;
    private String description;
    private Date createdDate;
    private Date updatedDate;
}
