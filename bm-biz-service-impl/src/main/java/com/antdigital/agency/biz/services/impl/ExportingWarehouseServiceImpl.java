package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.*;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.models.warehouse.request.CustomerSearchModel;
import com.antdigital.agency.core.models.warehouse.request.MerchandiseSearchModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IExportingWarehouseDao;
import com.antdigital.agency.dal.data.*;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.mappers.IExportingTransactionDtoMapper;
import com.antdigital.agency.mappers.IExportingWarehouseDtoMapper;
import com.antdigital.agency.mappers.IMonthRevenueDetailDtoMapper;
import com.antdigital.agency.mappers.ISellingBonusDtoMapper;
import com.antdigital.agency.services.IExportingWarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ExportingWarehouseServiceImpl implements IExportingWarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(ExportingWarehouseServiceImpl.class);

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderTransactionRepository orderTransactionRepository;

    @Autowired
    private ISettingsRepository settingsRepository;

    @Autowired
    private IReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private IReceiptAdviceDetailRepository receiptAdviceDetailRepository;

    @Autowired
    private IDebtClearingDetailRepository debtClearingDetailRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IExportingWarehouseDao exportingWarehouseDao;

    @Autowired
    private ICollaboratorRepository collaboratorRepository;

    @Autowired
    private IEmployeesRepository employeesRepository;

    @Autowired
    private IReferralBonusRepository referralBonusRepository;

    @Autowired
    private ISellingBonusRepository sellingBonusRepository;

    @Autowired
    private IInvoiceCommissionRepository invoiceCommissionRepository;


    @Override
    public ExportingWarehouseSearchDto search(HttpServletRequest request, ExportingWarehouseSearchDto exportingWarehouseSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if (exportingWarehouseSearchDto.getCustomerCode() != null && !exportingWarehouseSearchDto.getCustomerCode().isEmpty()) {
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, exportingWarehouseSearchDto.getCustomerCode());
            for (CustomerModel customerModel : customerModelList) {
                customerIds.add(customerModel.getId());
            }
        }
        //Get merchandise id from warehouse service
        List<String> merchandiseIds = new ArrayList<>();
        if (exportingWarehouseSearchDto.getMerchandiseCode() != null && !exportingWarehouseSearchDto.getMerchandiseCode().isEmpty()) {
            List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getMerchandisesLikeCode(request, exportingWarehouseSearchDto.getMerchandiseCode());
            for (MerchandiseModel merchandiseModel : merchandiseModelList) {
                merchandiseIds.add(merchandiseModel.getId());
            }
        }

        SearchResult<List<ExportingWarehouseDetail>> result = exportingWarehouseDao.search(
                exportingWarehouseSearchDto.getCode(),
                exportingWarehouseSearchDto.getNumber(),
                exportingWarehouseSearchDto.getCustomerAddress(),
                exportingWarehouseSearchDto.getDescription(),
                exportingWarehouseSearchDto.getNote(),
                customerIds,
                merchandiseIds,
                exportingWarehouseSearchDto.getPaymentStatus(),
                exportingWarehouseSearchDto.getStartDate(),
                exportingWarehouseSearchDto.getEndDate(),
                exportingWarehouseSearchDto.getCreatedDateSort(),
                exportingWarehouseSearchDto.getStartNumber(),
                exportingWarehouseSearchDto.getEndNumber(),
                exportingWarehouseSearchDto.getCurrentPage(),
                exportingWarehouseSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for (ExportingWarehouseDetail exportingWarehouseDetail : result.getResult()) {
            customerIds.add(exportingWarehouseDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for (ExportingWarehouseDetail exportingWarehouseDetail : result.getResult()) {
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(exportingWarehouseDetail.getCustomerId())).findFirst().orElse(null);
            if (customerModels != null) {
                exportingWarehouseDetail.setCustomerCode(customerModels.getCode());
                exportingWarehouseDetail.setCustomerName(customerModels.getFullName());
            }
        }

        exportingWarehouseSearchDto.setTotalRecords(result.getTotalRecords());
        exportingWarehouseSearchDto.setResult(IExportingWarehouseDtoMapper.INSTANCE.toExportWarehouseDetailDtoList(result.getResult()));
        return exportingWarehouseSearchDto;
    }

    @Override
    public List<ExportingWarehouseDetailDto> revenueReport(HttpServletRequest request, LicenseSearchDto licenseSearchDto)
            throws IOException, JAXBException {
        //Get customer id from warehouse service

        List<String> customerIds = new ArrayList<>();
        List<CustomerModel> customerModelList = new ArrayList<>();
        // Case no customer information search
        if (licenseSearchDto.getCustomerCode() == null && licenseSearchDto.getCustomerGroup1Id() == null
                && licenseSearchDto.getCustomerGroup2Id() == null && licenseSearchDto.getCustomerGroup3Id() == null) {
            customerIds = null;
            customerModelList = null;
        }
        // Case has customer information search
        else {
            CustomerSearchModel customerSearchModel = new CustomerSearchModel();
            customerSearchModel.setCode(licenseSearchDto.getCustomerCode());
            customerSearchModel.setCompanyId(licenseSearchDto.getCompanyId());
            customerSearchModel.getCustomerGroup1().setId(licenseSearchDto.getCustomerGroup1Id());
            customerSearchModel.getCustomerGroup2().setId(licenseSearchDto.getCustomerGroup2Id());
            customerSearchModel.getCustomerGroup3().setId(licenseSearchDto.getCustomerGroup3Id());

            customerModelList = warehouseRequestService.advancedSearch(request, customerSearchModel);
            if (customerModelList.size() == 0) {
                return new ArrayList<>();
            }
            for (CustomerModel customerModel : customerModelList) {
                customerIds.add(customerModel.getId());
            }
        }

        //Get merchandise id from warehouse service

        // Case no merchandise information search
        List<String> merchandiseIds = new ArrayList<>();
        List<MerchandiseModel> merchandiseModelList = new ArrayList<>();
        if (licenseSearchDto.getMerchandiseCode() == null && licenseSearchDto.getMerchandiseGroup1Id() == null
                && licenseSearchDto.getMerchandiseGroup2Id() == null && licenseSearchDto.getMerchandiseGroup3Id() == null) {
            merchandiseIds = null;
            merchandiseModelList = null;
        }
        // Case has merchandise information search
        else {
            MerchandiseSearchModel merchandiseSearchModel = new MerchandiseSearchModel();
            merchandiseSearchModel.setCode(licenseSearchDto.getMerchandiseCode());
            merchandiseSearchModel.setCompanyId(licenseSearchDto.getCompanyId());
            merchandiseSearchModel.getMerchandiseGroup1().setId(licenseSearchDto.getMerchandiseGroup1Id());
            merchandiseSearchModel.getMerchandiseGroup2().setId(licenseSearchDto.getMerchandiseGroup2Id());
            merchandiseSearchModel.getMerchandiseGroup3().setId(licenseSearchDto.getMerchandiseGroup3Id());
            merchandiseSearchModel.getProductGroup().setId(licenseSearchDto.getProductGroupId());

            merchandiseModelList = warehouseRequestService.advancedSearch(request, merchandiseSearchModel);
            if (merchandiseModelList.size() == 0) {
                return new ArrayList<>();
            }
            for (MerchandiseModel merchandiseModel : merchandiseModelList) {
                merchandiseIds.add(merchandiseModel.getId());
            }
        }

        List<ExportingWarehouseDetail> result = exportingWarehouseDao.report(
                licenseSearchDto.getFromDate(),
                licenseSearchDto.getToDate(),
                licenseSearchDto.getCode(),
                licenseSearchDto.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                licenseSearchDto.getAgencyId()
        );

        // Case no customer information search -> get list customer
        if (customerIds == null) {
            customerIds = new ArrayList<>();
            customerModelList = new ArrayList<>();
            for (ExportingWarehouseDetail item : result) {
                int indexCustomerId = customerIds.indexOf(item.getCustomerId());

                if (indexCustomerId == -1) {
                    customerIds.add(item.getCustomerId());
                }
            }
            customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        }

        //set customer code and name
        for (ExportingWarehouseDetail exportingWarehouseDetail : result) {
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(exportingWarehouseDetail.getTransactionCustomerId())).findFirst().get();
            exportingWarehouseDetail.setCustomerCode(customerModels.getCode());
            exportingWarehouseDetail.setCustomerName(customerModels.getFullName());
        }
        return IExportingWarehouseDtoMapper.INSTANCE.toExportWarehouseDetailDtoList(result);
    }

    @Override
    public List<ExportingWarehouseDto> findAll(String agencyId) {
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.findAllByAgencyId(agencyId);
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDtoList(exportingWarehouses);
    }

    @Override
    public BaseSearchDto<List<ExportingWarehouseDto>> findAll(BaseSearchDto<List<ExportingWarehouseDto>> searchDto, String agencyId) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(agencyId));
            return searchDto;
        }

        Sort sort = null;
        if (searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<ExportingWarehouse> page = exportingWarehouseRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ExportingWarehouseDto getById(String id) {
        ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDto(exportingWarehouse);
    }

    @Override
    public ExportingWarehouseFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
        ExportingWarehouseFullDto exportingWarehouseFull = IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseFullDto(exportingWarehouse);
        List<ExportingTransaction> exportingTransactions = exportingTransactionRepository.getByExportingId(id);

        Double receiptTotal = receiptDetailRepository.getTotalByExportingWarehouseId(id);
        receiptTotal = receiptTotal != null ? receiptTotal : 0;

        Double debtClearingTotal = debtClearingDetailRepository.getTotalByExportingWarehouseId(id);
        debtClearingTotal = debtClearingTotal != null ? debtClearingTotal : 0;

        Double receiptAdviceTotal = receiptAdviceDetailRepository.getTotalByExportingWarehouseId(id);
        receiptAdviceTotal = receiptAdviceTotal != null ? receiptAdviceTotal : 0;

        receiptTotal += debtClearingTotal;
        receiptTotal += receiptAdviceTotal;

        Double importingReturnTotal = importingReturnTransactionRepository.getTotalByExportingWarehouseId(id);

        List<ExportingTransactionFullDto> exportingTransactionFullDtos = new ArrayList<>();
        for (ExportingTransaction exportingTransaction : exportingTransactions) {
            exportingTransactionFullDtos.add(IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionFullDto(exportingTransaction));
        }
        exportingWarehouseFull.setExportTransactionFulls(exportingTransactionFullDtos);
        exportingWarehouseFull.setReceiptTotal(receiptTotal);
        exportingWarehouseFull.setImportingReturnTotal(importingReturnTotal);

        CompletableFuture<Boolean> getCustomers = getCustomers(request, exportingWarehouseFull);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, exportingWarehouseFull);
        CompletableFuture<Boolean> getMerchandise = getMerchandise(request, exportingWarehouseFull);

        CompletableFuture.allOf(
                getCustomers,
                getAccounting,
                getMerchandise
        );

        return exportingWarehouseFull;
    }

    @Override
    public List<ExportingTransactionFullDto> GetByOrderId(HttpServletRequest request, String id) throws IOException, JAXBException {
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.getByOrderId(id);
        List<ExportingTransactionFullDto> exportingTransactionFullDtos = new ArrayList<>();
        for (ExportingWarehouse exportingWarehouse : exportingWarehouses) {
            ExportingWarehouseFullDto exportingWarehouseFullDto = getFullById(request, exportingWarehouse.getId());
            for (ExportingTransactionFullDto exportingTransactionFullDto : exportingWarehouseFullDto.getExportTransactionFulls()) {
                exportingTransactionFullDtos.add(exportingTransactionFullDto);
            }
        }
        List<ExportingTransactionFullDto> exportingTransactionFullDtoList = new ArrayList<>();
        for (ExportingTransactionFullDto exportingTransactionFullDto : exportingTransactionFullDtos) {
            if (exportingTransactionFullDtoList.size() == 0) {
                exportingTransactionFullDtoList.add(exportingTransactionFullDto);
                continue;
            }
            int i = 0;
            for (ExportingTransactionFullDto exportingTransactionFullDto1 : exportingTransactionFullDtoList) {
                if (exportingTransactionFullDto1.getMerchandise().getId().equals(exportingTransactionFullDto.getMerchandise().getId())) {
                    exportingTransactionFullDto1.setQuantity(exportingTransactionFullDto1.getQuantity() + exportingTransactionFullDto.getQuantity());
                    exportingTransactionFullDto1.setConversionQuantity(exportingTransactionFullDto1.getConversionQuantity() + exportingTransactionFullDto.getConversionQuantity());
                    i++;
                    break;
                }
            }
            if (i == 0) {
                exportingTransactionFullDtoList.add(exportingTransactionFullDto);
            }
        }

        return exportingTransactionFullDtoList;
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ExportingWarehouse result = exportingWarehouseRepository.getExportNumber(sdf.parse(createdDate), agencyId);
            if (result == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(createdDate));
                int month = cal.get(Calendar.MONTH) + 1;
                String monthStr = month > 9 ? String.valueOf(month) : "0" + month;
                return monthStr + "/0001";
            }
            String number = result.getNumber();
            return StringHelper.NumberOfCertificate(number);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public List<ExportingWarehouseDto> getLikeCode(String code, String agencyId) {
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.getLikeCode(code, agencyId);
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDtoList(exportingWarehouses);
    }

    @Override
    public List<ExportingWarehouseFullDto> getForReceipt(String customerId, Double amount, String agencyId) {
        List<ExportingWarehouse> uncompletedExporting = exportingWarehouseRepository.getUncompletedByCustomer(customerId, agencyId);
        List<ExportingWarehouseFullDto> uncompletedExportingDtos = IExportingWarehouseDtoMapper.INSTANCE.toExportWarehouseFullDtoList(uncompletedExporting);
        List<ExportingWarehouseFullDto> importingsForPayment = new ArrayList<>();
        Double exportingAmount = 0D;
        for (ExportingWarehouseFullDto exporting : uncompletedExportingDtos) {

            exporting.setTotal(exportingTransactionRepository.getTotal(exporting.getId()));
            /* Receipt and return amount */
            Double receiptTotal = receiptDetailRepository.getTotalByExportingWarehouseId(exporting.getId());
            receiptTotal = receiptTotal != null ? receiptTotal : 0;

            Double debtClearingTotal = debtClearingDetailRepository.getTotalByExportingWarehouseId(exporting.getId());
            debtClearingTotal = debtClearingTotal != null ? debtClearingTotal : 0;

            Double paymentAdviceTotal = receiptAdviceDetailRepository.getTotalByExportingWarehouseId(exporting.getId());
            paymentAdviceTotal = paymentAdviceTotal != null ? paymentAdviceTotal : 0;

            exporting.setReceiptTotal(receiptTotal + paymentAdviceTotal + debtClearingTotal);

            Double importingReturnTotal = importingReturnTransactionRepository.getTotalByExportingWarehouseId(exporting.getId());
            importingReturnTotal = importingReturnTotal != null ? importingReturnTotal : 0;

            exporting.setImportingReturnTotal(importingReturnTotal);

            double paymentAmount = exporting.getTotal() - exporting.getReceiptTotal() - exporting.getImportingReturnTotal();
            if (paymentAmount == 0) {
                continue;
            }
            exportingAmount += paymentAmount;

            importingsForPayment.add(exporting);
            if (exportingAmount >= amount) {
                break;
            }
        }
        return importingsForPayment;
    }

    @Override
    public List<ExportingTransactionDto> getExportingTransactionForReturn(String customerId, String merchandiseId, Float quantity, String agencyId) {
        Float remainingQuantity = quantity;
        List<ExportingTransactionDto> exportingTransactionForReturn = new ArrayList<>();
        List<ExportingTransaction> exportingTransactions = exportingTransactionRepository.getByCustomerIdAndMerchandiseId(customerId, merchandiseId, agencyId);
        List<ExportingTransactionDto> exportingTransactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionDtoList(exportingTransactions);
        List<String> exportingIds = new ArrayList<>();
        for (ExportingTransactionDto i : exportingTransactionDtos) {
            if (i.getExportingWarehouse() != null) {
                int index = exportingIds.indexOf(i.getExportingWarehouse().getId());
                if (index == -1) {
                    exportingIds.add(i.getExportingWarehouse().getId());
                }
            }
        }
        List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getByExportingWarehouseIdList(exportingIds);
        List<ImportingReturnTransactionDto> importingReturnTransactionDtos = IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransactionDtoList(importingReturnTransactions);
        exportingTransactionDtos.sort(Comparator.comparing(i -> i.getExportingWarehouse().getCreatedDate()));
        exportingTransactionDtos.sort(Comparator.comparing(i -> i.getExportingWarehouse().getNumber()));
        exportingTransactionDtos.sort(Comparator.comparing(i -> i.getExportingWarehouse().getPaymentStatus()));
        for (ExportingTransactionDto i : exportingTransactionDtos) {
            List<ImportingReturnTransactionDto> importingReturnTransactionDtoList = importingReturnTransactionDtos.stream().filter(item -> item.getMerchandiseId().equals(i.getMerchandiseId())).collect(Collectors.toList());
            if (importingReturnTransactionDtoList.size() > 0) {
                for (ImportingReturnTransactionDto e : importingReturnTransactionDtoList) {
                    i.setQuantity(i.getQuantity() - e.getQuantity());
                }
            }
            if (i.getQuantity() <= 0) {
                continue;
            }
            if (remainingQuantity <= i.getQuantity()) {
                exportingTransactionForReturn.add(i);
                break;
            } else {
                remainingQuantity -= i.getQuantity();
                exportingTransactionForReturn.add(i);
            }
        }
        return exportingTransactionForReturn;
    }

    @Override
    public List<CustomerStatisticDto> getCustomerBaseOnSpent(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = format1.format(rangeDateDto.getFromDate());
        String toDate = format1.format(rangeDateDto.getToDate());
        List<CustomerStatistic> customerStatistics = exportingWarehouseDao.getCustomerBaseOnSpent(fromDate, toDate, agencyId);
        List<CustomerStatisticDto> customers = new ArrayList<>();
        List<String> customerIds = new ArrayList<>();
        for (CustomerStatistic customerStatistic : customerStatistics) {
            customerIds.add(customerStatistic.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for (CustomerStatistic customerStatistic : customerStatistics) {
            CustomerStatisticDto customer = new CustomerStatisticDto();
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(customerStatistic.getCustomerId())).findFirst().orElse(null);
            customer.setCustomer(customerModels);
            customer.setTotalSpent(customerStatistic.getTotalSpent());
            customers.add(customer);
        }
        return customers;
    }

    @Override
    public List<MerchandiseStatisticDto> getMerchandiseBestSold(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = format1.format(rangeDateDto.getFromDate());
        String toDate = format1.format(rangeDateDto.getToDate());
        List<MerchandiseStatistic> merchandiseStatistics = exportingWarehouseDao.getMerchandiseBestSold(fromDate, toDate, agencyId);
        List<MerchandiseStatisticDto> merchandises = new ArrayList<>();
        List<String> merchandiseIds = new ArrayList<>();
        for (MerchandiseStatistic merchandiseStatistic : merchandiseStatistics) {
            merchandiseIds.add(merchandiseStatistic.getMerchandiseId());
        }
        List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getListMerchandises(request, merchandiseIds);
        for (MerchandiseStatistic merchandiseStatistic : merchandiseStatistics) {
            MerchandiseStatisticDto merchandise = new MerchandiseStatisticDto();
            MerchandiseModel merchandiseModel = merchandiseModelList.stream().filter(item -> item.getId().equals(merchandiseStatistic.getMerchandiseId())).findFirst().orElse(null);
            merchandise.setMerchandise(merchandiseModel);
            merchandise.setTotalRevenue(merchandiseStatistic.getTotalRevenue());
            merchandises.add(merchandise);
        }
        return merchandises;
    }

    @Override
    public ExportingWarehouseDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDto(exportingWarehouse);
    }

    @Override
    public List<ExportingWarehouseDto> getPaymentNotCompleted(String customerId, String code, String agencyId) {
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.getPaymentNotCompleted(customerId, code, agencyId);
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDtoList(exportingWarehouses);
    }

    @Override
    public List<ExportingWarehouseDto> getPaymentNotCompletedCode(String code, String agencyId) {
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.getPaymentNotCompletedCode(code, agencyId);
        return IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseDtoList(exportingWarehouses);
    }

    @Override
    public Double getTotal(String id) {
        Double total = exportingTransactionRepository.getTotal(id);
        return total;
    }

    @Override
    public Double getDebt(String customerId, String agencyId) {
        List<String> exportingIdList = exportingWarehouseRepository.getIdListByCustomer(customerId, agencyId);
        if (exportingIdList == null || exportingIdList.size() == 0){
            return 0D;
        }
        Double total = exportingTransactionRepository.getTotalByExportingIdList(exportingIdList);

        Double receiptTotal = receiptDetailRepository.getTotalByExportingWarehouseIdList(exportingIdList);
        receiptTotal = receiptTotal != null ? receiptTotal : 0;

        Double receiptAdviceTotal = receiptAdviceDetailRepository.getTotalByExportingWarehouseIdList(exportingIdList);
        receiptAdviceTotal = receiptAdviceTotal != null ? receiptAdviceTotal : 0;

        return total - receiptTotal - receiptAdviceTotal;
    }

    @Override
    public int countByOrder(String orderId) {
        int count = exportingWarehouseRepository.countByOrderId(orderId);
        return count;
    }

    @Override
    @Transactional
    public ExportingWarehouseFullDto insert(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto, String companyId) {
        try {
            ExportingWarehouse exportingWarehouse = IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouse(exportingWarehouseFullDto);
            exportingWarehouse.setId(UUIDHelper.generateType4UUID().toString());
            if (exportingWarehouse.getOrder() == null || exportingWarehouse.getOrder().getId() == null
                    || exportingWarehouse.getOrder().getId().isEmpty()) {
                exportingWarehouse.setOrder(null);
            }

            ExportingWarehouse created = exportingWarehouseRepository.save(exportingWarehouse);
            exportingWarehouseFullDto.setId(created.getId());

            CompletableFuture<Boolean> saveExportingTransaction = saveExportingTransaction(exportingWarehouseFullDto);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, exportingWarehouseFullDto);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, exportingWarehouseFullDto, null);
            CompletableFuture<Boolean> saveSellingBonus = saveSellingBonus(exportingWarehouseFullDto, companyId);

            CompletableFuture.allOf(
                    saveExportingTransaction,
                    updatePurchasePrice,
                    addQuantityInWarehouse,
                    saveSellingBonus
            );

            updateDeliverStatusOrder(exportingWarehouseFullDto);

            return exportingWarehouseFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ExportingWarehouseFullDto update(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto, String companyId) {
        try {
            ExportingWarehouse exportingWarehouse = IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouse(exportingWarehouseFullDto);
            List<ExportingTransaction> exportingTransactions = exportingTransactionRepository.getByExportingId(exportingWarehouse.getId());
            List<ExportingTransactionDto> exportingTransactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionDtoList(exportingTransactions);
            if (exportingWarehouse.getOrder() == null || exportingWarehouse.getOrder().getId() == null
                    || exportingWarehouse.getOrder().getId().isEmpty()) {
                exportingWarehouse.setOrder(null);
            }

            exportingWarehouseRepository.save(exportingWarehouse);

            CompletableFuture<Boolean> saveExportingTransaction = saveExportingTransaction(exportingWarehouseFullDto);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, exportingWarehouseFullDto);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, exportingWarehouseFullDto, exportingTransactionDtos);
            CompletableFuture<Boolean> saveSellingBonus = saveSellingBonus(exportingWarehouseFullDto, companyId);
            CompletableFuture.allOf(
                    saveExportingTransaction,
                    updatePurchasePrice,
                    addQuantityInWarehouse,
                    saveSellingBonus
            );

            updateDeliverStatusOrder(exportingWarehouseFullDto);

            return exportingWarehouseFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(HttpServletRequest request, String id) {
        try {
            ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
            ExportingWarehouseFullDto exportingWarehouseFullDto = IExportingWarehouseDtoMapper.INSTANCE.toExportingWarehouseFullDto(exportingWarehouse);
            List<ExportingTransaction> exportingTransactions = exportingTransactionRepository.getByExportingId(id);
            List<ExportingTransactionDto> exportingTransactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionDtoList(exportingTransactions);
            for (ExportingTransaction item : exportingTransactions) {
                exportingTransactionRepository.deleteById(item.getId());
            }
            List<SellingBonus> sellingBonuses = sellingBonusRepository.getByExportingWarehouseId(id);
            if (sellingBonuses.size() > 0) {
                sellingBonusRepository.deleteAll(sellingBonuses);
            }
            exportingWarehouseRepository.deleteById(id);

            exportingWarehouseFullDto.setExportTransactionFulls(new ArrayList<>());
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, exportingWarehouseFullDto, exportingTransactionDtos);
            CompletableFuture.allOf(
                    addQuantityInWarehouse
            );

            updateDeliverStatusOrder(exportingWarehouseFullDto);

            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public List<MonthRevenueDetailDto> getMonthRevenue(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<MonthRevenueDetail> monthRevenueDetails = exportingWarehouseDao.getMonthRevenue(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromMonth = fromCal.get(Calendar.MONTH) + 1;
        Integer toMonth = toCal.get(Calendar.MONTH) + 1;
        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear = toCal.get(Calendar.YEAR);

        if (fromYear < toYear) {
            toMonth = 12;
        }
        List<MonthRevenueDetail> monthRevenueDetailList = new ArrayList<>();
        while (fromYear <= toYear) {
            if (fromYear - toYear == 0) {
                toMonth = toCal.get(Calendar.MONTH) + 1;
            }
            while (fromMonth <= toMonth) {
                MonthRevenueDetail monthRevenueDetail = new MonthRevenueDetail();
                monthRevenueDetail.setMonthDate(fromMonth);
                monthRevenueDetail.setYearDate(fromYear);
                monthRevenueDetail.setTotal(0);
                monthRevenueDetailList.add(monthRevenueDetail);
                fromMonth += 1;
            }
            fromMonth = 1;
            fromYear += 1;
        }
        for (MonthRevenueDetail monthRevenueDetail : monthRevenueDetails) {
            List<MonthRevenueDetail> detailLst = monthRevenueDetailList.stream().filter(item -> item.getMonthDate() == monthRevenueDetail.getMonthDate()).collect(Collectors.toList());
            for (MonthRevenueDetail temp : detailLst) {
                temp.setTotal(monthRevenueDetail.getTotal());
            }
        }
        List<MonthRevenueDetailDto> monthRevenueDtos = IMonthRevenueDetailDtoMapper.INSTANCE.toMonthRevenueDtoList(monthRevenueDetailList);

        return monthRevenueDtos;
    }

    @Override
    public List<DateRevenueDetailDto> getDateRevenue(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<DateRevenueDetail> dateRevenueDetails = exportingWarehouseDao.getDateRevenue(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromDate = fromCal.get(Calendar.DAY_OF_MONTH);
        Integer toDate = toCal.get(Calendar.DAY_OF_MONTH);
        Integer month = fromCal.get(Calendar.MONTH) + 1;
        Integer year = fromCal.get(Calendar.YEAR);

        List<DateRevenueDetail> dateRevenueDetailList = new ArrayList<>();
        while (fromDate <= toDate) {
            DateRevenueDetail dateRevenueDetail = new DateRevenueDetail();
            dateRevenueDetail.setDate(fromDate);
            dateRevenueDetail.setMonth(month);
            dateRevenueDetail.setYear(year);
            dateRevenueDetail.setTotal(0);
            dateRevenueDetailList.add(dateRevenueDetail);
            fromDate += 1;
        }

        for (DateRevenueDetail dateRevenueDetail : dateRevenueDetails) {
            List<DateRevenueDetail> detailLst = dateRevenueDetailList.stream().filter(item -> item.getDate() == dateRevenueDetail.getDate()).collect(Collectors.toList());
            for (DateRevenueDetail temp : detailLst) {
                temp.setTotal(dateRevenueDetail.getTotal());
            }
        }
        List<DateRevenueDetailDto> dateRevenueDetailDtos = IDateRevenueDetailDtoMapper.INSTANCE.toDateRevenueDtoList(dateRevenueDetailList);

        return dateRevenueDetailDtos;
    }

    @Override
    public List<YearRevenueDetailDto> getYearRevenue(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<YearRevenueDetail> yearRevenueDetails = exportingWarehouseDao.getYearRevenue(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear = toCal.get(Calendar.YEAR);

        List<YearRevenueDetail> yearRevenueDetailList = new ArrayList<>();
        while (fromYear <= toYear) {
            YearRevenueDetail yearRevenueDetail = new YearRevenueDetail();
            yearRevenueDetail.setYear(fromYear);
            yearRevenueDetail.setTotal(0);
            yearRevenueDetailList.add(yearRevenueDetail);
            fromYear += 1;
        }

        for (YearRevenueDetail yearRevenueDetail : yearRevenueDetails) {
            List<YearRevenueDetail> detailLst = yearRevenueDetailList.stream().filter(item -> item.getYear() == yearRevenueDetail.getYear()).collect(Collectors.toList());
            for (YearRevenueDetail temp : detailLst) {
                temp.setTotal(yearRevenueDetail.getTotal());
            }
        }
        List<YearRevenueDetailDto> yearRevenueDetailDtos = IYearRevenueDetailDtoMapper.INSTANCE.toYearRevenueDtoList(yearRevenueDetailList);

        return yearRevenueDetailDtos;
    }

    @Async
    CompletableFuture<Boolean> saveExportingTransaction(ExportingWarehouseFullDto exportingWarehouseFullDto) {
        ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(exportingWarehouseFullDto.getId()).get();
        if (exportingWarehouse.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            return CompletableFuture.completedFuture(false);
        }

        List<ExportingTransactionFullDto> exportingTransactionFulls = exportingWarehouseFullDto.getExportTransactionFulls();
        List<ExportingTransaction> oldTransactions = exportingTransactionRepository.getByExportingId(exportingWarehouse.getId());

        for (ExportingTransactionFullDto item : exportingTransactionFulls) {
            ExportingTransaction exportingTransaction = IExportingTransactionDtoMapper.INSTANCE.toExportingTransaction(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                exportingTransaction.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getOrder().getId() == null) {
                exportingTransaction.setOrder(null);
            }
            exportingTransaction.setExportingWarehouse(exportingWarehouse);
            ExportingTransaction created = exportingTransactionRepository.save(exportingTransaction);
            item.setId(created.getId());
        }

        if (oldTransactions != null) {
            for (ExportingTransaction item : oldTransactions) {
                int index = exportingTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    exportingTransactionRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> saveSellingBonus(ExportingWarehouseFullDto exportingWarehouseFullDto, String companyId) {
        ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(exportingWarehouseFullDto.getId()).get();

        Double exportingWarehouseTotal = exportingWarehouseFullDto.getTotalByExportingTransactions();
        InvoiceCommission employeeCommission = invoiceCommissionRepository.getByObjectAndRevenue(UserModelEnum.EMPLOYEE.toString(), exportingWarehouseTotal, companyId);
        InvoiceCommission collaboratorCommission = invoiceCommissionRepository.getByObjectAndRevenue(UserModelEnum.COLLABORATOR.toString(), exportingWarehouseTotal, companyId);

        Double bonus = 0D;
        Double refereeBonus = 100000D;
        if (employeeCommission != null || collaboratorCommission != null) {
            if (exportingWarehouseFullDto.getTransactionCustomer().getCollaboratorRefId() != null) {
                if(collaboratorCommission.getCommissionType() == CommissionTypeEnum.PERCENT){
                    bonus = collaboratorCommission.getBonus() * exportingWarehouseTotal / 100;
                } else {
                    bonus = collaboratorCommission.getBonus();
                }
            } else if (exportingWarehouseFullDto.getTransactionCustomer().getEmployeeRefId() != null) {
                if(employeeCommission.getCommissionType() == CommissionTypeEnum.PERCENT){
                    bonus = employeeCommission.getBonus() * exportingWarehouseTotal / 100;
                } else {
                    bonus = employeeCommission.getBonus();
                }
            } else {
                return CompletableFuture.completedFuture(false);
            }
        }

        List<SellingBonus> sellingBonuses = sellingBonusRepository.getByExportingWarehouseId(exportingWarehouseFullDto.getId());
        // delete ( or dont add ) selling bonus if not reach min revenue
        if (bonus == 0F) {
            sellingBonusRepository.deleteAll(sellingBonuses);
            return CompletableFuture.completedFuture(true);
        }
        //case update
        if (sellingBonuses.size() > 0) {
            for (SellingBonus sellingBonus : sellingBonuses) {
                sellingBonus.setAmount(bonus);
                if (sellingBonus.getCollaboratorRef() != null || sellingBonus.getEmployeeRef() != null) {
                    sellingBonus.setAmount(refereeBonus);
                }
            }
            sellingBonusRepository.saveAll(sellingBonuses);
            return CompletableFuture.completedFuture(true);
        }

        SellingBonus sellingBonus = new SellingBonus();
        Agency agency = new Agency();
        agency.setId(exportingWarehouseFullDto.getAgency().getId());
        ReferralBonus referee = new ReferralBonus();
        //get ref
        if (exportingWarehouseFullDto.getTransactionCustomer().getCollaboratorRefId() != null) {
            Collaborator collaborator = collaboratorRepository.findById(exportingWarehouseFullDto.getTransactionCustomer().getCollaboratorRefId()).get();
            sellingBonus.setCollaborator(collaborator);
            referee = referralBonusRepository.getByCollaboratorActivated(exportingWarehouseFullDto.getTransactionCustomer().getCollaboratorRefId());
        } else if (exportingWarehouseFullDto.getTransactionCustomer().getEmployeeRefId() != null) {
            Employees employee = employeesRepository.findById(exportingWarehouseFullDto.getTransactionCustomer().getEmployeeRefId()).get();
            sellingBonus.setEmployee(employee);
            referee = referralBonusRepository.getByEmployeeActivated(exportingWarehouseFullDto.getTransactionCustomer().getEmployeeRefId());
        } else {
            return CompletableFuture.completedFuture(false);
        }

        sellingBonus.setExportingWarehouse(exportingWarehouse);
        sellingBonus.setAmount(bonus);
        PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.UNCOMPLETED;
        sellingBonus.setDescription("Hoa hồng phát sinh - " + exportingWarehouse.getDescription());
        sellingBonus.setPaymentStatus(paymentStatusEnum);
        sellingBonus.setId(UUIDHelper.generateType4UUID().toString());
        sellingBonus.setAgency(agency);
        sellingBonus.setCreatedDate(exportingWarehouseFullDto.getCreatedDate());
        sellingBonuses.add(sellingBonus);

        //save referee's selling bonus
        SellingBonus refereeSellingBonus;
        if (referee != null) {
            refereeSellingBonus = ISellingBonusDtoMapper.INSTANCE.newSellingBonus(sellingBonus);
            refereeSellingBonus.setCollaboratorRef(referee.getCollaboratorRef());
            refereeSellingBonus.setEmployeeRef(referee.getEmployeeRef());
            refereeSellingBonus.setCollaborator(null);
            refereeSellingBonus.setEmployee(null);

            refereeBonus = referee.getCollaboratorRef() != null ? Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.COLLABORATOR_INDIRECT_BONUS.toString()).getValues())
                    : Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.EMPLOYEE_INDIRECT_BONUS.toString()).getValues());
            refereeSellingBonus.setAmount(refereeBonus);

            if (referee.getCollaborator() != null) {
                refereeSellingBonus.setDescription("Hoa hồng từ cộng tác viên " + referee.getCollaborator().getFullName());
            }
            if (referee.getEmployee() != null) {
                refereeSellingBonus.setDescription("Hoa hồng từ nhân viên " + referee.getEmployee().getFullName());
            }

            refereeSellingBonus.setId(UUIDHelper.generateType4UUID().toString());
            refereeSellingBonus.setAgency(agency);
        } else {
            refereeSellingBonus = null;
        }
        if (refereeSellingBonus != null) {
            sellingBonuses.add(refereeSellingBonus);
        }

        sellingBonusRepository.saveAll(sellingBonuses);
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(exportingWarehouseFullDto.getCustomer().getId());
        ids.add(exportingWarehouseFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer : customerModels) {
            if (exportingWarehouseFullDto.getCustomer() != null && exportingWarehouseFullDto.getCustomer().getId() != null) {
                if (exportingWarehouseFullDto.getCustomer().getId().equals(customer.getId())) {
                    exportingWarehouseFullDto.setCustomer(customer);
                }
            }

            if (exportingWarehouseFullDto.getTransactionCustomer() != null && exportingWarehouseFullDto.getTransactionCustomer().getId() != null) {
                if (exportingWarehouseFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    exportingWarehouseFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ExportingTransactionFullDto> transactionFulls = exportingWarehouseFullDto.getExportTransactionFulls();
        List<String> ids = new ArrayList<>();
        for (ExportingTransactionFullDto item : transactionFulls) {
            int indexCredit = ids.indexOf(item.getCreditAccount().getId());
            int indexDebit = ids.indexOf(item.getDebitAccount().getId());
            int indexCreditPurchase = ids.indexOf(item.getCreditAccountPurchase().getId());
            int indexDebitPurchase = ids.indexOf(item.getDebitAccountPurchase().getId());

            if (indexCredit == -1) {
                ids.add(item.getCreditAccount().getId());
            }

            if (indexDebit == -1) {
                ids.add(item.getDebitAccount().getId());
            }

            if (indexCreditPurchase == -1) {
                ids.add(item.getCreditAccountPurchase().getId());
            }

            if (indexDebitPurchase == -1) {
                ids.add(item.getDebitAccountPurchase().getId());
            }
        }

        List<AccountingTableModel> accountingTableModels = warehouseRequestService.getAccountingList(request, ids);
        for (AccountingTableModel account : accountingTableModels) {
            for (ExportingTransactionFullDto item : transactionFulls) {
                if (item.getCreditAccount().getId() != null && item.getCreditAccount().getId().equals(account.getId())) {
                    item.setCreditAccount(account);
                }

                if (item.getDebitAccount().getId() != null && item.getDebitAccount().getId().equals(account.getId())) {
                    item.setDebitAccount(account);
                }

                if (item.getCreditAccountPurchase().getId() != null && item.getCreditAccountPurchase().getId().equals(account.getId())) {
                    item.setCreditAccountPurchase(account);
                }

                if (item.getDebitAccountPurchase().getId() != null && item.getDebitAccountPurchase().getId().equals(account.getId())) {
                    item.setDebitAccountPurchase(account);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getMerchandise(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ExportingTransactionFullDto> transactionFulls = exportingWarehouseFullDto.getExportTransactionFulls();
        List<String> ids = new ArrayList<>();
        for (ExportingTransactionFullDto item : transactionFulls) {
            ids.add(item.getMerchandise().getId());
        }

        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, ids);
        for (MerchandiseModel merchandiseModel : merchandiseModels) {
            for (ExportingTransactionFullDto item : transactionFulls) {
                if (merchandiseModel.getId().equals(item.getMerchandise().getId())) {
                    item.setMerchandise(merchandiseModel);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> updatePurchasePrice(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ExportingTransactionFullDto> exportingTransactionFulls = exportingWarehouseFullDto.getExportTransactionFulls();
        List<MerchandiseModel> merchandiseModels = new ArrayList<>();
        Integer groupPrice = exportingWarehouseFullDto.getTransactionCustomer().getPriceGroup();
        if (groupPrice == null) {
            return CompletableFuture.completedFuture(true);
        }

        for (ExportingTransactionFullDto item : exportingTransactionFulls) {
            MerchandiseModel merchandiseModel = new MerchandiseModel();
            merchandiseModel.setId(item.getMerchandise().getId());
            setMerchandisePrice(merchandiseModel, groupPrice, item.getPrice());

            merchandiseModels.add(merchandiseModel);
        }

        warehouseRequestService.updateSoldPrice(request, merchandiseModels);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> addQuantityInWarehouse(HttpServletRequest request,
                                                      ExportingWarehouseFullDto exportingWarehouseFullDto,
                                                      List<ExportingTransactionDto> oldTransactions)
            throws IOException, JAXBException {

        // In case insert or update transactions
        List<MerchandiseWarehouseModel> merchandiseWarehouseModels = new ArrayList<>();
        List<ExportingTransactionFullDto> exportingTransactionFulls = exportingWarehouseFullDto.getExportTransactionFulls();
        for (ExportingTransactionFullDto item : exportingTransactionFulls) {
            float oldQuantity = 0;
            float oldConversionQuantity = 0;
            if (oldTransactions != null && oldTransactions.size() > 0) {
                int index = oldTransactions.stream().map(t -> t.getMerchandiseId()).collect(Collectors.toList()).indexOf(item.getMerchandise().getId());
                if (index != -1) {
                    oldQuantity = oldTransactions.get(index).getQuantity() == null ? 0 : oldTransactions.get(index).getQuantity();
                    oldConversionQuantity = oldTransactions.get(index).getConversionQuantity() == null ? 0 : oldTransactions.get(index).getConversionQuantity();
                }
            }

            MerchandiseModel merchandiseModel = new MerchandiseModel();
            float quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            float conversionQuantity = item.getConversionQuantity() == null ? 0 : item.getConversionQuantity();
            merchandiseModel.setId(item.getMerchandise().getId());
            MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
            merchandiseWarehouseModel.setAgencyId(exportingWarehouseFullDto.getAgency().getId());
            merchandiseWarehouseModel.setMerchandise(merchandiseModel);
            merchandiseWarehouseModel.setQuantity(oldQuantity - quantity);
            merchandiseWarehouseModel.setConversionQuantity(oldConversionQuantity - conversionQuantity);

            if (item.getMerchandise().getId() != null && (merchandiseWarehouseModel.getQuantity() != 0 || merchandiseWarehouseModel.getConversionQuantity() != 0)) {
                merchandiseWarehouseModels.add(merchandiseWarehouseModel);
            }
        }

        // In case delete transactions
        if (oldTransactions != null) {
            for (ExportingTransactionDto item : oldTransactions) {
                int index = exportingTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1 && item.getMerchandiseId() != null) {
                    MerchandiseModel merchandiseModel = new MerchandiseModel();
                    merchandiseModel.setId(item.getMerchandiseId());
                    MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
                    merchandiseWarehouseModel.setAgencyId(exportingWarehouseFullDto.getAgency().getId());
                    merchandiseWarehouseModel.setMerchandise(merchandiseModel);
                    merchandiseWarehouseModel.setQuantity(item.getQuantity());
                    merchandiseWarehouseModel.setConversionQuantity(item.getConversionQuantity());

                    merchandiseWarehouseModels.add(merchandiseWarehouseModel);
                }
            }
        }

        if (merchandiseWarehouseModels.size() > 0) {
            warehouseRequestService.addMerchandiseQuantity(request, merchandiseWarehouseModels);
        }

        return CompletableFuture.completedFuture(true);
    }

    void setMerchandisePrice(MerchandiseModel merchandiseModel, int group, Double price) {
        switch (group) {
            case 1:
                merchandiseModel.setPrice1(price);
                break;
            case 2:
                merchandiseModel.setPrice2(price);
                break;
            case 3:
                merchandiseModel.setPrice3(price);
                break;
            case 4:
                merchandiseModel.setPrice4(price);
                break;
            case 5:
                merchandiseModel.setPrice5(price);
                break;
            case 6:
                merchandiseModel.setPrice6(price);
                break;
            case 7:
                merchandiseModel.setPrice7(price);
                break;
            case 8:
                merchandiseModel.setPrice8(price);
                break;
            case 9:
                merchandiseModel.setPrice9(price);
                break;
            case 10:
                merchandiseModel.setPrice10(price);
                break;
        }
    }

    boolean updateDeliverStatusOrder(ExportingWarehouseFullDto exportingWarehouseFullDto) {
        if (exportingWarehouseFullDto.getOrder() == null || exportingWarehouseFullDto.getOrder().getId() == null
                || exportingWarehouseFullDto.getOrder().getId().isEmpty()) {
            return true;
        }

        Order order = orderRepository.findById(exportingWarehouseFullDto.getOrder().getId()).get();
        List<ExportingWarehouse> exportingWarehouses = exportingWarehouseRepository.getByOrderId(order.getId());
        List<OrderTransaction> orderTransactions = orderTransactionRepository.getByOrderId(order.getId());

        float totalQuantityOrder = 0;
        for (OrderTransaction item : orderTransactions) {
            float quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            totalQuantityOrder += quantity;
        }

        float totalQuantityDelivery = 0;
        for (ExportingWarehouse item : exportingWarehouses) {
            List<ExportingTransaction> transactions = exportingTransactionRepository.getByExportingId(item.getId());
            for (ExportingTransaction transaction : transactions) {
                float quantity = transaction.getQuantity() == null ? 0 : transaction.getQuantity();
                totalQuantityDelivery += quantity;
            }
        }

        DeliveryStatusEnum deliveryStatusEnum = totalQuantityDelivery < totalQuantityOrder ? DeliveryStatusEnum.UNCOMPLETED : DeliveryStatusEnum.COMPLETED;
        if (order.getDeliverStatus() != deliveryStatusEnum) {
            order.setDeliverStatus(deliveryStatusEnum);
            orderRepository.save(order);
        }

        return true;
    }
}
