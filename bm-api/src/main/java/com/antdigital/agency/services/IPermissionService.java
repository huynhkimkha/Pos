package com.antdigital.agency.services;

import com.antdigital.agency.dtos.response.PermissionDto;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;
public interface IPermissionService {
    List<PermissionDto> findAll();
    List<PermissionDto> getAllOnLicense(String companyId);
    List<PermissionDto> getAllByLicense(HttpServletRequest request, String companyId) throws IOException, JAXBException;
}
