package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Customers;
import com.antdigital.agency.dal.repository.ICustomersRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CustomersDto;
import com.antdigital.agency.mappers.ICustomersDtoMapper;
import com.antdigital.agency.services.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service

public class CustomerServiceImpl implements ICustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private ICustomersRepository customersRepository;

    @Override
    public List<CustomersDto> findAll() {
        List<Customers> customers = customersRepository.findAll();
        return ICustomersDtoMapper.INSTANCE.toCustomersDtoList(customers);
    }

    @Override
    public BaseSearchDto<List<CustomersDto>> findAll(BaseSearchDto<List<CustomersDto>> searchDto) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll());
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Customers> page = customersRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ICustomersDtoMapper.INSTANCE.toCustomersDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public CustomersDto getCustomerById(String id) {
        Customers customer = customersRepository.findById(id).get();
        return ICustomersDtoMapper.INSTANCE.toCustomersDto(customer);
    }

    @Override
    public CustomersDto getCustomerByPhone(String phone) {
        Customers customer = customersRepository.getCustomerByPhone(phone);
        return ICustomersDtoMapper.INSTANCE.toCustomersDto(customer);
    }

    @Transactional
    public CustomersDto insert(CustomersDto customerDto) {
        try {
            Customers customer = ICustomersDtoMapper.INSTANCE.toCustomers(customerDto);
            customer.setId(UUIDHelper.generateType4UUID().toString());
            Customers createdCustomer = customersRepository.save(customer);
            customerDto.setId(createdCustomer.getId());
            return customerDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public CustomersDto update(CustomersDto customerDto) {
        try {
            Customers customer = ICustomersDtoMapper.INSTANCE.toCustomers(customerDto);
            customersRepository.save(customer);
            return customerDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteCustomer(String id) {
        try {
            customersRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
