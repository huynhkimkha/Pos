package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.EmployeesDto;

import java.util.List;

public interface IEmployeeService {
    BaseSearchDto<List<EmployeesDto>> findAll(BaseSearchDto<List<EmployeesDto>> searchDto);
    List<EmployeesDto> findAll();
    EmployeesDto getEmployeeByEmail(String email);
    EmployeesDto getEmployeeById(String id);
}
