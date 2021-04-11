package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Product;
import com.antdigital.agency.dtos.response.ProductDto;
import com.antdigital.agency.dtos.response.ProductFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IProductDtoMapper {
    IProductDtoMapper INSTANCE = Mappers.getMapper(IProductDtoMapper.class);

    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

    List<ProductDto> toProductDtoList(List<Product> productList);

    Product toProduct(ProductFullDto productDto);

    ProductFullDto toProductFullDto(Product product);
}
