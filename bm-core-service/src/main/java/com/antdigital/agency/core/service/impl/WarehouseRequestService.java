package com.antdigital.agency.core.service.impl;

import com.antdigital.agency.common.utils.WarehouseServiceRequest;
import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.models.warehouse.request.CustomerSearchModel;
import com.antdigital.agency.core.models.warehouse.request.MerchandiseSearchModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class WarehouseRequestService implements IWarehouseRequestService {
    @Autowired
    private WarehouseServiceRequest warehouseServiceRequest;

    @Override
    public MerchandiseModel[] getMerchandises(HttpServletRequest request, List<String> ids)
        throws IOException, JAXBException {
    ResponseModel response = new ResponseModel();
    String uri = "/api/v1/merchandise/list";
    response = warehouseServiceRequest.post(uri, ids, response.getClass(), request);
    if(response.getResult() != null) {
        ObjectMapper objectMapper = new ObjectMapper();
        MerchandiseModel[] merchandiseModels = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
        return merchandiseModels;
    }

    return null;
    }

    @Override
    public Boolean addMerchandiseQuantity(HttpServletRequest request, List<MerchandiseWarehouseModel> merchandiseWarehouseModels)
            throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise-warehouse/add/quantity";
        response = warehouseServiceRequest.post(uri, merchandiseWarehouseModels, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Boolean result = objectMapper.convertValue(response.getResult(), Boolean.class);
            return result;
        }

        return false;
    }

    @Override
    public CustomerModel getCustomerById(HttpServletRequest request, String customerId)
            throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/" + customerId;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel customerModel = objectMapper.convertValue(response.getResult(), CustomerModel.class);
            return customerModel;
        }

        return null;
    }

    @Override
    public List<CustomerModel> getAllCustomers(HttpServletRequest request) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/findAll";
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<CustomerModel> getCustomerListByCollaboratorId(HttpServletRequest request, String id) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/getCustomerListByCollaboratorId/?collaboratorId=" + id;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<CustomerModel> getCustomers(HttpServletRequest request, List<String> ids) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/list";
        response = warehouseServiceRequest.post(uri, ids, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<MerchandiseModel> getListMerchandises(HttpServletRequest request, List<String> ids) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise/list";
        response = warehouseServiceRequest.post(uri, ids, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MerchandiseModel[] merchandiseModels = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
            return Arrays.asList(merchandiseModels);
        }

        return null;
    }

    @Override
    public List<CustomerModel> getCustomersLikeCode(HttpServletRequest request, String code) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/like-code/?code="+code;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<CustomerModel> getCustomersByAccountingCode(HttpServletRequest request, String code) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/accounting-code/?accountingCode=" + code;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<CustomerModel> advancedSearch(HttpServletRequest request, CustomerSearchModel customerSearchModel) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/customer/advanced-search";
        response = warehouseServiceRequest.post(uri, customerSearchModel, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerModel[] customerModels = objectMapper.convertValue(response.getResult(), CustomerModel[].class);
            return Arrays.asList(customerModels);
        }

        return null;
    }

    @Override
    public List<MerchandiseModel> getMerchandisesLikeCode(HttpServletRequest request, String code) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise/like-code?code="+code;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MerchandiseModel[] merchandiseModels = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
            return Arrays.asList(merchandiseModels);
        }

        return null;
    }

    @Override
    public List<MerchandiseModel> advancedSearch(HttpServletRequest request, MerchandiseSearchModel merchandiseSearchModel) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise/advanced-search";
        response = warehouseServiceRequest.post(uri, merchandiseSearchModel, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MerchandiseModel[] merchandiseModels = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
            return Arrays.asList(merchandiseModels);
        }

        return null;
    }

    @Override
    public List<AccountingTableModel> getAccountingList(HttpServletRequest request, List<String> ids) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/accounting-table/list";
        response = warehouseServiceRequest.post(uri, ids, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            AccountingTableModel[] accountingTableModels = objectMapper.convertValue(response.getResult(), AccountingTableModel[].class);
            return Arrays.asList(accountingTableModels);
        }
        return null;
    }

    @Override
    public List<AccountingTableModel> getAccountingListByCodes(HttpServletRequest request, List<String> codes) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/accounting-table/getByCodes";
        response = warehouseServiceRequest.post(uri, codes, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            AccountingTableModel[] accountingTableModels = objectMapper.convertValue(response.getResult(), AccountingTableModel[].class);
            return Arrays.asList(accountingTableModels);
        }
        return null;
    }

    @Override
    public AccountingTableModel getAccounting(HttpServletRequest request, String code)
            throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/accounting-table/" + code;
        response = warehouseServiceRequest.get(uri, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            AccountingTableModel accountingTableModel = objectMapper.convertValue(response.getResult(), AccountingTableModel.class);
            return accountingTableModel;
        }
        return null;
    }

    @Override
    public List<MerchandiseModel> updatePurchasePrice(HttpServletRequest request, List<MerchandiseModel> merchandiseModels)
            throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise/update/purchase-price";
        response = warehouseServiceRequest.post(uri, merchandiseModels, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MerchandiseModel[] merchandiseList = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
            return Arrays.asList(merchandiseList);
        }
        return null;
    }

    @Override
    public List<MerchandiseModel> updateSoldPrice(HttpServletRequest request, List<MerchandiseModel> merchandiseModels)
            throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/merchandise/update/sold-price";
        response = warehouseServiceRequest.post(uri, merchandiseModels, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            MerchandiseModel[] merchandiseList = objectMapper.convertValue(response.getResult(), MerchandiseModel[].class);
            return Arrays.asList(merchandiseList);
        }
        return null;
    }

    @Override
    public List<SaleRankModel> getSaleRanks(HttpServletRequest request, List<String> ids) throws IOException, JAXBException {
        ResponseModel response = new ResponseModel();
        String uri = "/api/v1/sale-rank/list";
        response = warehouseServiceRequest.post(uri, ids, response.getClass(), request);
        if(response.getResult() != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            SaleRankModel[] saleRankModels = objectMapper.convertValue(response.getResult(), SaleRankModel[].class);
            return Arrays.asList(saleRankModels);
        }

        return null;
    }
}
