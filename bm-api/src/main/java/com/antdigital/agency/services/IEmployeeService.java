package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.EmployeesDto;

import java.util.List;

public interface IEmployeeService {
    BaseSearchDto<List<EmployeesDto>> findAll(BaseSearchDto<List<EmployeesDto>> searchDto, String agencyId);
    List<EmployeesDto> findAll(String agencyId);
    EmployeesDto getEmployeeByEmail(String email);
    EmployeesDto getEmployeeById(String id);
    EmployeesDto update(EmployeesDto employeeFullDto);
    EmployeesDto insert(EmployeesDto employeeFullDto);
    boolean deleteEmployee(String id);
}
