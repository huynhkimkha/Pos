package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.ProductDto;
import com.antdigital.agency.dtos.response.ProductFullDto;

import java.util.List;

public interface IProductService {
    List<ProductDto> findAll();
    List<ProductFullDto> findAllFull();
    List<ProductFullDto> findAllFullByCateId(String cateId);
    BaseSearchDto<List<ProductDto>> findAll(BaseSearchDto<List<ProductDto>> searchDto);
    ProductDto getById(String id);
    ProductDto getBySlug(String nameSlug);
    ProductDto getByName(String name);
    List<ProductDto> getLikeSlugOrName(String name);
    List<ProductDto> getProducts(List<String> ids);
    ProductFullDto insert(ProductFullDto productDto);
    ProductFullDto update(ProductFullDto productDto);
    boolean delete(String id);
    List<ProductDto> getLikeSlug(String nameSlug);
    ProductFullDto getProductFull(String productId);
}
