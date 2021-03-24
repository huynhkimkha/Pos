package com.antdigital.agency.core.service;

import com.antdigital.agency.core.models.warehouse.CompanyShortModel;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IEzSoftwareRequestService {
    boolean reachUserLimit(HttpServletRequest request, String companyId, String softwareId, int numberOfUser) throws IOException, JAXBException;
    boolean reachBranchLimit(HttpServletRequest request, String companyId, String softwareId, int numberOfAgency) throws IOException, JAXBException;
    List<String> getLicensePermissionCodes(HttpServletRequest request, String companyId, String softwareId) throws IOException, JAXBException;
    CompanyShortModel getByNameSlug(HttpServletRequest request, String nameSlug) throws IOException, JAXBException;
    List<CompanyShortModel> getLikeNameOrSlugName(HttpServletRequest request, String name) throws IOException, JAXBException;
    Boolean validateLicense(HttpServletRequest request, String companyId, String softwareId) throws IOException, JAXBException;
}
