package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.configuration.security.jwt.UserPrinciple;
import com.antdigital.agency.dtos.response.CollaboratorDto;
import com.antdigital.agency.dtos.response.EmployeesDto;
import com.antdigital.agency.services.ICollaboratorService;
import com.antdigital.agency.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BaseController {
    @Autowired
    private IEmployeeService employeeService;

    @Autowired
    private ICollaboratorService collaboratorService;

    protected EmployeesDto getCurrentEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrincipal = (UserPrinciple) auth.getPrincipal();
        EmployeesDto employeesDto = employeeService.getEmployeeByEmail(userPrincipal.getUsername(), userPrincipal.getAgencyId());

        return employeesDto;
    }

    protected CollaboratorDto getCurrentCollaborator() {
        String companyId = getCompanyId();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrinciple userPrinciple = (UserPrinciple) auth.getPrincipal();
        CollaboratorDto collaboratorDto = collaboratorService.getByEmail(userPrinciple.getUsername(), companyId);

        return collaboratorDto;
    }

    protected String getAgencyId() {
        UserPrinciple userPrincipal = this.getUserPrinciple();
        return userPrincipal.getAgencyId();
    }

    protected String getCompanyId() {
        UserPrinciple userPrincipal = this.getUserPrinciple();
        return userPrincipal.getCompanyId();
    }

    private UserPrinciple getUserPrinciple() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (UserPrinciple) auth.getPrincipal();
    }
}
