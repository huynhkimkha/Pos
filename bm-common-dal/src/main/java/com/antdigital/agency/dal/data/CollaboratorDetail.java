package com.antdigital.agency.dal.data;

import com.antdigital.agency.dal.entity.Collaborator;
import com.antdigital.agency.dal.entity.ReferralBonus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CollaboratorDetail extends Collaborator {
    private ReferralBonus referee;
    private Double unpaidBonus;
}
