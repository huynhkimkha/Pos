package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.PromotionProduct;
import com.antdigital.agency.dtos.response.PromotionProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPromotionProductDtoMapper {
    IPromotionProductDtoMapper INSTANCE = Mappers.getMapper(IPromotionProductDtoMapper.class);

    PromotionProduct toPromotionProduct(PromotionProductDto promotionProductDto);

    PromotionProductDto toPromotionProductDto(PromotionProduct promotionProduct);

    List<PromotionProductDto> toPromotionProductDtoList(List<PromotionProduct> promotionProductList);
}
