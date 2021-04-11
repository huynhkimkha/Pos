package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Product;
import com.antdigital.agency.dal.entity.ProductCategory;
import com.antdigital.agency.dtos.response.ProductCategoryDto;
import com.antdigital.agency.dtos.response.ProductDto;
import com.antdigital.agency.dtos.response.ProductFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IProductCategoryDtoMapper {
    IProductCategoryDtoMapper INSTANCE = Mappers.getMapper(IProductCategoryDtoMapper.class);

    ProductCategory toProductCategory(ProductCategoryDto productCategoryDto);

    ProductCategoryDto toProductCategoryDto(ProductCategory productCategory);

    List<ProductCategoryDto> toProductCategoryDtoList(List<ProductCategory> productCategoryList);
}
