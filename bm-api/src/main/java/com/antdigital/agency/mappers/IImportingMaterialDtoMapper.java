package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ImportingMaterial;
import com.antdigital.agency.dtos.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IImportingMaterialDtoMapper {
    IImportingMaterialDtoMapper INSTANCE = Mappers.getMapper(IImportingMaterialDtoMapper.class);

    ImportingMaterialDto toImportingMaterialDto(ImportingMaterial ImportingMaterial);

    ImportingMaterial toImportingMaterial(ImportingMaterialDto ImportingMaterialDto);

    List<ImportingMaterialDto> toImportingMaterialDtoList(List<ImportingMaterial> ImportingMaterialList);

    ImportingMaterial toImportingMaterial(ImportingMaterialFullDto ImportingMaterialDto);

    ImportingMaterialFullDto toImportingMaterialFullDto(ImportingMaterial ImportingMaterial);
}
