package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IReceiptDao;
import com.antdigital.agency.dal.data.ReceiptSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IReceiptDetailDtoMapper;
import com.antdigital.agency.mappers.IReceiptDtoMapper;
import com.antdigital.agency.services.IReceiptService;
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
public class ReceiptServiceImpl implements IReceiptService {
    private static final Logger logger = LoggerFactory.getLogger(ReceiptServiceImpl.class);

    @Autowired
    private IReceiptRepository receiptRepository;

    @Autowired
    private IReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private IReceiptAdviceDetailRepository receiptAdviceDetailRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IReceiptDao receiptDao;

    @Override
    public ReceiptSearchDto search(HttpServletRequest request, ReceiptSearchDto receiptSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(receiptSearchDto.getCustomerCode()!=null && !receiptSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, receiptSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }

        SearchResult<List<ReceiptSearchDetail>> result = receiptDao.search(
                receiptSearchDto.getCode(),
                receiptSearchDto.getNumber(),
                receiptSearchDto.getCustomerAddress(),
                receiptSearchDto.getDescription(),
                receiptSearchDto.getNote(),
                customerIds,
                receiptSearchDto.getStartDate(),
                receiptSearchDto.getEndDate(),
                receiptSearchDto.getCreatedDateSort(),
                receiptSearchDto.getStartNumber(),
                receiptSearchDto.getEndNumber(),
                receiptSearchDto.getCurrentPage(),
                receiptSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(ReceiptSearchDetail receiptSearchDetail: result.getResult()){
            customerIds.add(receiptSearchDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(ReceiptSearchDetail receiptSearchDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(receiptSearchDetail.getCustomerId())).findFirst().get();
            receiptSearchDetail.setCustomerCode(customerModels.getCode());
            receiptSearchDetail.setCustomerName(customerModels.getFullName());
        }

        receiptSearchDto.setTotalRecords(result.getTotalRecords());
        receiptSearchDto.setResult(IReceiptDtoMapper.INSTANCE.toReceiptSearchDetailDtoList(result.getResult()));
        return receiptSearchDto;
    }

    @Override
    public List<ReceiptDto> findAll(String agencyId) {
        List<Receipt> receipts = receiptRepository.findAllByAgencyId(agencyId);
        return IReceiptDtoMapper.INSTANCE.toReceiptDtoList(receipts);
    }

    @Override
    public BaseSearchDto<List<ReceiptDto>> findAll(BaseSearchDto<List<ReceiptDto>> searchDto, String agencyId) {
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

        Page<Receipt> page = receiptRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IReceiptDtoMapper.INSTANCE.toReceiptDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ReceiptDto getById(String id) {
        Receipt receipt = receiptRepository.findById(id).get();
        return IReceiptDtoMapper.INSTANCE.toReceiptDto(receipt);
    }

    @Override
    public ReceiptFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        Receipt receipt = receiptRepository.findById(id).get();
        ReceiptFullDto receiptFullDto = IReceiptDtoMapper.INSTANCE.toReceiptFullDto(receipt);
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.getByReceiptId(id);
        receiptFullDto.setReceiptDetails(IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailFullDtoList(receiptDetails));

        CompletableFuture<Boolean> getCustomers = getCustomers(request, receiptFullDto);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, receiptFullDto);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting
        );

        return receiptFullDto;
    }

    @Override
    public String getReceiptNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Receipt result = receiptRepository.getReceiptNumber(sdf.parse(createdDate), agencyId);
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
    public ReceiptDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        Receipt receipt = receiptRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IReceiptDtoMapper.INSTANCE.toReceiptDto(receipt);
    }

    @Override
    public int countByExportId(String exportId) {
        int count = receiptDetailRepository.countByExportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public ReceiptFullDto insert(ReceiptFullDto receiptFullDto) {
        try {
            Receipt receipt = IReceiptDtoMapper.INSTANCE.toReceipt(receiptFullDto);
            receipt.setId(UUIDHelper.generateType4UUID().toString());
            receiptRepository.save(receipt);

            receiptFullDto.setId(receipt.getId());
            CompletableFuture<Boolean> saveReceiptDetail = saveReceiptDetail(receiptFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(receiptFullDto, null);

            CompletableFuture.allOf (
                    saveReceiptDetail,
                    updateExportingWarehousePaymentStatus
            );

            updateExportingWarehousePaymentStatus(receiptFullDto, null);

            return receiptFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public ReceiptFullDto update(ReceiptFullDto receiptFullDto) {
        try {
            List<ReceiptDetail> receiptDetails = receiptDetailRepository.getByReceiptId(receiptFullDto.getId());
            List<ReceiptDetailDto> receiptDetailDtos = IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailDtoList(receiptDetails);
            Receipt receipt = IReceiptDtoMapper.INSTANCE.toReceipt(receiptFullDto);

            receiptRepository.save(receipt);

            CompletableFuture<Boolean> saveReceiptDetail = saveReceiptDetail(receiptFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(receiptFullDto, receiptDetailDtos);

            CompletableFuture.allOf (
                    saveReceiptDetail,
                    updateExportingWarehousePaymentStatus
            );

            return receiptFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        try {
            Receipt receipt = receiptRepository.findById(id).get();
            List<ReceiptDetail> receiptDetails = receiptDetailRepository.getByReceiptId(id);
            List<ReceiptDetailDto> receiptDetailDtos = IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailDtoList(receiptDetails);
            ReceiptFullDto receiptFullDto = IReceiptDtoMapper.INSTANCE.toReceiptFullDto(receipt);
            receiptFullDto.setReceiptDetails(IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailFullDtoList(receiptDetails));

            for (ReceiptDetail item : receiptDetails) {
                receiptDetailRepository.deleteById(item.getId());
            }
            receiptRepository.deleteById(id);

            updateExportingWarehousePaymentStatus(receiptFullDto, receiptDetailDtos);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, ReceiptFullDto receiptFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(receiptFullDto.getCustomer().getId());
        ids.add(receiptFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (receiptFullDto.getCustomer() != null && receiptFullDto.getCustomer().getId() != null) {
                if (receiptFullDto.getCustomer().getId().equals(customer.getId())) {
                    receiptFullDto.setCustomer(customer);
                }
            }

            if (receiptFullDto.getTransactionCustomer() != null && receiptFullDto.getTransactionCustomer().getId() != null) {
                if (receiptFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    receiptFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, ReceiptFullDto receiptFullDto)
            throws IOException, JAXBException {
        List<ReceiptDetailFullDto> receiptDetailFulls = receiptFullDto.getReceiptDetails();
        List<String> ids = new ArrayList<>();
        for(ReceiptDetailFullDto item : receiptDetailFulls) {
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
            for(ReceiptDetailFullDto item : receiptDetailFulls) {
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
    CompletableFuture<Boolean> saveReceiptDetail(ReceiptFullDto receiptFullDto) {
        List<ReceiptDetail> oldReceiptDetails = receiptDetailRepository.getByReceiptId(receiptFullDto.getId());
        List<ReceiptDetailFullDto> receiptDetails = receiptFullDto.getReceiptDetails();
        Receipt receipt = IReceiptDtoMapper.INSTANCE.toReceipt(receiptFullDto);

        for (ReceiptDetailFullDto item : receiptDetails) {
            ReceiptDetail receiptDetail = IReceiptDetailDtoMapper.INSTANCE.toReceiptDetail(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                receiptDetail.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getExportingWarehouse() != null
                    && (item.getExportingWarehouse().getId() == null || item.getExportingWarehouse().getId().isEmpty())) {
                receiptDetail.setExportingWarehouse(null);
            }
            receiptDetail.setReceipt(receipt);
            ReceiptDetail created = receiptDetailRepository.save(receiptDetail);
            item.setId(created.getId());
        }

        if (oldReceiptDetails != null) {
            for (ReceiptDetail item : oldReceiptDetails) {
                int index = receiptDetails.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    receiptDetailRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    CompletableFuture<Boolean> updateExportingWarehousePaymentStatus(ReceiptFullDto receiptFullDto, List<ReceiptDetailDto> oldReceiptDetails) {
        List<ReceiptDetail> receiptDetails = receiptDetailRepository.getHasExporting(receiptFullDto.getId());
        List<String> exportIds = receiptDetails.stream().map(t -> t.getExportingWarehouse().getId()).distinct().collect(Collectors.toList());

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

        if (oldReceiptDetails != null && oldReceiptDetails.size() > 0) {
            for (ReceiptDetailDto item : oldReceiptDetails) {
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
