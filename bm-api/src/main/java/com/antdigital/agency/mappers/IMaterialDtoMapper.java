package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Material;
import com.antdigital.agency.dtos.response.MaterialDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IMaterialDtoMapper {
    IMaterialDtoMapper INSTANCE = Mappers.getMapper( IMaterialDtoMapper.class );

    MaterialDto toMaterialDto(Material Material);

    Material toMaterial(MaterialDto MaterialFullDto);

    List<MaterialDto> toMaterialDtoList(List<Material> MaterialList);
}
