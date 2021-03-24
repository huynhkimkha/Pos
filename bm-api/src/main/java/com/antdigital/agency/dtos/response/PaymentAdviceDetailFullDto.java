package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import lombok.Data;
@Data
public class PaymentAdviceDetailFullDto {
    private String id;
    private PaymentAdviceDto paymentAdvice;
    private String description;
    private Double amount;
    private ImportingWarehouseDto importingWarehouse;
    private AccountingTableModel creditAccount;
    private AccountingTableModel debitAccount;
    private SellingBonusDto sellingBonus;
    private ReferralBonusDto referralBonus;
}
