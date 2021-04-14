package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Promotion;
import com.antdigital.agency.dtos.response.PromotionDto;
import com.antdigital.agency.dtos.response.PromotionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPromotionDtoMapper {
    IPromotionDtoMapper INSTANCE = Mappers.getMapper(IPromotionDtoMapper.class);

    List<PromotionDto> toPromotionDtoList(List<Promotion> promotionList);

    Promotion toPromotion(PromotionFullDto promotionFullDto);

    PromotionFullDto toPromotionFullDto(Promotion promotion);
}
