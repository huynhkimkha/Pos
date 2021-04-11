package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Categories;
import com.antdigital.agency.dal.entity.Customers;
import com.antdigital.agency.dtos.response.CategoriesDto;
import com.antdigital.agency.dtos.response.CustomersDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ICategoriesDtoMapper {
    ICategoriesDtoMapper INSTANCE = Mappers.getMapper(ICategoriesDtoMapper.class );

    List<CategoriesDto> toCategoríeDtoList(List<Categories> categoriesList);

    CategoriesDto toCategoriesDto(Categories categories);

    List<CustomersDto> toCustomersDtoList(List<Customers> customersList);

    Categories toCategory(CategoriesDto categoriesDto);
}
