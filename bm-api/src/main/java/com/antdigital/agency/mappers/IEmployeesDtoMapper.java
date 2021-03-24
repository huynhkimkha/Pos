package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Employees;
import com.antdigital.agency.dtos.response.EmployeeDetailDto;
import com.antdigital.agency.dtos.response.EmployeeFullDto;
import com.antdigital.agency.dtos.response.EmployeesDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IEmployeesDtoMapper {
    IEmployeesDtoMapper INSTANCE = Mappers.getMapper( IEmployeesDtoMapper.class );

    EmployeesDto toEmployeesDto(Employees employee);

    Employees toEmployees(EmployeeFullDto employeeFullDto);
    EmployeeFullDto toEmployeeFullDto(Employees employees);
    EmployeeDetailDto toEmployeeDetailDto(EmployeesDto employeesDto);
    List<EmployeesDto> toEmployeesDtoList(List<Employees> employeeList);
    List<EmployeeDetailDto> toEmployeeDetailDtos(List<EmployeesDto> employeesDtos);
}
