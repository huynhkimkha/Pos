package com.antdigital.agency.core.service;

import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.models.warehouse.request.CustomerSearchModel;
import com.antdigital.agency.core.models.warehouse.request.MerchandiseSearchModel;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IWarehouseRequestService {
    MerchandiseModel[] getMerchandises(HttpServletRequest request, List<String> ids)
            throws IOException, JAXBException;
    Boolean addMerchandiseQuantity(HttpServletRequest request, List<MerchandiseWarehouseModel> productWarehouseModels)
            throws IOException, JAXBException;
    CustomerModel getCustomerById(HttpServletRequest request, String customerId)
            throws IOException, JAXBException;
    List<CustomerModel> getAllCustomers(HttpServletRequest request)
            throws IOException, JAXBException;
    List<CustomerModel> getCustomers(HttpServletRequest request, List<String> ids)
            throws IOException, JAXBException;
    List<CustomerModel> getCustomersLikeCode(HttpServletRequest request, String code)
            throws IOException, JAXBException;
    List<CustomerModel> getCustomersByAccountingCode(HttpServletRequest request, String code)
            throws IOException, JAXBException;
    List<CustomerModel> advancedSearch(HttpServletRequest request, CustomerSearchModel customerSearchModel)
            throws IOException, JAXBException;
    List<MerchandiseModel> getMerchandisesLikeCode(HttpServletRequest request, String code)
            throws IOException, JAXBException;
    List<MerchandiseModel> advancedSearch(HttpServletRequest request, MerchandiseSearchModel merchandiseSearchModel)
            throws IOException, JAXBException;
    List<AccountingTableModel> getAccountingList(HttpServletRequest request, List<String> ids)
            throws IOException, JAXBException;
    List<AccountingTableModel> getAccountingListByCodes(HttpServletRequest request, List<String> codes)
            throws IOException, JAXBException;
    List<MerchandiseModel> updatePurchasePrice(HttpServletRequest request, List<MerchandiseModel> merchandiseModels)
            throws IOException, JAXBException;
    List<MerchandiseModel> updateSoldPrice(HttpServletRequest request, List<MerchandiseModel> merchandiseModels)
            throws IOException, JAXBException;
    AccountingTableModel getAccounting(HttpServletRequest request, String code)
            throws IOException, JAXBException;
    List<MerchandiseModel> getListMerchandises(HttpServletRequest request, List<String> ids)
            throws IOException, JAXBException;
    List<SaleRankModel> getSaleRanks(HttpServletRequest request, List<String> ids)
            throws IOException, JAXBException;
    List<CustomerModel> getCustomerListByCollaboratorId(HttpServletRequest request, String id)
            throws IOException, JAXBException;
}
