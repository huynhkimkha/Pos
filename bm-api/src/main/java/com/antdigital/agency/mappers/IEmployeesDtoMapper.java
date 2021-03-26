package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Employees;
import com.antdigital.agency.dtos.response.EmployeesDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IEmployeesDtoMapper {
    IEmployeesDtoMapper INSTANCE = Mappers.getMapper( IEmployeesDtoMapper.class );

    EmployeesDto toEmployeesDto(Employees employee);

    Employees toEmployees(EmployeesDto employeeFullDto);

    List<EmployeesDto> toEmployeesDtoList(List<Employees> employeeList);
}
