package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CustomersDto;
import com.antdigital.agency.dtos.response.MaterialDto;

import java.util.List;

public interface ICustomerService {
    BaseSearchDto<List<CustomersDto>> findAll(BaseSearchDto<List<CustomersDto>> searchDto);
    List<CustomersDto> findAll();
    CustomersDto getCustomerById(String id);
    CustomersDto getCustomerByPhone(String phone);
    CustomersDto update(CustomersDto customersDto);
    CustomersDto insert(CustomersDto customersDto);
    boolean deleteCustomer(String id);
    List<CustomersDto> getLikeName(String name);
}
