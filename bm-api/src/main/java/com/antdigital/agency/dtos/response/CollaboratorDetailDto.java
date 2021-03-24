package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class CollaboratorDetailDto extends CollaboratorDto{
    private ReferralBonusDto referee;
    private Double unpaidBonus;
}
