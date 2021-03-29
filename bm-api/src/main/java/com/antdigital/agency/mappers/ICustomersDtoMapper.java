package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Customers;
import com.antdigital.agency.dtos.response.CustomersDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ICustomersDtoMapper {
    ICustomersDtoMapper INSTANCE = Mappers.getMapper(ICustomersDtoMapper.class );

    CustomersDto toCustomersDto(Customers customers);

    Customers toCustomers(CustomersDto customersFullDto);

    List<CustomersDto> toCustomersDtoList(List<Customers> customersList);
}
