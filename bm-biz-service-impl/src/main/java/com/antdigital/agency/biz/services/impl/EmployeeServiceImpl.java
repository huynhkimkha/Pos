package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IEmployeeService;
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
public class EmployeeServiceImpl implements IEmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private IEmployeesRepository employeesRepository;

    @Override
    @Transactional
    public List<EmployeesDto> findAll() {
        List<Employees> employees = employeesRepository.findAll();
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(employees);
    }

    @Override
    @Transactional
    public EmployeesDto getEmployeeByEmail(String email) {
        Employees employee = employeesRepository.getEmployeeByEmail(email);
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDto(employee);
    }

    @Override
    public BaseSearchDto<List<EmployeesDto>> findAll(BaseSearchDto<List<EmployeesDto>> searchDto) {
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

        Page<Employees> page = employeesRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(page.getContent()));

        return searchDto;
    }


    @Override
    public EmployeesDto getEmployeeById(String id) {
        try {
            Employees employee = employeesRepository.findById(id).get();
            EmployeesDto employeeDto = IEmployeesDtoMapper.INSTANCE.toEmployeesDto(employee);
            return employeeDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }
}
