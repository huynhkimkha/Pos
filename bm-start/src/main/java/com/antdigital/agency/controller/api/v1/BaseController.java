package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.configuration.security.jwt.UserPrinciple;
import com.antdigital.agency.dtos.response.EmployeesDto;
import com.antdigital.agency.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    @Autowired
    private IEmployeeService employeeService;

    protected EmployeesDto getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrincipal = (UserPrinciple) auth.getPrincipal();
        EmployeesDto employeesDto = employeeService.getEmployeeByEmail(userPrincipal.getUsername());

        return employeesDto;
    }

    private UserPrinciple getUserPrinciple() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) auth.getPrincipal();
    }

    protected String getAgencyId() {
        UserPrinciple userPrincipal = this.getUserPrinciple();
        return userPrincipal.getAgencyId();
    }
}
