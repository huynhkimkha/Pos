package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ProductSize;
import com.antdigital.agency.dtos.response.ProductSizeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IProductSizeDtoMapper {
    IProductSizeDtoMapper INSTANCE = Mappers.getMapper(IProductSizeDtoMapper.class);

    ProductSize toProductSize(ProductSizeDto productSizeDto);

    ProductSizeDto toProductSizeDto(ProductSize productSize);

    List<ProductSizeDto> toProductSizeDtoList(List<ProductSize> productSizeList);
}
