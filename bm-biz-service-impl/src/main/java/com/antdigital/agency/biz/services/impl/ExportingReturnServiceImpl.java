package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IExportingReturnDao;
import com.antdigital.agency.dal.data.ExportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IExportingReturnDtoMapper;
import com.antdigital.agency.mappers.IExportingReturnTransactionDtoMapper;
import com.antdigital.agency.services.IExportingReturnService;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ExportingReturnServiceImpl implements IExportingReturnService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    IPaymentRepository paymentRepository;

    @Autowired
    IPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private IPaymentAdviceDetailRepository paymentAdviceDetailRepository;

    @Autowired
    private IImportingWarehouseRepository importingWarehouseRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingReturnRepository exportingReturnRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private ISettingsRepository settingsRepository;

    @Autowired
    private IExportingReturnDao exportingReturnDao;

    @Override
    public ExportingReturnSearchDto search(HttpServletRequest request, ExportingReturnSearchDto exportingReturnSearchDto, String agencyId) throws IOException, JAXBException {

        // Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if (exportingReturnSearchDto.getCustomerCode() != null && !exportingReturnSearchDto.getCustomerCode().isEmpty()) {
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, exportingReturnSearchDto.getCustomerCode());
            for (CustomerModel item : customerModelList) {
                customerIds.add(item.getId());
            }
        }

        // Get merchandise id from warehouse service
        List<String> merchandiseIds = new ArrayList<>();
        if (exportingReturnSearchDto.getMerchandiseCode() != null && !exportingReturnSearchDto.getMerchandiseCode().isEmpty()) {
            List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getMerchandisesLikeCode(request, exportingReturnSearchDto.getMerchandiseCode());
            for (MerchandiseModel merchandiseModel : merchandiseModelList) {
                merchandiseIds.add(merchandiseModel.getId());
            }
        }

        SearchResult<List<ExportingReturnSearchDetail>> result = exportingReturnDao.search(
                exportingReturnSearchDto.getCode(),
                exportingReturnSearchDto.getNumber(),
                exportingReturnSearchDto.getCustomerAddress(),
                exportingReturnSearchDto.getDescription(),
                exportingReturnSearchDto.getNote(),
                customerIds,
                merchandiseIds,
                exportingReturnSearchDto.getStartDate(),
                exportingReturnSearchDto.getEndDate(),
                exportingReturnSearchDto.getCreatedDateSort(),
                exportingReturnSearchDto.getStartNumber(),
                exportingReturnSearchDto.getEndNumber(),
                exportingReturnSearchDto.getCurrentPage(),
                exportingReturnSearchDto.getRecordOfPage(),
                agencyId);

        // Get customer code and name
        customerIds = new ArrayList<>();
        for (ExportingReturnSearchDetail item : result.getResult()) {
            customerIds.add(item.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for (ExportingReturnSearchDetail item : result.getResult()) {
            CustomerModel customerModel = customerModelList.stream().filter(t -> t.getId().equals(item.getCustomerId())).findFirst().orElse(null);
            if (customerModel != null) {
                item.setCustomerCode(customerModel.getCode());
                item.setCustomerName(customerModel.getFullName());
            }
        }

        exportingReturnSearchDto.setTotalRecords(result.getTotalRecords());
        exportingReturnSearchDto.setResult(IExportingReturnDtoMapper.INSTANCE.toExportReturnDetailDtoList(result.getResult()));
        return exportingReturnSearchDto;
    }

    @Override
    public List<ExportingReturnDto> findAll(String agencyId) {
        List<ExportingReturn> exportingReturns = exportingReturnRepository.findAllByAgencyId(agencyId);
        return IExportingReturnDtoMapper.INSTANCE.toExportingReturnDtoList(exportingReturns);
    }

    @Override
    public BaseSearchDto<List<ExportingReturnDto>> findAll(BaseSearchDto<List<ExportingReturnDto>> searchDto, String agencyId) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(agencyId));
            return searchDto;
        }
        if (searchDto.getCreatedDateSort() != null) {
            if (searchDto.getCreatedDateSort().equals("desc")) {
                searchDto.setSortBy("createdDate");
                searchDto.setSortBy("number");
                searchDto.setSortAsc(false);
            }
            if (searchDto.getCreatedDateSort().equals("asc")) {
                searchDto.setSortBy("createdDate");
                searchDto.setSortBy("number");
                searchDto.setSortAsc(true);
            }
        }
        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<ExportingReturn> page = exportingReturnRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IExportingReturnDtoMapper.INSTANCE.toExportingReturnDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ExportingReturnDto getById(String id) {
        ExportingReturn exportingReturn = exportingReturnRepository.findById(id).get();
        return IExportingReturnDtoMapper.INSTANCE.toExportingReturnDto(exportingReturn);
    }

    @Override
    public ExportingReturnFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        ExportingReturn exportingReturn = exportingReturnRepository.findById(id).get();
        ExportingReturnFullDto exportingReturnFull = IExportingReturnDtoMapper.INSTANCE.toExportingReturnFullDto(exportingReturn);
        List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByExportingReturnId(id);

        List<ExportingReturnTransactionFullDto> exportingReturnTransactionFullDtos = new ArrayList<>();
        for (ExportingReturnTransaction exportingReturnTransaction : exportingReturnTransactions) {
            exportingReturnTransactionFullDtos.add(IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionFullDto(exportingReturnTransaction));
        }
        exportingReturnFull.setExportReturnTransactionFulls(exportingReturnTransactionFullDtos);

        CompletableFuture<Boolean> getCustomers = getCustomers(request, exportingReturnFull);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, exportingReturnFull);
        CompletableFuture<Boolean> getMerchandise = getMerchandise(request, exportingReturnFull);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting,
                getMerchandise
        );

        return exportingReturnFull;
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ExportingReturn result = exportingReturnRepository.getExportNumber(sdf.parse(createdDate), agencyId);
            if (result == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(createdDate));
                int month = cal.get(Calendar.MONTH) + 1;
                String monthStr = month > 9 ? String.valueOf(month) : "0" + month ;
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
    public ExportingReturnDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        ExportingReturn exportingReturn = exportingReturnRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IExportingReturnDtoMapper.INSTANCE.toExportingReturnDto(exportingReturn);
    }

    @Override
    public List<ExportingReturnTransactionDto> getByImportingWarehouseId(String importingWarehouseId){
        List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByImportingWarehouseId(importingWarehouseId);
        return IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionDtoList(exportingReturnTransactions);
    }

    @Override
    public int countByImportingId(String importId) {
        int count = exportingReturnTransactionRepository.countByImportingWarehouseId(importId);
        return count;
    }

    @Override
    @Transactional
    public ExportingReturnFullDto insert(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto) {
        try {
            ExportingReturn exportingReturn = IExportingReturnDtoMapper.INSTANCE.toExportingReturn(exportingReturnFullDto);
            exportingReturn.setId(UUIDHelper.generateType4UUID().toString());
            if (exportingReturn.getImportingWarehouse() == null || exportingReturn.getImportingWarehouse().getId() == null
                    || exportingReturn.getImportingWarehouse().getId().isEmpty()) {
                exportingReturn.setImportingWarehouse(null);
            }

            ExportingReturn created = exportingReturnRepository.save(exportingReturn);
            exportingReturnFullDto.setId(created.getId());

            CompletableFuture<Boolean> saveExportingReturnTransaction = saveExportingReturnTransaction(exportingReturnFullDto);
            CompletableFuture<Boolean> addQuantityInReturn = addQuantityInWarehouse(request, exportingReturnFullDto, null);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(exportingReturnFullDto, null);

            CompletableFuture.allOf (
                    saveExportingReturnTransaction,
                    addQuantityInReturn,
                    updateImportingWarehouseStatus
            );
            
            return exportingReturnFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ExportingReturnFullDto update(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto) {
        try {
            ExportingReturn exportingReturn = IExportingReturnDtoMapper.INSTANCE.toExportingReturn(exportingReturnFullDto);
            if (exportingReturn.getImportingWarehouse() == null || exportingReturn.getImportingWarehouse().getId() == null
                    || exportingReturn.getImportingWarehouse().getId().isEmpty()) {
                exportingReturn.setImportingWarehouse(null);
            }
            List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByExportingReturnId(exportingReturn.getId());
            List<ExportingReturnTransactionDto> exportingReturnTransactionDtos = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionDtoList(exportingReturnTransactions);

            exportingReturnRepository.save(exportingReturn);

            CompletableFuture<Boolean> saveExportingReturnTransaction = saveExportingReturnTransaction(exportingReturnFullDto);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, exportingReturnFullDto, exportingReturnTransactionDtos);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(exportingReturnFullDto, exportingReturnTransactionDtos);
            CompletableFuture.allOf (
                    saveExportingReturnTransaction,
                    addQuantityInWarehouse,
                    updateImportingWarehouseStatus
            );

            return exportingReturnFullDto;
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
            ExportingReturn exportingReturn = exportingReturnRepository.findById(id).get();
            ExportingReturnFullDto exportingReturnFullDto = IExportingReturnDtoMapper.INSTANCE.toExportingReturnFullDto(exportingReturn);
            List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByExportingReturnId(id);
            List<ExportingReturnTransactionDto> exportingReturnTransactionDtos = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionDtoList(exportingReturnTransactions);
            for (ExportingReturnTransaction item : exportingReturnTransactions) {
                exportingReturnTransactionRepository.deleteById(item.getId());
            }
            exportingReturnRepository.deleteById(id);

            exportingReturnFullDto.setExportReturnTransactionFulls(new ArrayList<>());
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, exportingReturnFullDto, exportingReturnTransactionDtos);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(exportingReturnFullDto, exportingReturnTransactionDtos);

            CompletableFuture.allOf (
                    addQuantityInWarehouse,
                    updateImportingWarehouseStatus
            );

            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(exportingReturnFullDto.getCustomer().getId());
        ids.add(exportingReturnFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (exportingReturnFullDto.getCustomer() != null && exportingReturnFullDto.getCustomer().getId() != null) {
                if (exportingReturnFullDto.getCustomer().getId().equals(customer.getId())) {
                    exportingReturnFullDto.setCustomer(customer);
                }
            }

            if (exportingReturnFullDto.getTransactionCustomer() != null && exportingReturnFullDto.getTransactionCustomer().getId() != null) {
                if (exportingReturnFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    exportingReturnFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto)
            throws IOException, JAXBException {
        List<ExportingReturnTransactionFullDto> transactionFulls = exportingReturnFullDto.getExportReturnTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ExportingReturnTransactionFullDto item : transactionFulls) {
            int indexCredit = ids.indexOf(item.getCreditAccount().getId());
            int indexDebit = ids.indexOf(item.getDebitAccount().getId());

            if (indexCredit == -1) {
                ids.add(item.getCreditAccount().getId());
            }

            if (indexDebit == -1) {
                ids.add(item.getDebitAccount().getId());
            }
        }

        List<AccountingTableModel> accountingTableModels = warehouseRequestService.getAccountingList(request, ids);
        for (AccountingTableModel account : accountingTableModels) {
            for(ExportingReturnTransactionFullDto item : transactionFulls) {
                if (item.getCreditAccount().getId() != null && item.getCreditAccount().getId().equals(account.getId())) {
                    item.setCreditAccount(account);
                }

                if (item.getDebitAccount().getId() != null && item.getDebitAccount().getId().equals(account.getId())) {
                    item.setDebitAccount(account);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getMerchandise(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto)
            throws IOException, JAXBException {
        List<ExportingReturnTransactionFullDto> transactionFulls = exportingReturnFullDto.getExportReturnTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ExportingReturnTransactionFullDto item : transactionFulls) {
            ids.add(item.getMerchandise().getId());
        }

        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, ids);
        for(MerchandiseModel merchandiseModel : merchandiseModels) {
            for(ExportingReturnTransactionFullDto item : transactionFulls) {
                if(merchandiseModel.getId().equals(item.getMerchandise().getId())) {
                    item.setMerchandise(merchandiseModel);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> saveExportingReturnTransaction(ExportingReturnFullDto exportingReturnFullDto) {
        ExportingReturn exportingReturn = exportingReturnRepository.findById(exportingReturnFullDto.getId()).get();
        List<ExportingReturnTransactionFullDto> exportingReturnTransactionFulls = exportingReturnFullDto.getExportReturnTransactionFulls();
        List<ExportingReturnTransaction> oldReturnTransactions = exportingReturnTransactionRepository.getByExportingReturnId(exportingReturn.getId());

        for (ExportingReturnTransactionFullDto item : exportingReturnTransactionFulls) {
            ExportingReturnTransaction exportingReturnTransaction = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransaction(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                exportingReturnTransaction.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getImportingWarehouse().getId() == null) {
                exportingReturnTransaction.setImportingWarehouse(null);
            }
            exportingReturnTransaction.setExportingReturn(exportingReturn);
            ExportingReturnTransaction created = exportingReturnTransactionRepository.save(exportingReturnTransaction);
            item.setId(created.getId());
        }

        if (oldReturnTransactions != null) {
            for (ExportingReturnTransaction item : oldReturnTransactions) {
                int index = exportingReturnTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    exportingReturnTransactionRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> addQuantityInWarehouse(HttpServletRequest request,
                                                      ExportingReturnFullDto exportingReturnFullDto,
                                                      List<ExportingReturnTransactionDto> oldTransactions)
            throws IOException, JAXBException {

        // In case insert or update transactions
        List<MerchandiseWarehouseModel> merchandiseWarehouseModels = new ArrayList<>();
        List<ExportingReturnTransactionFullDto> exportingReturnTransactionFulls = exportingReturnFullDto.getExportReturnTransactionFulls();
        for (ExportingReturnTransactionFullDto item : exportingReturnTransactionFulls) {
            float oldQuantity = 0;
            float oldConversionQuantity = 0;
            if (oldTransactions != null && oldTransactions.size() > 0) {
                int index = oldTransactions.stream().map(t -> t.getMerchandiseId()).collect(Collectors.toList()).indexOf(item.getMerchandise().getId());
                if (index != -1) {
                    oldQuantity = oldTransactions.get(index).getQuantity();
                    oldConversionQuantity = oldTransactions.get(index).getConversionQuantity();
                }
            }

            MerchandiseModel merchandiseModel = new MerchandiseModel();
            merchandiseModel.setId(item.getMerchandise().getId());
            MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
            merchandiseWarehouseModel.setAgencyId(exportingReturnFullDto.getAgency().getId());
            merchandiseWarehouseModel.setMerchandise(merchandiseModel);
            merchandiseWarehouseModel.setQuantity(oldQuantity - item.getQuantity());
            merchandiseWarehouseModel.setConversionQuantity(oldConversionQuantity - item.getConversionQuantity());

            if (merchandiseWarehouseModel.getQuantity() != 0 || merchandiseWarehouseModel.getConversionQuantity() != 0) {
                merchandiseWarehouseModels.add(merchandiseWarehouseModel);
            }
        }

        // In case delete transactions
        if (oldTransactions != null) {
            for (ExportingReturnTransactionDto item : oldTransactions) {
                int index = exportingReturnTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    MerchandiseModel merchandiseModel = new MerchandiseModel();
                    merchandiseModel.setId(item.getMerchandiseId());
                    MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
                    merchandiseWarehouseModel.setAgencyId(exportingReturnFullDto.getAgency().getId());
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

    @Async
    CompletableFuture<Boolean> updateImportingWarehouseStatus(ExportingReturnFullDto exportingReturnFullDto, List<ExportingReturnTransactionDto> oldExportingReturnTransactions) {
        List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getHasImporting(exportingReturnFullDto.getId());
        List<String> importIds = exportingReturnTransactions.stream().map(t -> t.getImportingWarehouse().getId()).distinct().collect(Collectors.toList());

        for (String id : importIds) {
            Double total = importingTransactionRepository.getTotal(id);
            Double totalPayment = paymentDetailRepository.getTotalByImportingWarehouseId(id);
            Double totalPaymentAdvice = paymentAdviceDetailRepository.getTotalByImportingWarehouseId(id);
            Double totalReturn = exportingReturnTransactionRepository.getTotalByImportingWarehouseId(id);

            totalPayment = totalPayment != null ? totalPayment : 0;
            totalPaymentAdvice = totalPaymentAdvice != null ? totalPaymentAdvice : 0;
            totalReturn = totalReturn != null ? totalReturn : 0;
            totalPayment += totalPaymentAdvice;
            totalPayment += totalReturn;

            PaymentStatusEnum paymentStatusEnum = totalPayment >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(id).get();
            if (importingWarehouse.getPaymentStatus() != paymentStatusEnum) {
                importingWarehouse.setPaymentStatus(paymentStatusEnum);
                importingWarehouseRepository.save(importingWarehouse);
            }
        }

        if (oldExportingReturnTransactions != null && oldExportingReturnTransactions.size() > 0) {
            for (ExportingReturnTransactionDto item : oldExportingReturnTransactions) {
                if (item.getImportingWarehouse() == null || item.getImportingWarehouse().getId() == null) {
                    continue;
                }
                int index = importIds.indexOf(item.getImportingWarehouse().getId());
                if (index == -1) {
                    ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(item.getImportingWarehouse().getId()).get();
                    if (importingWarehouse.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
                        importingWarehouse.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                        importingWarehouseRepository.save(importingWarehouse);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }
}
