package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IPaymentAdviceDao;
import com.antdigital.agency.dal.data.PaymentAdviceSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.PaymentAdviceSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IPaymentAdviceDetailDtoMapper;
import com.antdigital.agency.mappers.IPaymentAdviceDtoMapper;
import com.antdigital.agency.mappers.IPaymentDetailDtoMapper;
import com.antdigital.agency.mappers.IPaymentDtoMapper;
import com.antdigital.agency.services.IPaymentAdviceService;
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
public class PaymentAdviceServiceImpl implements IPaymentAdviceService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentAdviceServiceImpl.class);

    @Autowired
    IPaymentAdviceRepository paymentAdviceRepository;

    @Autowired
    IPaymentAdviceDetailRepository paymentAdviceDetailRepository;

    @Autowired
    IPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IImportingWarehouseRepository importingWarehouseRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Autowired
    private IPaymentAdviceDao paymentAdviceDao;

    @Autowired
    private IReferralBonusRepository referralBonusRepository;

    @Autowired
    private  ISellingBonusRepository sellingBonusRepository;


    @Override
    public PaymentAdviceSearchDto search(HttpServletRequest request, PaymentAdviceSearchDto paymentAdviceSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(paymentAdviceSearchDto.getCustomerCode()!=null && !paymentAdviceSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, paymentAdviceSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }

        SearchResult<List<PaymentAdviceSearchDetail>> result = paymentAdviceDao.search(
                paymentAdviceSearchDto.getCode(),
                paymentAdviceSearchDto.getNumber(),
                paymentAdviceSearchDto.getCustomerAddress(),
                paymentAdviceSearchDto.getDescription(),
                paymentAdviceSearchDto.getNote(),
                customerIds,
                paymentAdviceSearchDto.getStartDate(),
                paymentAdviceSearchDto.getEndDate(),
                paymentAdviceSearchDto.getCreatedDateSort(),
                paymentAdviceSearchDto.getStartNumber(),
                paymentAdviceSearchDto.getEndNumber(),
                paymentAdviceSearchDto.getCurrentPage(),
                paymentAdviceSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(PaymentAdviceSearchDetail paymentAdviceSearchDetail: result.getResult()){
            customerIds.add(paymentAdviceSearchDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(PaymentAdviceSearchDetail paymentAdviceSearchDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(paymentAdviceSearchDetail.getCustomerId())).findFirst().orElse(null);
            if(customerModels!=null){
                paymentAdviceSearchDetail.setCustomerCode(customerModels.getCode());
                paymentAdviceSearchDetail.setCustomerName(customerModels.getFullName());
            }
        }

        paymentAdviceSearchDto.setTotalRecords(result.getTotalRecords());
        paymentAdviceSearchDto.setResult(IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceSearchDetailDtoList(result.getResult()));
        return paymentAdviceSearchDto;
    }

    @Override
    public List<PaymentAdviceDto> findAll(String agencyId) {
        List<PaymentAdvice> paymentAdvices = paymentAdviceRepository.findAllByAgencyId(agencyId);
        return IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceDtoList(paymentAdvices);
    }

    @Override
    public PaymentAdviceDto getById(String id) {
        PaymentAdvice paymentAdvice = paymentAdviceRepository.findById(id).get();
        return IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceDto(paymentAdvice);
    }

    @Override
    public PaymentAdviceFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        PaymentAdvice paymentAdvice = paymentAdviceRepository.findById(id).get();
        PaymentAdviceFullDto paymentAdviceFullDto = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceFullDto(paymentAdvice);
        List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(id);
        paymentAdviceFullDto.setPaymentAdviceDetails(IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailFullDtoList(paymentAdviceDetails));

        CompletableFuture<Boolean> getCustomers = getCustomers(request, paymentAdviceFullDto);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, paymentAdviceFullDto);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting
        );

        return paymentAdviceFullDto;
    }

    @Override
    public String getPaymentAdviceNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            PaymentAdvice result = paymentAdviceRepository.getPaymentAdviceNumber(sdf.parse(createdDate), agencyId);
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
    public PaymentAdviceDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        PaymentAdvice paymentAdvice = paymentAdviceRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceDto(paymentAdvice);
    }

    @Override
    public int countByImportingId(String exportId) {
        int count = paymentAdviceDetailRepository.countByImportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public PaymentAdviceFullDto insert(PaymentAdviceFullDto paymentAdviceFullDto) {
        try {
            PaymentAdvice paymentAdvice = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdvice(paymentAdviceFullDto);
            paymentAdvice.setId(UUIDHelper.generateType4UUID().toString());
            paymentAdviceRepository.save(paymentAdvice);

            paymentAdviceFullDto.setId(paymentAdvice.getId());
            CompletableFuture<Boolean> savePaymentAdviceDetail = savePaymentAdviceDetail(paymentAdviceFullDto);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(paymentAdviceFullDto, null);
            CompletableFuture.allOf (
                    savePaymentAdviceDetail,
                    updateImportingWarehouseStatus
            );

            return paymentAdviceFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentAdviceFullDto insertPaymentCommission(PaymentAdviceFullDto paymentAdviceFullDto) {
        try {
            PaymentAdvice paymentAdvice = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdvice(paymentAdviceFullDto);
            paymentAdvice.setId(UUIDHelper.generateType4UUID().toString());
            paymentAdviceRepository.save(paymentAdvice);

            paymentAdviceFullDto.setId(paymentAdvice.getId());
            CompletableFuture<Boolean> savePaymentAdviceDetail = savePaymentAdviceDetail(paymentAdviceFullDto);
            CompletableFuture<Boolean> updateBonusStatus = updateBonusStatus(paymentAdviceFullDto, null);
            CompletableFuture.allOf (
                    savePaymentAdviceDetail,
                    updateBonusStatus
            );

            return paymentAdviceFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentAdviceFullDto update(PaymentAdviceFullDto paymentAdviceFullDto) {
        try {
            List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(paymentAdviceFullDto.getId());
            List<PaymentAdviceDetailDto> paymentAdviceDetailDtos = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailDtoList(paymentAdviceDetails);
            PaymentAdvice paymentAdvice = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdvice(paymentAdviceFullDto);
            paymentAdviceRepository.save(paymentAdvice);

            CompletableFuture<Boolean> savePaymentAdviceDetail = savePaymentAdviceDetail(paymentAdviceFullDto);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(paymentAdviceFullDto, paymentAdviceDetailDtos);
            CompletableFuture.allOf (
                    savePaymentAdviceDetail,
                    updateImportingWarehouseStatus
            );


            return paymentAdviceFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentAdviceFullDto updatePaymentCommission(PaymentAdviceFullDto paymentAdviceFullDto) {
        try {
            List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(paymentAdviceFullDto.getId());
            List<PaymentAdviceDetailDto> paymentAdviceDetailDtos = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailDtoList(paymentAdviceDetails);
            PaymentAdvice paymentAdvice = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdvice(paymentAdviceFullDto);
            paymentAdviceRepository.save(paymentAdvice);

            CompletableFuture<Boolean> savePaymentAdviceDetail = savePaymentAdviceDetail(paymentAdviceFullDto);
            CompletableFuture<Boolean> updateBonusStatus = updateBonusStatus(paymentAdviceFullDto, paymentAdviceDetailDtos);
            CompletableFuture.allOf (
                    savePaymentAdviceDetail,
                    updateBonusStatus
            );
            return paymentAdviceFullDto;
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
            PaymentAdvice paymentAdvice = paymentAdviceRepository.findById(id).get();
            List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(id);
            List<PaymentAdviceDetailDto> paymentAdviceDetailDtos = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailDtoList(paymentAdviceDetails);
            PaymentAdviceFullDto paymentAdviceFullDto = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdviceFullDto(paymentAdvice);
            paymentAdviceFullDto.setPaymentAdviceDetails(IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailFullDtoList(paymentAdviceDetails));

            for (PaymentAdviceDetail item : paymentAdviceDetails) {
                paymentAdviceDetailRepository.deleteById(item.getId());
            }
            paymentAdviceRepository.deleteById(id);

            updateImportingWarehouseStatus(paymentAdviceFullDto, paymentAdviceDetailDtos);
            updateBonusStatus(paymentAdviceFullDto, paymentAdviceDetailDtos);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    @Transactional
    public List<PaymentAdviceDetailDto> getByRefferalBonusId(String id) {
        List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByRefferalBonusId(id);
        return IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailDtoList(paymentAdviceDetails);
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, PaymentAdviceFullDto paymentAdviceFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(paymentAdviceFullDto.getCustomer().getId());
        ids.add(paymentAdviceFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (paymentAdviceFullDto.getCustomer() != null && paymentAdviceFullDto.getCustomer().getId() != null) {
                if (paymentAdviceFullDto.getCustomer().getId().equals(customer.getId())) {
                    paymentAdviceFullDto.setCustomer(customer);
                }
            }

            if (paymentAdviceFullDto.getTransactionCustomer() != null && paymentAdviceFullDto.getTransactionCustomer().getId() != null) {
                if (paymentAdviceFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    paymentAdviceFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, PaymentAdviceFullDto paymentAdviceFullDto)
            throws IOException, JAXBException {
        List<PaymentAdviceDetailFullDto> paymentAdviceDetailFulls = paymentAdviceFullDto.getPaymentAdviceDetails();
        List<String> ids = new ArrayList<>();
        for(PaymentAdviceDetailFullDto item : paymentAdviceDetailFulls) {
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
            for(PaymentAdviceDetailFullDto item : paymentAdviceDetailFulls) {
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
    CompletableFuture<Boolean> savePaymentAdviceDetail(PaymentAdviceFullDto paymentAdviceFullDto) {
        List<PaymentAdviceDetail> oldPaymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(paymentAdviceFullDto.getId());
        List<PaymentAdviceDetailFullDto> paymentAdviceDetails = paymentAdviceFullDto.getPaymentAdviceDetails();
        PaymentAdvice paymentAdvice = IPaymentAdviceDtoMapper.INSTANCE.toPaymentAdvice(paymentAdviceFullDto);

        for (PaymentAdviceDetailFullDto item : paymentAdviceDetails) {
            PaymentAdviceDetail paymentAdviceDetail = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetail(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                paymentAdviceDetail.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getImportingWarehouse() != null
                    && (item.getImportingWarehouse().getId() == null || item.getImportingWarehouse().getId().isEmpty())) {
                paymentAdviceDetail.setImportingWarehouse(null);
            }
            if (item.getReferralBonus() != null
                    && (item.getReferralBonus().getId() == null || item.getReferralBonus().getId().isEmpty())) {
                paymentAdviceDetail.setReferralBonus(null);
            }
            if (item.getSellingBonus() != null
                    && (item.getSellingBonus().getId() == null || item.getSellingBonus().getId().isEmpty())) {
                paymentAdviceDetail.setSellingBonus(null);
            }
            paymentAdviceDetail.setPaymentAdvice(paymentAdvice);
            PaymentAdviceDetail created = paymentAdviceDetailRepository.save(paymentAdviceDetail);
            item.setId(created.getId());
        }

        if (oldPaymentAdviceDetails != null) {
            for (PaymentAdviceDetail item : oldPaymentAdviceDetails) {
                int index = paymentAdviceDetails.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    paymentAdviceDetailRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> updateImportingWarehouseStatus(PaymentAdviceFullDto paymentAdviceFullDto, List<PaymentAdviceDetailDto> oldPaymentAdviceDetails) {
        List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getHasImporting(paymentAdviceFullDto.getId());
        List<String> importIds = paymentAdviceDetails.stream().map(t -> t.getImportingWarehouse().getId()).distinct().collect(Collectors.toList());

        for (String id : importIds) {
            Double total = importingTransactionRepository.getTotal(id);
            Double totalPaymentAdvice = paymentAdviceDetailRepository.getTotalByImportingWarehouseId(id);
            Double totalPayment = paymentDetailRepository.getTotalByImportingWarehouseId(id);
            Double totalReturn = exportingReturnTransactionRepository.getTotalByImportingWarehouseId(id);

            totalPaymentAdvice = totalPaymentAdvice != null ? totalPaymentAdvice : 0;
            totalPayment = totalPayment != null ? totalPayment : 0;
            totalReturn = totalReturn != null ? totalReturn : 0;
            totalPaymentAdvice += totalPayment;
            totalPaymentAdvice += totalReturn;

            PaymentStatusEnum paymentStatusEnum = totalPaymentAdvice >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ImportingWarehouse importingWarehouse = importingWarehouseRepository.findById(id).get();
            if (importingWarehouse.getPaymentStatus() != paymentStatusEnum) {
                importingWarehouse.setPaymentStatus(paymentStatusEnum);
                importingWarehouseRepository.save(importingWarehouse);
            }
        }

        if (oldPaymentAdviceDetails != null && oldPaymentAdviceDetails.size() > 0) {
            for (PaymentAdviceDetailDto item : oldPaymentAdviceDetails) {
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

    @Async
    CompletableFuture<Boolean> updateBonusStatus(PaymentAdviceFullDto paymentAdviceFullDto, List<PaymentAdviceDetailDto> oldPaymentAdviceDetails) {
        List<PaymentAdviceDetail> paymentSellingBonuses = paymentAdviceDetailRepository.getHasSellingBonus(paymentAdviceFullDto.getId());
        List<PaymentAdviceDetail> paymentReferralBonuses = paymentAdviceDetailRepository.getHasReferralBonus(paymentAdviceFullDto.getId());
        List<String> referralBonusIds = paymentReferralBonuses.stream().map(t -> t.getReferralBonus().getId()).distinct().collect(Collectors.toList());
        List<String> sellingBonusIds = paymentSellingBonuses.stream().map(t -> t.getSellingBonus().getId()).distinct().collect(Collectors.toList());

        for (String id : referralBonusIds) {
            Double total = referralBonusRepository.getTotal(id);
            Double totalPayment = paymentDetailRepository.getTotalByReferralBonusId(id);
            Double totalPaymentAdvice = paymentAdviceDetailRepository.getTotalByReferralBonusId(id);

            totalPayment = totalPayment != null ? totalPayment : 0;
            totalPaymentAdvice = totalPaymentAdvice != null ? totalPaymentAdvice : 0;
            totalPayment += totalPaymentAdvice;

            PaymentStatusEnum paymentStatusEnum = totalPayment >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ReferralBonus referralBonus = referralBonusRepository.findById(id).get();
            if (referralBonus.getPaymentStatus() != paymentStatusEnum) {
                referralBonus.setPaymentStatus(paymentStatusEnum);
                referralBonusRepository.save(referralBonus);
            }
        }

        for (String id : sellingBonusIds) {
            Double total = sellingBonusRepository.getTotal(id);
            Double totalPayment = paymentDetailRepository.getTotalBySellingBonusId(id);
            Double totalPaymentAdvice = paymentAdviceDetailRepository.getTotalBySellingBonusId(id);

            totalPayment = totalPayment != null ? totalPayment : 0;
            totalPaymentAdvice = totalPaymentAdvice != null ? totalPaymentAdvice : 0;
            totalPayment += totalPaymentAdvice;

            PaymentStatusEnum paymentStatusEnum = totalPayment >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            SellingBonus sellingBonus = sellingBonusRepository.findById(id).get();
            if (sellingBonus.getPaymentStatus() != paymentStatusEnum) {
                sellingBonus.setPaymentStatus(paymentStatusEnum);
                sellingBonusRepository.save(sellingBonus);
            }
        }

        if (oldPaymentAdviceDetails != null && oldPaymentAdviceDetails.size() > 0) {
            for (PaymentAdviceDetailDto item : oldPaymentAdviceDetails) {
                if (item.getReferralBonus() == null || item.getReferralBonus().getId() == null) {
                    item.setReferralBonus(null);
                }
                if (item.getReferralBonus() != null) {
                    int index = referralBonusIds.indexOf(item.getReferralBonus().getId());
                    if (index == -1) {
                        ReferralBonus referralBonus = referralBonusRepository.findById(item.getReferralBonus().getId()).get();
                        if (referralBonus.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
                            referralBonus.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                            referralBonusRepository.save(referralBonus);
                        }
                    }
                }

                if (item.getSellingBonus() == null || item.getSellingBonus().getId() == null) {
                    item.setSellingBonus(null);
                }
                if (item.getSellingBonus() != null) {
                    int index1 = sellingBonusIds.indexOf(item.getSellingBonus().getId());
                    if (index1 == -1) {
                        SellingBonus sellingBonus = sellingBonusRepository.findById(item.getSellingBonus().getId()).get();
                        if (sellingBonus.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
                            sellingBonus.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                            sellingBonusRepository.save(sellingBonus);
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }
}
