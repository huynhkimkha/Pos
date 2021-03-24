package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.enums.SettingKeysEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IImportingWarehouseDao;
import com.antdigital.agency.dal.data.ImportingWarehouseDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IImportingWarehouseService;
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
public class ImportingWarehouseServiceImpl implements IImportingWarehouseService {
    private static final Logger logger = LoggerFactory.getLogger(ImportingWarehouseServiceImpl.class);

    @Autowired
    private IImportingWarehouseRepository importingWarehouseRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private ISettingsRepository settingsRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderTransactionRepository orderTransactionRepository;

    @Autowired
    private IPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private IPaymentAdviceDetailRepository paymentAdviceDetailRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Autowired
    private IImportingWarehouseDao importingWarehouseDao;

    @Override
    public ImportingWarehouseSearchDto search(HttpServletRequest request, ImportingWarehouseSearchDto importingWarehouseSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(importingWarehouseSearchDto.getCustomerCode()!=null && !importingWarehouseSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, importingWarehouseSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }
        //Get merchandise id from warehouse service
        List<String> merchandiseIds = new ArrayList<>();
        if(importingWarehouseSearchDto.getMerchandiseCode()!=null && !importingWarehouseSearchDto.getMerchandiseCode().isEmpty()){
            List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getMerchandisesLikeCode(request, importingWarehouseSearchDto.getMerchandiseCode());
            for(MerchandiseModel merchandiseModel: merchandiseModelList){
                merchandiseIds.add(merchandiseModel.getId());
            }
        }

        SearchResult<List<ImportingWarehouseDetail>> result = importingWarehouseDao.search(
                importingWarehouseSearchDto.getCode(),
                importingWarehouseSearchDto.getNumber(),
                importingWarehouseSearchDto.getCustomerAddress(),
                importingWarehouseSearchDto.getDescription(),
                importingWarehouseSearchDto.getNote(),
                customerIds,
                merchandiseIds,
                importingWarehouseSearchDto.getPaymentStatus(),
                importingWarehouseSearchDto.getStartDate(),
                importingWarehouseSearchDto.getEndDate(),
                importingWarehouseSearchDto.getCreatedDateSort(),
                importingWarehouseSearchDto.getStartNumber(),
                importingWarehouseSearchDto.getEndNumber(),
                importingWarehouseSearchDto.getCurrentPage(),
                importingWarehouseSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(ImportingWarehouseDetail importingWarehouseDetail: result.getResult()){
            customerIds.add(importingWarehouseDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(ImportingWarehouseDetail importingWarehouseDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(importingWarehouseDetail.getCustomerId())).findFirst().orElse(null);
            if(customerModels!=null){
                importingWarehouseDetail.setCustomerCode(customerModels.getCode());
                importingWarehouseDetail.setCustomerName(customerModels.getFullName());
            }
        }

        importingWarehouseSearchDto.setTotalRecords(result.getTotalRecords());
        importingWarehouseSearchDto.setResult(IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDetailDtoList(result.getResult()));
        return importingWarehouseSearchDto;
    }

    @Override
    public List<ImportingWarehouseDto> findAll(String agencyId) {
        List<ImportingWarehouse> importingWarehouses = importingWarehouseRepository.findAllImportingWarehouses(agencyId);
        return IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDtoList(importingWarehouses);
    }

    @Override
    public ImportingWarehouseDto getById(String id) {
        ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(id).get();
        return IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDto(importingWarehouse);
    }

    @Override
    public ImportingWarehouseFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(id).get();
        ImportingWarehouseFullDto importingWarehouseFull = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseFullDto(importingWarehouse);
        List<ImportingTransaction> importingTransactions = importingTransactionRepository.getByImportingId(id);


        Double paymentTotal = paymentDetailRepository.getTotalByImportingWarehouseId(id);
        paymentTotal = paymentTotal != null ? paymentTotal : 0;

        Double paymentAdviceTotal = paymentAdviceDetailRepository.getTotalByImportingWarehouseId(id);
        paymentAdviceTotal = paymentAdviceTotal != null ? paymentAdviceTotal : 0;

        paymentTotal += paymentAdviceTotal;

        Double exportingReturnTotal = exportingReturnTransactionRepository.getTotalByImportingWarehouseId(id);

        List<ImportingTransactionFullDto> importingTransactionFullDtos = new ArrayList<>();
        for (ImportingTransaction importingTransaction : importingTransactions) {
            importingTransactionFullDtos.add(IImportingTransactionDtoMapper.INSTANCE.toImportTransactionFullDto(importingTransaction));
        }
        importingWarehouseFull.setImportTransactionFulls(importingTransactionFullDtos);
        importingWarehouseFull.setPaymentTotal(paymentTotal);
        importingWarehouseFull.setExportingReturnTotal(exportingReturnTotal);

        CompletableFuture<Boolean> getCustomers = getCustomers(request, importingWarehouseFull);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, importingWarehouseFull);
        CompletableFuture<Boolean> getMerchandise = getMerchandise(request, importingWarehouseFull);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting,
                getMerchandise
        );

        return importingWarehouseFull;
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ImportingWarehouse result = importingWarehouseRepository.getImportNumber(sdf.parse(createdDate), agencyId);
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
    public ImportingWarehouseDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        ImportingWarehouse importingWarehouse = importingWarehouseRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDto(importingWarehouse);
    }

    @Override
    public List<ImportingWarehouseDto> getNotCompleted(String customerId, String code) {
        List<ImportingWarehouse> importingWarehouses = importingWarehouseRepository.getNotCompleted(customerId, code);
        return IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDtoList(importingWarehouses);
    }

    @Override
    public List<ImportingWarehouseDto> getLikeCode(String code, String agencyId) {
        List<ImportingWarehouse> importingWarehouses = importingWarehouseRepository.getLikeCode(code, agencyId);
        return IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseDtoList(importingWarehouses);
    }

    @Override
    public List<ImportingWarehouseFullDto> getForPayment(String customerId, Double amount) {
        List<ImportingWarehouse> uncompletedImporting = importingWarehouseRepository.getUncompletedByCustomer(customerId);
        List<ImportingWarehouseFullDto> uncompletedImportingDtos = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseFullDtoList(uncompletedImporting);
        List<ImportingWarehouseFullDto> importingsForPayment = new ArrayList<>();
        Double importingAmount = 0D;
        for (ImportingWarehouseFullDto importing : uncompletedImportingDtos) {

            importing.setTotal(importingTransactionRepository.getTotal(importing.getId()));
            /* Payment and return amount */
            Double paymentTotal = paymentDetailRepository.getTotalByImportingWarehouseId(importing.getId());
            paymentTotal = paymentTotal != null ? paymentTotal : 0;

            Double paymentAdviceTotal = paymentAdviceDetailRepository.getTotalByImportingWarehouseId(importing.getId());
            paymentAdviceTotal = paymentAdviceTotal != null ? paymentAdviceTotal : 0;

            importing.setPaymentTotal(paymentTotal + paymentAdviceTotal);

            Double exportingReturnTotal = exportingReturnTransactionRepository.getTotalByImportingWarehouseId(importing.getId());
            exportingReturnTotal = exportingReturnTotal != null ? exportingReturnTotal : 0;

            importing.setExportingReturnTotal(exportingReturnTotal);

            double paymentAmount = importing.getTotal() - importing.getPaymentTotal() - importing.getExportingReturnTotal();
            if (paymentAmount == 0) {
                continue;
            }
            importingAmount += paymentAmount;

            importingsForPayment.add(importing);
            if (importingAmount >= amount) {
                break;
            }
        }
        return importingsForPayment;
    }

    @Override
    public List<ImportingTransactionDto> getImportingTransactionForReturn(String customerId, String merchandiseId, Float quantity) {
        Float remainingQuantity = quantity;
        List<ImportingTransactionDto> importingTransactionForReturn = new ArrayList<>();
        List<ImportingTransaction> importingTransactions = importingTransactionRepository.getByCustomerIdAndMerchandiseId(customerId, merchandiseId);
        List<ImportingTransactionDto> importingTransactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionDtoList(importingTransactions);
        List<String> importingIds = new ArrayList<>();
        for (ImportingTransactionDto i : importingTransactionDtos) {
            if (i.getImportingWarehouse() != null) {
                int index = importingIds.indexOf(i.getImportingWarehouse().getId());
                if (index == -1) {
                    importingIds.add(i.getImportingWarehouse().getId());
                }
            }
        }
        List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByImportingWarehouseIdList(importingIds);
        List<ExportingReturnTransactionDto> exportingReturnTransactionDtos = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionDtoList(exportingReturnTransactions);
        importingTransactionDtos.sort(Comparator.comparing(i -> i.getImportingWarehouse().getCreatedDate()));
        importingTransactionDtos.sort(Comparator.comparing(i -> i.getImportingWarehouse().getNumber()));
        importingTransactionDtos.sort(Comparator.comparing(i -> i.getImportingWarehouse().getPaymentStatus()));
        for (ImportingTransactionDto i : importingTransactionDtos) {
            List<ExportingReturnTransactionDto> exportingReturnTransactionDtoList = exportingReturnTransactionDtos.stream().filter(item -> item.getMerchandiseId().equals(i.getMerchandiseId())).collect(Collectors.toList());
            if (exportingReturnTransactionDtoList.size() > 0) {
                for (ExportingReturnTransactionDto e : exportingReturnTransactionDtoList) {
                    i.setQuantity(i.getQuantity() - e.getQuantity());
                }
            }
            if (i.getQuantity() <= 0) {
                continue;
            }
            if (remainingQuantity <= i.getQuantity()) {
                importingTransactionForReturn.add(i);
                break;
            } else {
                remainingQuantity -= i.getQuantity();
                importingTransactionForReturn.add(i);
            }
        }
        return importingTransactionForReturn;
    }

    @Override
    public Double getTotal(String id) {
        Double total = importingTransactionRepository.getTotal(id);
        return total;
    }

    @Override

    public Double getDebt(String customerId, String agencyId) {
        List<String> importingIdList = importingWarehouseRepository.getIdListByCustomer(customerId, agencyId);
        if (importingIdList == null || importingIdList.size() == 0){
            return 0D;
        }
        Double total = importingTransactionRepository.getTotalByImportingIdList(importingIdList);

        Double paymentTotal = paymentDetailRepository.getTotalByImportingWarehouseIdList(importingIdList);
        paymentTotal = paymentTotal != null ? paymentTotal : 0;

        Double paymentAdviceTotal = paymentAdviceDetailRepository.getTotalByImportingWarehouseIdList(importingIdList);
        paymentAdviceTotal = paymentAdviceTotal != null ? paymentAdviceTotal : 0;

        return total - paymentTotal - paymentAdviceTotal;
    }

    @Override
    public float countByOrder(String orderId) {
        float count = importingWarehouseRepository.countByOrderId(orderId);
        return count;
    }

    @Override
    public List<ImportingTransactionFullDto> GetByOrderId(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException {
        List<ImportingWarehouse> importingWarehouses = importingWarehouseRepository.getByOrderId(id, agencyId);
        List<ImportingTransactionFullDto> importingTransactionFullDtos = new ArrayList<>();
        for (ImportingWarehouse importingWarehouse: importingWarehouses){
            ImportingWarehouseFullDto importingWarehouseFullDto = getFullById(request, importingWarehouse.getId());
            for (ImportingTransactionFullDto importingTransactionFullDto: importingWarehouseFullDto.getImportTransactionFulls()){
                importingTransactionFullDtos.add(importingTransactionFullDto);
            }
        }
        List<ImportingTransactionFullDto> importingTransactionFullDtoList = new ArrayList<>();
        for (ImportingTransactionFullDto importingTransactionFullDto: importingTransactionFullDtos){
            if (importingTransactionFullDtoList.size() == 0) {
                importingTransactionFullDtoList.add(importingTransactionFullDto);
                continue;
            }

            int i = 0;
            for (ImportingTransactionFullDto importingTransactionFullDto1: importingTransactionFullDtoList){
                if(importingTransactionFullDto1.getMerchandise().getId().equals(importingTransactionFullDto.getMerchandise().getId())){
                    importingTransactionFullDto1.setQuantity(importingTransactionFullDto1.getQuantity() + importingTransactionFullDto.getQuantity());
                    importingTransactionFullDto1.setConversionQuantity(importingTransactionFullDto1.getConversionQuantity() + importingTransactionFullDto.getConversionQuantity());
                    i++;
                    break;
                }
            }
            if(i == 0){
                importingTransactionFullDtoList.add(importingTransactionFullDto);
            }
        }

        return importingTransactionFullDtoList;
    }

    @Override
    @Transactional
    public ImportingWarehouseFullDto insert(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto) {
        try {
            ImportingWarehouse importingWarehouse = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouse(importingWarehouseFullDto);
            importingWarehouse.setId(UUIDHelper.generateType4UUID().toString());
            if (importingWarehouse.getOrder() == null || importingWarehouse.getOrder().getId() == null
                    || importingWarehouse.getOrder().getId().isEmpty()) {
                importingWarehouse.setOrder(null);
            }
            ImportingWarehouse created = importingWarehouseRepository.save(importingWarehouse);
            importingWarehouseFullDto.setId(created.getId());

            CompletableFuture<Boolean> saveImportingTransaction = saveImportingTransaction(importingWarehouseFullDto, null);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingWarehouseFullDto, null);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, importingWarehouseFullDto);

            CompletableFuture.allOf (
                    saveImportingTransaction,
                    addQuantityInWarehouse,
                    updatePurchasePrice
            );

            updateImportStatusOrder(importingWarehouseFullDto);

            return importingWarehouseFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ImportingWarehouseFullDto update(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto) {
        try {
            ImportingWarehouse importingWarehouse = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouse(importingWarehouseFullDto);
            List<ImportingTransaction> importingTransactions = importingTransactionRepository.getByImportingId(importingWarehouseFullDto.getId());
            if (importingWarehouse.getOrder() == null || importingWarehouse.getOrder().getId() == null
                    || importingWarehouse.getOrder().getId().isEmpty()) {
                importingWarehouse.setOrder(null);
            }
            importingWarehouseRepository.save(importingWarehouse);

            // new instance list
            List<ImportingTransactionDto> importingTransactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionDtoList(importingTransactions);

            CompletableFuture<Boolean> saveImportingTransaction = saveImportingTransaction(importingWarehouseFullDto, importingTransactions);
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingWarehouseFullDto, importingTransactionDtos);
            CompletableFuture<Boolean> updatePurchasePrice = updatePurchasePrice(request, importingWarehouseFullDto);

            CompletableFuture.allOf (
                    saveImportingTransaction,
                    addQuantityInWarehouse,
                    updatePurchasePrice
            );

            updateImportStatusOrder(importingWarehouseFullDto);

            return importingWarehouseFullDto;
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
            ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(id).get();
            if (importingWarehouse.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
                return false;
            }

            List<ImportingTransaction> importingTransactions = importingTransactionRepository.getByImportingId(id);
            List<ImportingTransactionDto> importingTransactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionDtoList(importingTransactions);
            for (ImportingTransaction importingTransaction : importingTransactions) {
                importingTransactionRepository.deleteById(importingTransaction.getId());
            }
            importingWarehouseRepository.deleteById(id);

            ImportingWarehouseFullDto importingWarehouseFullDto = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouseFullDto(importingWarehouse);
            importingWarehouseFullDto.setImportTransactionFulls(new ArrayList<>());
            CompletableFuture<Boolean> addQuantityInWarehouse = addQuantityInWarehouse(request, importingWarehouseFullDto, importingTransactionDtos);

            CompletableFuture.allOf(addQuantityInWarehouse);

            updateImportStatusOrder(importingWarehouseFullDto);

            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Async
    CompletableFuture<Boolean> saveImportingTransaction(ImportingWarehouseFullDto importingWarehouseFullDto,
                                                        List<ImportingTransaction> oldTransactions) {
        List<ImportingTransactionFullDto> importingTransactionFulls = importingWarehouseFullDto.getImportTransactionFulls();
        ImportingWarehouse importingWarehouse = IImportingWarehouseDtoMapper.INSTANCE.toImportWarehouse(importingWarehouseFullDto);

        for (ImportingTransactionFullDto item : importingTransactionFulls) {
            ImportingTransaction importingTransaction = IImportingTransactionDtoMapper.INSTANCE.toImportTransaction(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                importingTransaction.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getOrder().getId() == null) {
                importingTransaction.setOrder(null);
            }
            importingTransaction.setImportingWarehouse(importingWarehouse);
            ImportingTransaction created = importingTransactionRepository.save(importingTransaction);
            item.setId(created.getId());
        }

        if (oldTransactions != null) {
            for (ImportingTransaction item : oldTransactions) {
                int index = importingTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    importingTransactionRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> addQuantityInWarehouse(HttpServletRequest request,
                                                         ImportingWarehouseFullDto importingWarehouseFullDto,
                                                         List<ImportingTransactionDto> oldTransactions)
            throws IOException, JAXBException {
        // In case insert or update transactions
        List<MerchandiseWarehouseModel> merchandiseWarehouseModels = new ArrayList<>();
        List<ImportingTransactionFullDto> importingTransactionFulls = importingWarehouseFullDto.getImportTransactionFulls();
        for (ImportingTransactionFullDto item : importingTransactionFulls) {
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
            merchandiseWarehouseModel.setAgencyId(importingWarehouseFullDto.getAgency().getId());
            merchandiseWarehouseModel.setMerchandise(merchandiseModel);
            merchandiseWarehouseModel.setQuantity(quantity - oldQuantity);
            merchandiseWarehouseModel.setConversionQuantity(conversionQuantity - oldConversionQuantity);

            if (item.getMerchandise().getId() != null && (quantity != 0 || conversionQuantity != 0)) {
                merchandiseWarehouseModels.add(merchandiseWarehouseModel);
            }
        }

        // In case delete transactions
        if (oldTransactions != null) {
            for (ImportingTransactionDto item : oldTransactions) {
                int index = importingTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1 && item.getMerchandiseId() != null) {
                    MerchandiseModel merchandiseModel = new MerchandiseModel();
                    merchandiseModel.setId(item.getMerchandiseId());
                    MerchandiseWarehouseModel merchandiseWarehouseModel = new MerchandiseWarehouseModel();
                    merchandiseWarehouseModel.setAgencyId(importingWarehouseFullDto.getAgency().getId());
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
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(importingWarehouseFullDto.getCustomer().getId());
        ids.add(importingWarehouseFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (importingWarehouseFullDto.getCustomer() != null && importingWarehouseFullDto.getCustomer().getId() != null) {
                if (importingWarehouseFullDto.getCustomer().getId().equals(customer.getId())) {
                    importingWarehouseFullDto.setCustomer(customer);
                }
            }

            if (importingWarehouseFullDto.getTransactionCustomer() != null && importingWarehouseFullDto.getTransactionCustomer().getId() != null) {
                if (importingWarehouseFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    importingWarehouseFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ImportingTransactionFullDto> transactionFulls = importingWarehouseFullDto.getImportTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ImportingTransactionFullDto item : transactionFulls) {
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
            for(ImportingTransactionFullDto item : transactionFulls) {
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
    CompletableFuture<Boolean> getMerchandise(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ImportingTransactionFullDto> transactionFulls = importingWarehouseFullDto.getImportTransactionFulls();
        List<String> ids = new ArrayList<>();
        for(ImportingTransactionFullDto item : transactionFulls) {
            ids.add(item.getMerchandise().getId());
        }

        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, ids);
        for(MerchandiseModel merchandiseModel : merchandiseModels) {
            for(ImportingTransactionFullDto item : transactionFulls) {
                if(merchandiseModel.getId().equals(item.getMerchandise().getId())) {
                    item.setMerchandise(merchandiseModel);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> updatePurchasePrice(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto)
            throws IOException, JAXBException {
        List<ImportingTransactionFullDto> importingTransactionFulls = importingWarehouseFullDto.getImportTransactionFulls();
        List<MerchandiseModel> merchandiseModels = new ArrayList<>();
        for (ImportingTransactionFullDto item : importingTransactionFulls) {
            MerchandiseModel merchandiseModel = new MerchandiseModel();
            merchandiseModel.setId(item.getMerchandise().getId());
            merchandiseModel.setPurchasePrice(item.getPrice());
            merchandiseModel.setConversionPurchasePrice(item.getConversionPrice());

            merchandiseModels.add(merchandiseModel);
        }

        warehouseRequestService.updatePurchasePrice(request, merchandiseModels);

        return CompletableFuture.completedFuture(true);
    }

    boolean updateImportStatusOrder(ImportingWarehouseFullDto importingWarehouseFullDto) {
        if (importingWarehouseFullDto.getOrder() == null || importingWarehouseFullDto.getOrder().getId() == null
                || importingWarehouseFullDto.getOrder().getId().isEmpty()) {
            return true;
        }

        Order order = orderRepository.findById(importingWarehouseFullDto.getOrder().getId()).get();
        List<ImportingWarehouse> importingWarehouses = importingWarehouseRepository.getByOrderId(order.getId(), importingWarehouseFullDto.getAgency().getId());
        List<OrderTransaction> orderTransactions = orderTransactionRepository.getByOrderId(order.getId());

        float totalQuantityOrder = 0;
        for (OrderTransaction item : orderTransactions) {
            float quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            totalQuantityOrder += quantity;
        }

        float totalQuantityImport = 0;
        for (ImportingWarehouse item : importingWarehouses) {
            List<ImportingTransaction> transactions = importingTransactionRepository.getByImportingId(item.getId());
            for (ImportingTransaction transaction : transactions) {
                float quantity = transaction.getQuantity() == null ? 0 : transaction.getQuantity();
                totalQuantityImport += quantity;
            }
        }

        ImportStatusEnum importStatusEnum = totalQuantityImport < totalQuantityOrder ? ImportStatusEnum.UNCOMPLETED : ImportStatusEnum.COMPLETED;
        if (order.getImportStatus() != importStatusEnum) {
            order.setImportStatus(importStatusEnum);
            orderRepository.save(order);
        }

        return true;
    }
}
