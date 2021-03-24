package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ReferralBonus;
import com.antdigital.agency.dtos.response.ReferralBonusDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IReferralBonusDtoMapper {
    IReferralBonusDtoMapper INSTANCE = Mappers.getMapper(IReferralBonusDtoMapper.class);
    ReferralBonusDto toReferralBonusDto(ReferralBonus referralBonus);
    ReferralBonus toReferralBonus(ReferralBonusDto referralBonusDto);
    List<ReferralBonusDto> toReferralBonusDtos(List<ReferralBonus> referralBonuses);
    List<ReferralBonus> toReferralBonuses(List<ReferralBonusDto> referralBonusDtos);
}
