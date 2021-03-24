package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IReceiptAdviceDao;
import com.antdigital.agency.dal.data.ReceiptAdviceSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReceiptAdviceSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.mappers.IReceiptAdviceDtoMapper;
import com.antdigital.agency.services.IReceiptAdviceService;
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
public class ReceiptAdviceServiceImpl implements IReceiptAdviceService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptServiceImpl.class);

    @Autowired
    private IReceiptAdviceRepository receiptAdviceRepository;

    @Autowired
    private IReceiptAdviceDetailRepository receiptAdviceDetailRepository;

    @Autowired
    private IReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IReceiptAdviceDao receiptAdviceDao;

    @Override
    public ReceiptAdviceSearchDto search(HttpServletRequest request, ReceiptAdviceSearchDto receiptAdviceSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(receiptAdviceSearchDto.getCustomerCode()!=null && !receiptAdviceSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, receiptAdviceSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }

        SearchResult<List<ReceiptAdviceSearchDetail>> result = receiptAdviceDao.search(
                receiptAdviceSearchDto.getCode(),
                receiptAdviceSearchDto.getNumber(),
                receiptAdviceSearchDto.getCustomerAddress(),
                receiptAdviceSearchDto.getDescription(),
                receiptAdviceSearchDto.getNote(),
                customerIds,
                receiptAdviceSearchDto.getStartDate(),
                receiptAdviceSearchDto.getEndDate(),
                receiptAdviceSearchDto.getCreatedDateSort(),
                receiptAdviceSearchDto.getStartNumber(),
                receiptAdviceSearchDto.getEndNumber(),
                receiptAdviceSearchDto.getCurrentPage(),
                receiptAdviceSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(ReceiptAdviceSearchDetail receiptAdviceSearchDetail: result.getResult()){
            customerIds.add(receiptAdviceSearchDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(ReceiptAdviceSearchDetail receiptAdviceSearchDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(receiptAdviceSearchDetail.getCustomerId())).findFirst().orElse(null);
            if(customerModels!=null){
                receiptAdviceSearchDetail.setCustomerCode(customerModels.getCode());
                receiptAdviceSearchDetail.setCustomerName(customerModels.getFullName());
            }
        }

        receiptAdviceSearchDto.setTotalRecords(result.getTotalRecords());
        receiptAdviceSearchDto.setResult(IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceSearchDetailDtoList(result.getResult()));
        return receiptAdviceSearchDto;
    }

    @Override
    public List<ReceiptAdviceDto> findAll(String agencyId) {
        List<ReceiptAdvice> receiptAdvices = receiptAdviceRepository.findAll(agencyId);
        return IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceDtoList(receiptAdvices);
    }
    @Override
    public BaseSearchDto<List<ReceiptAdviceDto>> findAll(BaseSearchDto<List<ReceiptAdviceDto>> searchDto, String agencyId) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(agencyId));
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<ReceiptAdvice> page = receiptAdviceRepository.findAll(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ReceiptAdviceDto getById(String id, String agencyId) {
        ReceiptAdvice receiptAdvice = receiptAdviceRepository.findById(id).get();
        return IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceDto(receiptAdvice);
    }

    @Override
    public ReceiptAdviceFullDto getFullById(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException {
        ReceiptAdvice receiptAdvice = receiptAdviceRepository.findById(id).get();
        ReceiptAdviceFullDto receiptAdviceFullDto = IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceFullDto(receiptAdvice);
        List<ReceiptAdviceDetail> receiptAdviceDetails = receiptAdviceDetailRepository.getByReceiptAdviceId(id);
        receiptAdviceFullDto.setReceiptAdviceDetails(IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailFullDtoList(receiptAdviceDetails));

        CompletableFuture<Boolean> getCustomers = getCustomers(request, receiptAdviceFullDto);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, receiptAdviceFullDto);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting
        );

        return receiptAdviceFullDto;
    }

    @Override
    public String getReceiptAdviceNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ReceiptAdvice result = receiptAdviceRepository.getReceiptAdviceNumber(sdf.parse(createdDate), agencyId);
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
    public ReceiptAdviceDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        ReceiptAdvice receiptAdvice = receiptAdviceRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceDto(receiptAdvice);
    }

    @Override
    public int countByExportId(String exportId) {
        int count = receiptAdviceDetailRepository.countByExportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public ReceiptAdviceFullDto insert(ReceiptAdviceFullDto receiptAdviceFullDto, String agencyId) {
        try {
            ReceiptAdvice receiptAdvice = IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdvice(receiptAdviceFullDto);
            receiptAdvice.setId(UUIDHelper.generateType4UUID().toString());
            receiptAdviceRepository.save(receiptAdvice);

            receiptAdviceFullDto.setId(receiptAdvice.getId());
            CompletableFuture<Boolean> saveReceiptAdviceDetail = saveReceiptAdviceDetail(receiptAdviceFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(receiptAdviceFullDto, null);

            CompletableFuture.allOf (
                    saveReceiptAdviceDetail,
                    updateExportingWarehousePaymentStatus
            );

            updateExportingWarehousePaymentStatus(receiptAdviceFullDto, null);

            return receiptAdviceFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ReceiptAdviceFullDto update(ReceiptAdviceFullDto receiptAdviceFullDto, String agencyId) {
        try {
            List<ReceiptAdviceDetail> receiptAdviceDetails = receiptAdviceDetailRepository.getByReceiptAdviceId(receiptAdviceFullDto.getId());
            List<ReceiptAdviceDetailDto> receiptAdviceDetailDtos = IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailDtoList(receiptAdviceDetails);
            ReceiptAdvice receiptAdvice = IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdvice(receiptAdviceFullDto);
            receiptAdviceRepository.save(receiptAdvice);

            CompletableFuture<Boolean> saveReceiptAdviceDetail = saveReceiptAdviceDetail(receiptAdviceFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(receiptAdviceFullDto, receiptAdviceDetailDtos);

            CompletableFuture.allOf (
                    saveReceiptAdviceDetail,
                    updateExportingWarehousePaymentStatus
            );

            return receiptAdviceFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(String id, String agencyId) {
        try {
            ReceiptAdvice receiptAdvice = receiptAdviceRepository.findById(id).get();
            List<ReceiptAdviceDetail> receiptAdviceDetails = receiptAdviceDetailRepository.getByReceiptAdviceId(id);
            List<ReceiptAdviceDetailDto> receiptAdviceDetailDtos = IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailDtoList(receiptAdviceDetails);
            ReceiptAdviceFullDto receiptAdviceFullDto = IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdviceFullDto(receiptAdvice);
            receiptAdviceFullDto.setReceiptAdviceDetails(IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailFullDtoList(receiptAdviceDetails));

            for (ReceiptAdviceDetail item : receiptAdviceDetails) {
                receiptAdviceDetailRepository.deleteById(item.getId());
            }
            receiptAdviceRepository.deleteById(id);

            updateExportingWarehousePaymentStatus(receiptAdviceFullDto, receiptAdviceDetailDtos);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ReceiptAdviceFullDto receiptAdviceFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(receiptAdviceFullDto.getCustomer().getId());
        ids.add(receiptAdviceFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (receiptAdviceFullDto.getCustomer() != null && receiptAdviceFullDto.getCustomer().getId() != null) {
                if (receiptAdviceFullDto.getCustomer().getId().equals(customer.getId())) {
                    receiptAdviceFullDto.setCustomer(customer);
                }
            }

            if (receiptAdviceFullDto.getTransactionCustomer() != null && receiptAdviceFullDto.getTransactionCustomer().getId() != null) {
                if (receiptAdviceFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    receiptAdviceFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ReceiptAdviceFullDto receiptAdviceFullDto)
            throws IOException, JAXBException {
        List<ReceiptAdviceDetailFullDto> receiptAdviceDetailFulls = receiptAdviceFullDto.getReceiptAdviceDetails();
        List<String> ids = new ArrayList<>();
        for(ReceiptAdviceDetailFullDto item : receiptAdviceDetailFulls) {
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
            for(ReceiptAdviceDetailFullDto item : receiptAdviceDetailFulls) {
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
    CompletableFuture<Boolean> saveReceiptAdviceDetail(ReceiptAdviceFullDto receiptAdviceFullDto) {
        List<ReceiptAdviceDetail> oldReceiptAdviceDetails = receiptAdviceDetailRepository.getByReceiptAdviceId(receiptAdviceFullDto.getId());
        List<ReceiptAdviceDetailFullDto> receiptAdviceDetails = receiptAdviceFullDto.getReceiptAdviceDetails();
        ReceiptAdvice receiptAdvice = IReceiptAdviceDtoMapper.INSTANCE.toReceiptAdvice(receiptAdviceFullDto);

        for (ReceiptAdviceDetailFullDto item : receiptAdviceDetails) {
            ReceiptAdviceDetail receiptAdviceDetail = IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetail(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                receiptAdviceDetail.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getExportingWarehouse() != null
                    && (item.getExportingWarehouse().getId() == null || item.getExportingWarehouse().getId().isEmpty())) {
                receiptAdviceDetail.setExportingWarehouse(null);
            }
            receiptAdviceDetail.setReceiptAdvice(receiptAdvice);
            ReceiptAdviceDetail created = receiptAdviceDetailRepository.save(receiptAdviceDetail);
            item.setId(created.getId());
        }

        if (oldReceiptAdviceDetails != null) {
            for (ReceiptAdviceDetail item : oldReceiptAdviceDetails) {
                int index = receiptAdviceDetails.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    receiptAdviceDetailRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    CompletableFuture<Boolean> updateExportingWarehousePaymentStatus(ReceiptAdviceFullDto receiptAdviceFullDto, List<ReceiptAdviceDetailDto> oldReceiptAdviceDetails) {
        List<ReceiptAdviceDetail> receiptAdviceDetails = receiptAdviceDetailRepository.getHasExporting(receiptAdviceFullDto.getId());
        List<String> exportIds = receiptAdviceDetails.stream().map(t -> t.getExportingWarehouse().getId()).distinct().collect(Collectors.toList());

        for (String id : exportIds) {
            Double total = exportingTransactionRepository.getTotal(id);
            Double totalReceipt = receiptDetailRepository.getTotalByExportingWarehouseId(id);
            Double totalReceiptAdvice = receiptAdviceDetailRepository.getTotalByExportingWarehouseId(id);
            Double totalReturn = importingReturnTransactionRepository.getTotalByExportingWarehouseId(id);
            totalReceipt = totalReceipt != null ? totalReceipt : 0;
            totalReceiptAdvice = totalReceiptAdvice != null ? totalReceiptAdvice : 0;
            totalReturn = totalReturn != null ? totalReturn : 0;
            totalReceiptAdvice += totalReceipt;
            totalReceiptAdvice += totalReturn;

            PaymentStatusEnum paymentStatusEnum = totalReceiptAdvice >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
            if (exportingWarehouse.getPaymentStatus() != paymentStatusEnum) {
                exportingWarehouse.setPaymentStatus(paymentStatusEnum);
                exportingWarehouseRepository.save(exportingWarehouse);
            }
        }

        if (oldReceiptAdviceDetails != null && oldReceiptAdviceDetails.size() > 0) {
            for (ReceiptAdviceDetailDto item : oldReceiptAdviceDetails) {
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
