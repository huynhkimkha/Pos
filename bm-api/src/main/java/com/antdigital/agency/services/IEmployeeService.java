package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.EmployeeFullDto;
import com.antdigital.agency.dtos.response.EmployeeSearchDto;
import com.antdigital.agency.dtos.response.EmployeesDto;
import com.antdigital.agency.dtos.response.RangeDateDto;

import java.util.List;

public interface IEmployeeService {
    BaseSearchDto<List<EmployeesDto>> findAll(BaseSearchDto<List<EmployeesDto>> searchDto, String agencyId);
    List<EmployeesDto> findAll(String agencyId);
    EmployeeSearchDto findAllAffiliate(EmployeeSearchDto searchDto, String agencyId);
    EmployeesDto getEmployeeById(String id);
    EmployeesDto getEmployeeByEmail(String email, String agencyId);
    List<EmployeesDto> getLikeName(String employeeName, String agencyId);
    List<EmployeesDto> getEmployees(List<String> ids);
    EmployeeFullDto insert(EmployeeFullDto employeeFullDto);
    EmployeeFullDto update(EmployeeFullDto employeeFullDto);
    boolean deleteEmployee(String id);
    EmployeeFullDto getEmployeeFullById(String id);
    EmployeeFullDto getEmployeeFull(String email, String companyId);
    EmployeesDto changePassword(String userName, ChangePasswordDto changePasswordDto, String agencyId);
    Double getBonusByRangeDate(RangeDateDto rangeDateDto, String employeeId);
}
