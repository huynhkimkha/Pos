package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Size;
import com.antdigital.agency.dtos.response.SizeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ISizeDtoMapper {
    ISizeDtoMapper INSTANCE = Mappers.getMapper(ISizeDtoMapper.class);

    SizeDto toSizeDto(Size size);

    Size toSize(SizeDto sizeFullDto);

    List<SizeDto> toSizeDtoList(List<Size> sizeList);
}
