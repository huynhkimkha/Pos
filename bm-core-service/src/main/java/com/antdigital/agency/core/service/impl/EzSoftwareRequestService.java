package com.antdigital.agency.core.service.impl;

import com.antdigital.agency.common.utils.EzSoftwareServiceRequest;
import com.antdigital.agency.core.models.warehouse.CompanyShortModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.models.warehouse.ResponseModel;
import com.antdigital.agency.core.service.IEzSoftwareRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class EzSoftwareRequestService implements IEzSoftwareRequestService {
    @Autowired
    private EzSoftwareServiceRequest ezSoftwareServiceRequest;

    @Override
    public boolean reachUserLimit(HttpServletRequest request, String companyId, String softwareId, int numberOfUser) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/validation/reach-user-limit/" + companyId + "/" + softwareId + "/" + numberOfUser;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if (response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(response.getResult(), boolean.class);
        }
        return false;
    }

    @Override
    public boolean reachBranchLimit(HttpServletRequest request, String companyId, String softwareId, int numberOfAgency) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/validation/reach-branch-limit/" + companyId + "/" + softwareId + "/" + numberOfAgency;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if (response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(response.getResult(), boolean.class);
        }
        return false;
    }

    @Override
    public List<String> getLicensePermissionCodes(HttpServletRequest request, String companyId, String softwareId) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/company-license/getLicensePermissionCode/" + companyId + "/" + softwareId;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String[] permissionCodes = objectMapper.convertValue(response.getResult(), String[].class);
            return Arrays.asList(permissionCodes);
        }

        return null;
    }

    @Override
    public CompanyShortModel getByNameSlug(HttpServletRequest request, String nameSlug) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/company/getByNameSlug/?nameSlug=" + nameSlug;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CompanyShortModel companyShortModel = objectMapper.convertValue(response.getResult(), CompanyShortModel.class);
            return companyShortModel;
        }

        return null;
    }

    @Override
    public List<CompanyShortModel> getLikeNameOrSlugName(HttpServletRequest request, String name) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/company/getLikeNameOrSlugName/?name=" + name;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CompanyShortModel[] companyShortModels = objectMapper.convertValue(response.getResult(), CompanyShortModel[].class);
            return Arrays.asList(companyShortModels);
        }

        return null;
    }

    @Override
    public Boolean validateLicense(HttpServletRequest request, String companyId, String softwareId) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/company-license/validateLicense/" + companyId + "/" + softwareId;
        response = ezSoftwareServiceRequest.get(uri, response.getClass(), request);
        if (response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(response.getResult(), boolean.class);
        }
        return false;
    }
}
