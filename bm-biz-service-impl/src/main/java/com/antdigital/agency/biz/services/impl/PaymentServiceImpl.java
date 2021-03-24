package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IPaymentDao;
import com.antdigital.agency.dal.data.*;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IPaymentService;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements IPaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    IPaymentRepository paymentRepository;

    @Autowired
    IPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private IPaymentAdviceDetailRepository paymentAdviceDetailRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IImportingWarehouseRepository importingWarehouseRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Autowired
    private IPaymentDao paymentDao;

    @Autowired
    private IReferralBonusRepository referralBonusRepository;

    @Autowired
    private  ISellingBonusRepository sellingBonusRepository;

    @Override
    public PaymentSearchDto search(HttpServletRequest request, PaymentSearchDto paymentSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(paymentSearchDto.getCustomerCode()!=null && !paymentSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, paymentSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }

        SearchResult<List<PaymentSearchDetail>> result = paymentDao.search(
                paymentSearchDto.getCode(),
                paymentSearchDto.getNumber(),
                paymentSearchDto.getCustomerAddress(),
                paymentSearchDto.getDescription(),
                paymentSearchDto.getNote(),
                customerIds,
                paymentSearchDto.getStartDate(),
                paymentSearchDto.getEndDate(),
                paymentSearchDto.getCreatedDateSort(),
                paymentSearchDto.getStartNumber(),
                paymentSearchDto.getEndNumber(),
                paymentSearchDto.getCurrentPage(),
                paymentSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(PaymentSearchDetail paymentSearchDetail: result.getResult()){
            customerIds.add(paymentSearchDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(PaymentSearchDetail paymentSearchDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(paymentSearchDetail.getCustomerId())).findFirst().orElse(null);
            if (customerModels != null) {
                paymentSearchDetail.setCustomerCode(customerModels.getCode());
                paymentSearchDetail.setCustomerName(customerModels.getFullName());
            }
        }

        paymentSearchDto.setTotalRecords(result.getTotalRecords());
        paymentSearchDto.setResult(IPaymentDtoMapper.INSTANCE.toPaymentSearchDetailDtoList(result.getResult()));
        return paymentSearchDto;
    }

    @Override
    public List<PaymentDto> findAll(String agencyId) {
        List<Payment> payments = paymentRepository.findAllByAgencyId(agencyId);
        return IPaymentDtoMapper.INSTANCE.toPaymentDtoList(payments);
    }

    @Override
    public BaseSearchDto<List<PaymentDto>> findAll(BaseSearchDto<List<PaymentDto>> searchDto, String agencyId) {
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

        Page<Payment> page = paymentRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IPaymentDtoMapper.INSTANCE.toPaymentDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public PaymentDto getById(String id) {
        Payment payment = paymentRepository.findById(id).get();
        return IPaymentDtoMapper.INSTANCE.toPaymentDto(payment);
    }

    @Override
    public PaymentFullDto getFullById(HttpServletRequest request, String id) throws IOException, JAXBException {
        Payment payment = paymentRepository.findById(id).get();
        PaymentFullDto paymentFullDto = IPaymentDtoMapper.INSTANCE.toPaymentFullDto(payment);
        List<PaymentDetail> paymentDetails = paymentDetailRepository.getByPaymentId(id);
        paymentFullDto.setPaymentDetails(IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailFullDtoList(paymentDetails));

        CompletableFuture<Boolean> getCustomers = getCustomers(request, paymentFullDto);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, paymentFullDto);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting
        );

        return paymentFullDto;
    }

    @Override
    public String getPaymentNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Payment result = paymentRepository.getPaymentNumber(sdf.parse(createdDate), agencyId);
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
    public PaymentDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        Payment payment = paymentRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IPaymentDtoMapper.INSTANCE.toPaymentDto(payment);
    }

    @Override
    public int countByImportingId(String exportId) {
        int count = paymentDetailRepository.countByImportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public PaymentFullDto insert(PaymentFullDto paymentFullDto) {
        try {
            Payment payment = IPaymentDtoMapper.INSTANCE.toPayment(paymentFullDto);
            payment.setId(UUIDHelper.generateType4UUID().toString());
            paymentRepository.save(payment);

            paymentFullDto.setId(payment.getId());
            CompletableFuture<Boolean> savePaymentDetail = savePaymentDetail(paymentFullDto);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(paymentFullDto, null);
            CompletableFuture.allOf (
                    savePaymentDetail,
                    updateImportingWarehouseStatus
            );

            return paymentFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentFullDto insertPaymentCommission(PaymentFullDto paymentFullDto) {
        try {
            Payment payment = IPaymentDtoMapper.INSTANCE.toPayment(paymentFullDto);
            payment.setId(UUIDHelper.generateType4UUID().toString());
            paymentRepository.save(payment);

            paymentFullDto.setId(payment.getId());
            CompletableFuture<Boolean> savePaymentDetail = savePaymentDetail(paymentFullDto);
            CompletableFuture<Boolean> updateBonusStatus = updateBonusStatus(paymentFullDto, null);
            CompletableFuture.allOf (
                    savePaymentDetail,
                    updateBonusStatus
            );

            return paymentFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentFullDto update(PaymentFullDto paymentFullDto) {
        try {
            List<PaymentDetail> paymentDetails = paymentDetailRepository.getByPaymentId(paymentFullDto.getId());
            List<PaymentDetailDto> paymentDetailDtos = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailDtoList(paymentDetails);
            Payment payment = IPaymentDtoMapper.INSTANCE.toPayment(paymentFullDto);
            paymentRepository.save(payment);

            CompletableFuture<Boolean> savePaymentDetail = savePaymentDetail(paymentFullDto);
            CompletableFuture<Boolean> updateImportingWarehouseStatus = updateImportingWarehouseStatus(paymentFullDto, paymentDetailDtos);
            CompletableFuture.allOf (
                    savePaymentDetail,
                    updateImportingWarehouseStatus
            );
            return paymentFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PaymentFullDto updatePaymentCommission(PaymentFullDto paymentFullDto) {
        try {
            List<PaymentDetail> paymentDetails = paymentDetailRepository.getByPaymentId(paymentFullDto.getId());
            List<PaymentDetailDto> paymentDetailDtos = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailDtoList(paymentDetails);
            Payment payment = IPaymentDtoMapper.INSTANCE.toPayment(paymentFullDto);
            paymentRepository.save(payment);

            CompletableFuture<Boolean> savePaymentDetail = savePaymentDetail(paymentFullDto);
            CompletableFuture<Boolean> updateBonusStatus = updateBonusStatus(paymentFullDto, paymentDetailDtos);
            CompletableFuture.allOf (
                    savePaymentDetail,
                    updateBonusStatus
            );
            return paymentFullDto;
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
            Payment payment = paymentRepository.findById(id).get();
            List<PaymentDetail> paymentDetails = paymentDetailRepository.getByPaymentId(id);
            List<PaymentDetailDto> paymentDetailDtos = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailDtoList(paymentDetails);
            PaymentFullDto paymentFullDto = IPaymentDtoMapper.INSTANCE.toPaymentFullDto(payment);
            paymentFullDto.setPaymentDetails(IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailFullDtoList(paymentDetails));

            for (PaymentDetail item : paymentDetails) {
                paymentDetailRepository.deleteById(item.getId());
            }
            paymentRepository.deleteById(id);

            updateImportingWarehouseStatus(paymentFullDto, paymentDetailDtos);
            updateBonusStatus(paymentFullDto, paymentDetailDtos);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public List<MonthCostDetailDto> getMonthCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        //get Account 6421
        String codeAccount = "6421";
        AccountingTableModel accountingTableModel = warehouseRequestService.getAccounting(request, codeAccount);
        List<MonthCostDetail> monthCostDetailPayments = paymentDao.getMonthCost(fromDateNew, toDateNew, accountingTableModel.getId(), agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromMonth = fromCal.get(Calendar.MONTH) + 1;
        Integer toMonth =  toCal.get(Calendar.MONTH) + 1;
        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear =  toCal.get(Calendar.YEAR);

        if (fromYear < toYear) {
            toMonth = 12;
        }
        List<MonthCostDetail> monthCostDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            if (fromYear - toYear == 0){
                toMonth =  toCal.get(Calendar.MONTH) + 1;
            }
            while(fromMonth <= toMonth){
                MonthCostDetail monthCostDetail = new MonthCostDetail();
                monthCostDetail.setMonthDate(fromMonth);
                monthCostDetail.setYearDate(fromYear);
                monthCostDetail.setTotal(0);
                monthCostDetailList.add(monthCostDetail);
                fromMonth += 1;
            }
            fromMonth = 1;
            fromYear += 1;
        }
        for(MonthCostDetail monthCostDetail: monthCostDetailPayments){
            monthCostDetailList.stream().filter(item -> item.getMonthDate() == monthCostDetail.getMonthDate()
                    && item.getYearDate() == monthCostDetail.getYearDate()).findFirst().orElseThrow().setTotal(monthCostDetail.getTotal());
        }
        List<MonthCostDetailDto> monthCostDetailDtoList = IMonthCostDetailDtoMapper.INSTANCE.toMonthCostDtoList(monthCostDetailList);
        return monthCostDetailDtoList;
    }

    @Override
    public List<YearCostDetailDto> getYearCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        //get Account 6421
        String codeAccount = "6421";
        AccountingTableModel accountingTableModel = warehouseRequestService.getAccounting(request, codeAccount);
        List<YearCostDetail> yearCostDetailPayments = paymentDao.getYearCost(fromDateNew, toDateNew, accountingTableModel.getId(), agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear = toCal.get(Calendar.YEAR);

        List<YearCostDetail> yearCostDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            YearCostDetail yearCostDetail = new YearCostDetail();
            yearCostDetail.setYear(fromYear);
            yearCostDetail.setTotal(0);
            yearCostDetailList.add(yearCostDetail);
            fromYear += 1;
        }

        for(YearCostDetail yearCostDetail: yearCostDetailPayments) {
            List<YearCostDetail> detailLst = yearCostDetailList.stream().filter(item -> item.getYear() == yearCostDetail.getYear()).collect(Collectors.toList());
            for (YearCostDetail temp : detailLst) {
                temp.setTotal(yearCostDetail.getTotal());
            }
        }
        List<YearCostDetailDto> yearCostDetailDtos = IYearCostDetailDtoMapper.INSTANCE.toYearCostDtoList(yearCostDetailList);

        return yearCostDetailDtos;
    }

    @Override
    public List<DateCostDetailDto> getDateCost(HttpServletRequest request, RangeDateDto rangeDateDto, String agencyId) throws IOException, JAXBException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        //get Account 6421
        String codeAccount = "6421";
        AccountingTableModel accountingTableModel = warehouseRequestService.getAccounting(request, codeAccount);
        List<DateCostDetail> dateCostDetailPayments = paymentDao.getDateCost(fromDateNew, toDateNew, accountingTableModel.getId(), agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromDate = fromCal.get(Calendar.DAY_OF_MONTH);
        Integer toDate =  toCal.get(Calendar.DAY_OF_MONTH);
        Integer month = fromCal.get(Calendar.MONTH) + 1;
        Integer year = fromCal.get(Calendar.YEAR);


        List<DateCostDetail> dateCostDetailList = new ArrayList<>();

        while(fromDate <= toDate){
            DateCostDetail dateCostDetail = new DateCostDetail();
            dateCostDetail.setDate(fromDate);
            dateCostDetail.setMonth(month);
            dateCostDetail.setYear(year);
            dateCostDetail.setTotal(0);
            dateCostDetailList.add(dateCostDetail);
            fromDate += 1;
        }

        for(DateCostDetail dateCostDetail: dateCostDetailPayments){
            dateCostDetailList.stream().filter(item -> item.getDate() == dateCostDetail.getDate()).findFirst().orElseThrow().setTotal(dateCostDetail.getTotal());
        }
        List<DateCostDetailDto> dateCostDetailDto = IDateCostDetailDtoMapper.INSTANCE.toDateCostDtoList(dateCostDetailList);
        return dateCostDetailDto;
    }

    @Override
    @Transactional
    public List<PaymentDetailDto> getByRefferalBonusId(String id) {
        List<PaymentDetail> paymentDetails = paymentDetailRepository.getByRefferalBonusId(id);
        return IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailDtoList(paymentDetails);
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, PaymentFullDto paymentFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(paymentFullDto.getCustomer().getId());
        ids.add(paymentFullDto.getTransactionCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (paymentFullDto.getCustomer() != null && paymentFullDto.getCustomer().getId() != null) {
                if (paymentFullDto.getCustomer().getId().equals(customer.getId())) {
                    paymentFullDto.setCustomer(customer);
                }
            }

            if (paymentFullDto.getTransactionCustomer() != null && paymentFullDto.getTransactionCustomer().getId() != null) {
                if (paymentFullDto.getTransactionCustomer().getId().equals(customer.getId())) {
                    paymentFullDto.setTransactionCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, PaymentFullDto paymentFullDto)
            throws IOException, JAXBException {
        List<PaymentDetailFullDto> paymentDetailFulls = paymentFullDto.getPaymentDetails();
        List<String> ids = new ArrayList<>();
        for(PaymentDetailFullDto item : paymentDetailFulls) {
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
            for(PaymentDetailFullDto item : paymentDetailFulls) {
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
    CompletableFuture<Boolean> savePaymentDetail(PaymentFullDto paymentFullDto) {
        List<PaymentDetail> oldPaymentDetails = paymentDetailRepository.getByPaymentId(paymentFullDto.getId());
        List<PaymentDetailFullDto> paymentDetails = paymentFullDto.getPaymentDetails();
        Payment payment = IPaymentDtoMapper.INSTANCE.toPayment(paymentFullDto);

        for (PaymentDetailFullDto item : paymentDetails) {
            PaymentDetail paymentDetail = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetail(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                paymentDetail.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getImportingWarehouse() != null
                    && (item.getImportingWarehouse().getId() == null || item.getImportingWarehouse().getId().isEmpty())) {
                paymentDetail.setImportingWarehouse(null);
            }
            if (item.getReferralBonus() != null
                    && (item.getReferralBonus().getId() == null || item.getReferralBonus().getId().isEmpty())) {
                paymentDetail.setReferralBonus(null);
            }
            if (item.getSellingBonus() != null
                    && (item.getSellingBonus().getId() == null || item.getSellingBonus().getId().isEmpty())) {
                paymentDetail.setSellingBonus(null);
            }
            paymentDetail.setPayment(payment);
            PaymentDetail created = paymentDetailRepository.save(paymentDetail);
            item.setId(created.getId());
        }

        if (oldPaymentDetails != null) {
            for (PaymentDetail item : oldPaymentDetails) {
                int index = paymentDetails.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    paymentDetailRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> updateImportingWarehouseStatus(PaymentFullDto paymentFullDto, List<PaymentDetailDto> oldPaymentDetails) {
        List<PaymentDetail> paymentDetails = paymentDetailRepository.getHasImporting(paymentFullDto.getId());
        List<String> importIds = paymentDetails.stream().map(t -> t.getImportingWarehouse().getId()).distinct().collect(Collectors.toList());

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

        if (oldPaymentDetails != null && oldPaymentDetails.size() > 0) {
            for (PaymentDetailDto item : oldPaymentDetails) {
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
    CompletableFuture<Boolean> updateBonusStatus(PaymentFullDto paymentFullDto, List<PaymentDetailDto> oldPaymentDetails) {
        List<PaymentDetail> paymentSellingBonuses = paymentDetailRepository.getHasSellingBonus(paymentFullDto.getId());
        List<PaymentDetail> paymentReferralBonuses = paymentDetailRepository.getHasReferralBonus(paymentFullDto.getId());
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

        if (oldPaymentDetails != null && oldPaymentDetails.size() > 0) {
            for (PaymentDetailDto item : oldPaymentDetails) {
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
