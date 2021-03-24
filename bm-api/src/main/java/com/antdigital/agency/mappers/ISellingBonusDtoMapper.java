package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.SellingBonus;
import com.antdigital.agency.dtos.response.SellingBonusDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ISellingBonusDtoMapper {
    ISellingBonusDtoMapper INSTANCE = Mappers.getMapper( ISellingBonusDtoMapper.class );

    SellingBonusDto toSellingBonusDto(SellingBonus sellingBonus);

    SellingBonus toSellingBonus(SellingBonusDto sellingBonusDto);

    SellingBonus newSellingBonus(SellingBonus sellingBonus);

    List<SellingBonusDto> toSellingBonusDtos(List<SellingBonus> sellingBonuses);

    List<SellingBonus> toSellingBonuses(List<SellingBonusDto> sellingBonusDtos);
}
