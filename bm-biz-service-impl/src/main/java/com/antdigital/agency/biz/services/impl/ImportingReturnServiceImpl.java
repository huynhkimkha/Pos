package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IImportingReturnDao;
import com.antdigital.agency.dal.data.ImportingReturnSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.*;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IImportingReturnService;
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
public class ImportingReturnServiceImpl implements IImportingReturnService {
    private static final Logger logger = LoggerFactory.getLogger(ImportingReturnServiceImpl.class);

    @Autowired
    private IReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private IReceiptAdviceDetailRepository receiptAdviceDetailRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private IImportingReturnRepository importingReturnRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IImportingReturnDao importingReturnDao;

    @Override
    public ImportingReturnSearchDto search(HttpServletRequest request, ImportingReturnSearchDto importingReturnSearchDto, String agencyId) throws IOException, JAXBException {

        // Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if (importingReturnSearchDto.getCustomerCode() != null && !importingReturnSearchDto.getCustomerCode().isEmpty()) {
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, importingReturnSearchDto.getCustomerCode());
            for (CustomerModel customerModel : customerModelList) {
                customerIds.add(customerModel.getId());
            }
        }
        // Get merchandise id from warehouse service
        List<String> merchandiseIds = new ArrayList<>();
        if (importingReturnSearchDto.getMerchandiseCode() != null && !importingReturnSearchDto.getMerchandiseCode().isEmpty()) {
            List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getMerchandisesLikeCode(request, importingReturnSearchDto.getMerchandiseCode());
            for (MerchandiseModel item : merchandiseModelList) {
                merchandiseIds.add(item.getId());
            }
        }

        SearchResult<List<ImportingReturnSearchDetail>> result = importingReturnDao.search(
                importingReturnSearchDto.getCode(),
                importingReturnSearchDto.getNumber(),
                importingReturnSearchDto.getCustomerAddress(),
                importingReturnSearchDto.getDescription(),
                importingReturnSearchDto.getNote(),
                customerIds,
                merchandiseIds,
                importingReturnSearchDto.getStartDate(),
                importingReturnSearchDto.getEndDate(),
                importingReturnSearchDto.getCreatedDateSort(),
                importingReturnSearchDto.getStartNumber(),
                importingReturnSearchDto.getEndNumber(),
                importingReturnSearchDto.getCurrentPage(),
                importingReturnSearchDto.getRecordOfPage(),
                agencyId
        );

        // get customer code and name
        customerIds = new ArrayList<>();
        for (ImportingReturnSearchDetail item : result.getResult()) {
            customerIds.add(item.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for (ImportingReturnSearchDetail importingReturnSearchDetail : result.getResult()) {
            CustomerModel customerModel = customerModelList.stream().filter(item -> item.getId().equals(importingReturnSearchDetail.getCustomerId())).findFirst().orElse(null);
            if (customerModel != null) {
                importingReturnSearchDetail.setCustomerCode(customerModel.getCode());
                importingReturnSearchDetail.setCustomerName(customerModel.getFullName());
            }
        }

        importingReturnSearchDto.setTotalRecords(result.getTotalRecords());
        importingReturnSearchDto.setResult(IImportingReturnDtoMapper.INSTANCE.toImportingReturnDetailDtoList(result.getResult()));
        return importingReturnSearchDto;
    }

    @Override
    public List<ImportingReturnDto> findAll(String agencyId) {
        List<ImportingReturn> importingReturns = importingReturnRepository.findAllImportinReturn(agencyId);
        return IImportingReturnDtoMapper.INSTANCE.toImportReturnDtoList(importingReturns);
    }

    @Override
    public BaseSearchDto<List<ImportingReturnDto>> findAll(BaseSearchDto<List<ImportingReturnDto>> searchDto, String agencyId) {
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

        Page<ImportingReturn> page = importingReturnRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IImportingReturnDtoMapper.INSTANCE.toImportReturnDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ImportingReturn result = importingReturnRepository.getImportReturnNumber(sdf.parse(createdDate), agencyId);
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
    public ImportingReturnDto getById(String id) {
        ImportingReturn importingReturn = importingReturnRepository.findById(id).get();
        return IImportingReturnDtoMapper.INSTANCE.toImportReturnDto(importingReturn);
    }

    @Override
    public ImportingReturnDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        ImportingReturn importingReturn = importingReturnRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IImportingReturnDtoMapper.INSTANCE.toImportReturnDto(importingReturn);
    }

    @Override
    public ImportingReturnFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        ImportingReturn importingReturn = importingReturnRepository.findById(id).get();
        ImportingReturnFullDto importingReturnFull = IImportingReturnDtoMapper.INSTANCE.toImportReturnFullDto(importingReturn);
        List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getByImportingId(id);


        List<ImportingReturnTransactionFullDto> importingReturnTransactionFullDtos = new ArrayList<>();
        for (ImportingReturnTransaction importingReturnTransaction : importingReturnTransactions) {
            importingReturnTransactionFullDtos.add(IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransactionFullDto(importingReturnTransaction));
        }
        importingReturnFull.setImportReturnTransactionFulls(importingReturnTransactionFullDtos);

        CompletableFuture<Boolean> getCustomers = getCustomers(request, importingReturnFull);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, importingReturnFull);
        CompletableFuture<Boolean> getMerchandise = getMerchandise(request, importingReturnFull);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting,
                getMerchandise
        );

        return importingReturnFull;
    }

    @Override
    public Double getTotal(String id) {
        Double total = importingReturnTransactionRepository.getTotal(id);
        return total;
    }

    @Override
    public List<ImportingReturnTransactionDto> getTransactionById(String exportId) {
        List<ImportingReturnTransaction> exportingWarehouses = importingReturnTransactionRepository.getTransactionById(exportId);
        return IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransactionDtoList(exportingWarehouses);
    }

    @Override
    public int countByExportId(String exportId) {
        int count = importingReturnTransactionRepository.countByExportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public ImportingReturnFullDto insert(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto) {
        try {
            ImportingReturn importingReturn = IImportingReturnDtoMapper.INSTANCE.toImportReturn(importingReturnFullDto);
            importingReturn.setId(UUIDHelper.generateType4UUID().toString());
            if (importingReturn.getExportingWarehouse() == null || importingReturn.getExportingWarehouse().getId() == null
                    || importingReturn.getExportingWarehouse().getId().isEmpty()) {
                importingReturn.setExportingWarehouse(null);
            }
            ImportingReturn created = importingReturnRepository.save(importingReturn);
            importingReturnFullDto.setId(created.getId());
            CompletableFuture<Boolean> saveImportingTransaction = saveImportingTransaction(importingReturnFullDto, null);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingReturnFullDto, null);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, importingReturnFullDto);
            CompletableFuture<Boolean> updateExportingWarehouseStatus = updateExportingWarehouseStatus(importingReturnFullDto, null);

            CompletableFuture.allOf (
                    saveImportingTransaction,
                    addQuantityInWarehouse,
                    updatePurchasePrice,
                    updateExportingWarehouseStatus
            );

            return importingReturnFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ImportingReturnFullDto update(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto) {
        try {
            ImportingReturn importingReturn = IImportingReturnDtoMapper.INSTANCE.toImportReturn(importingReturnFullDto);
            if (importingReturn.getExportingWarehouse() == null || importingReturn.getExportingWarehouse().getId() == null
                    || importingReturn.getExportingWarehouse().getId().isEmpty()) {
                importingReturn.setExportingWarehouse(null);
            }
            List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getByImportingId(importingReturnFullDto.getId());
            importingReturnRepository.save(importingReturn);

            // new instance list
            List<ImportingReturnTransactionDto> importingReturnTransactionDtos = IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransactionDtoList(importingReturnTransactions);

            CompletableFuture<Boolean> saveImportingTransaction = saveImportingTransaction(importingReturnFullDto, importingReturnTransactions);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingReturnFullDto, importingReturnTransactionDtos);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, importingReturnFullDto);
            CompletableFuture<Boolean> updateExportingWarehouseStatus = updateExportingWarehouseStatus(importingReturnFullDto, importingReturnTransactionDtos);

            CompletableFuture.allOf (
                    saveImportingTransaction,
                    addQuantityInWarehouse,
                    updatePurchasePrice,
                    updateExportingWarehouseStatus
            );

            return importingReturnFullDto;
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
            ImportingReturn importingReturn = importingReturnRepository.findById(id).get();

            List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getByImportingId(id);
            List<ImportingReturnTransactionDto> importingReturnTransactionDtos = IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransactionDtoList(importingReturnTransactions);
            for (ImportingReturnTransaction importingReturnTransaction : importingReturnTransactions) {
                importingReturnTransactionRepository.deleteById(importingReturnTransaction.getId());
            }
            importingReturnRepository.deleteById(id);

            ImportingReturnFullDto importingReturnFullDto = IImportingReturnDtoMapper.INSTANCE.toImportReturnFullDto(importingReturn);
            importingReturnFullDto.setImportReturnTransactionFulls(new ArrayList<>());
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingReturnFullDto, importingReturnTransactionDtos);
            CompletableFuture<Boolean> updateExportingWarehouseStatus = updateExportingWarehouseStatus(importingReturnFullDto, importingReturnTransactionDtos);

            CompletableFuture.allOf(
                    addQuantityInWarehouse,
                    updateExportingWarehouseStatus
            );

            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }


    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(importingReturnFullDto.getCustomer().getId());
        ids.add(importingReturnFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (importingReturnFullDto.getCustomer() != null && importingReturnFullDto.getCustomer().getId() != null) {
                if (importingReturnFullDto.getCustomer().getId().equals(customer.getId())) {
                    importingReturnFullDto.setCustomer(customer);
                }
            }

            if (importingReturnFullDto.getTransactionCustomer() != null && importingReturnFullDto.getTransactionCustomer().getId() != null) {
                if (importingReturnFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    importingReturnFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto)
            throws IOException, JAXBException {
        List<ImportingReturnTransactionFullDto> transactionFulls = importingReturnFullDto.getImportReturnTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ImportingReturnTransactionFullDto item : transactionFulls) {
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
            for(ImportingReturnTransactionFullDto item : transactionFulls) {
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
    CompletableFuture<Boolean> getMerchandise(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto)
            throws IOException, JAXBException {
        List<ImportingReturnTransactionFullDto> transactionFulls = importingReturnFullDto.getImportReturnTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ImportingReturnTransactionFullDto item : transactionFulls) {
            ids.add(item.getMerchandise().getId());
        }

        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, ids);
        for(MerchandiseModel merchandiseModel : merchandiseModels) {
            for(ImportingReturnTransactionFullDto item : transactionFulls) {
                if(merchandiseModel.getId().equals(item.getMerchandise().getId())) {
                    item.setMerchandise(merchandiseModel);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> saveImportingTransaction(ImportingReturnFullDto importingReturnFullDto,
                                                        List<ImportingReturnTransaction> oldTransactions) {
        List<ImportingReturnTransactionFullDto> importingReturnTransactionFulls = importingReturnFullDto.getImportReturnTransactionFulls();
        ImportingReturn importingReturn = IImportingReturnDtoMapper.INSTANCE.toImportReturn(importingReturnFullDto);

        for (ImportingReturnTransactionFullDto item : importingReturnTransactionFulls) {
            ImportingReturnTransaction importingReturnTransaction = IImportingReturnTransactionDtoMapper.INSTANCE.toImportReturnTransaction(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                importingReturnTransaction.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getExportingWarehouse().getId() == null) {
                importingReturnTransaction.setExportingWarehouse(null);
            }
            importingReturnTransaction.setImportingReturn(importingReturn);
            ImportingReturnTransaction created = importingReturnTransactionRepository.save(importingReturnTransaction);
            item.setId(created.getId());
        }

        if (oldTransactions != null) {
            for (ImportingReturnTransaction item : oldTransactions) {
                int index = importingReturnTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    importingReturnTransactionRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }


    @Async
    CompletableFuture<Boolean> addQuantityInWarehouse(HttpServletRequest request,
                                                      ImportingReturnFullDto importingReturnFullDto,
                                                      List<ImportingReturnTransactionDto> oldTransactions)
            throws IOException, JAXBException {
        // In case insert or update transactions
        List<MerchandiseWarehouseModel> merchandiseWarehouseModels = new ArrayList<>();
        List<ImportingReturnTransactionFullDto> importingTransactionFulls = importingReturnFullDto.getImportReturnTransactionFulls();
        for (ImportingReturnTransactionFullDto item : importingTransactionFulls) {
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
            merchandiseWarehouseModel.setAgencyId(importingReturnFullDto.getAgency().getId());
            merchandiseWarehouseModel.setMerchandise(merchandiseModel);
            merchandiseWarehouseModel.setQuantity(item.getQuantity() - oldQuantity);
            merchandiseWarehouseModel.setConversionQuantity(item.getConversionQuantity() - oldConversionQuantity);

            if (merchandiseWarehouseModel.getQuantity() != 0 || merchandiseWarehouseModel.getConversionQuantity() != 0) {
                merchandiseWarehouseModels.add(merchandiseWarehouseModel);
            }
        }

        // In case delete transactions
        if (oldTransactions != null) {
            for (ImportingReturnTransactionDto item : oldTransactions) {
                int index = importingTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    MerchandiseModel merchandiseModel = new MerchandiseModel();
                    merchandiseModel.setId(item.getMerchandiseId());
                    MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
                    merchandiseWarehouseModel.setAgencyId(importingReturnFullDto.getAgency().getId());
                    merchandiseWarehouseModel.setMerchandise(merchandiseModel);
                    merchandiseWarehouseModel.setQuantity(-item.getQuantity());
                    merchandiseWarehouseModel.setConversionQuantity(-item.getConversionQuantity());

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
    CompletableFuture<Boolean> updatePurchasePrice(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto)
            throws IOException, JAXBException {
        List<ImportingReturnTransactionFullDto> importingTransactionFulls = importingReturnFullDto.getImportReturnTransactionFulls();
        List<MerchandiseModel> merchandiseModels = new ArrayList<>();
        for (ImportingReturnTransactionFullDto item : importingTransactionFulls) {
            MerchandiseModel merchandiseModel = new MerchandiseModel();
            merchandiseModel.setId(item.getMerchandise().getId());
            merchandiseModel.setPurchasePrice(item.getPrice());
            merchandiseModel.setConversionPurchasePrice(item.getConversionPrice());

            merchandiseModels.add(merchandiseModel);
        }

        warehouseRequestService.updatePurchasePrice(request, merchandiseModels);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> updateExportingWarehouseStatus(ImportingReturnFullDto importingReturnFullDto, List<ImportingReturnTransactionDto> oldImportingReturnTransactions) {
        List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getHasExporting(importingReturnFullDto.getId());
        List<String> exportIds = importingReturnTransactions.stream().map(t -> t.getExportingWarehouse().getId()).distinct().collect(Collectors.toList());

        for (String id : exportIds) {
            Double total = exportingTransactionRepository.getTotal(id);
            Double totalReceipt = receiptDetailRepository.getTotalByExportingWarehouseId(id);
            Double totalReceiptAdvice = receiptAdviceDetailRepository.getTotalByExportingWarehouseId(id);
            Double totalReturn = importingReturnTransactionRepository.getTotalByExportingWarehouseId(id);

            totalReceipt = totalReceipt != null ? totalReceipt : 0;
            totalReceiptAdvice = totalReceiptAdvice != null ? totalReceiptAdvice : 0;
            totalReturn = totalReturn != null ? totalReturn : 0;
            totalReceipt += totalReceiptAdvice;
            totalReceipt += totalReturn;

            PaymentStatusEnum paymentStatusEnum = totalReceipt >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
            if (exportingWarehouse.getPaymentStatus() != paymentStatusEnum) {
                exportingWarehouse.setPaymentStatus(paymentStatusEnum);
                exportingWarehouseRepository.save(exportingWarehouse);
            }
        }

        if (oldImportingReturnTransactions != null && oldImportingReturnTransactions.size() > 0) {
            for (ImportingReturnTransactionDto item : oldImportingReturnTransactions) {
                if (item.getExportingWarehouse() == null || item.getExportingWarehouse().getId() == null) {
                    continue;
                }
                int index = exportIds.indexOf(item.getExportingWarehouse().getId());
                if (index == -1) {
                    ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(item.getExportingWarehouse().getId()).get();
                    if (exportingWarehouse.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
                        exportingWarehouse.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                        exportingWarehouseRepository.save(exportingWarehouse);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }
}
