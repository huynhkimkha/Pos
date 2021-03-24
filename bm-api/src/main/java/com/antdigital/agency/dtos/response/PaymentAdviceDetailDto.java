package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class PaymentAdviceDetailDto {
    private String id;
    private PaymentAdviceDto paymentAdvice;
    private String description;
    private Double amount;
    private ImportingWarehouseDto importingWarehouse;
    private String creditAccount;
    private String debitAccount;
    private SellingBonusDto sellingBonus;
    private ReferralBonusDto referralBonus;
}
