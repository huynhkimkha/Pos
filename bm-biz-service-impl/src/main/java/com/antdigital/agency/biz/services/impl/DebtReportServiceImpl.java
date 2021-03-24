package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import com.antdigital.agency.core.models.warehouse.request.CustomerSearchModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.DebtReportSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IDebtReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.*;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.antdigital.agency.common.constant.Constant.DEFAULT_DEBT_ACCOUNT;

@Service
@WebListener
public class DebtReportServiceImpl extends RequestContextListener implements IDebtReportService {
    private static final Logger logger = LoggerFactory.getLogger(DebtReportServiceImpl.class);

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IPaymentDetailRepository paymentDetailRepository;

    @Autowired
    private IReceiptDetailRepository receiptDetailRepository;

    @Autowired
    private IPaymentAdviceDetailRepository paymentAdviceDetailRepository;

    @Autowired
    private IReceiptAdviceDetailRepository receiptAdviceDetailRepository;

    @Autowired
    private IDebtClearingDetailRepository debtClearingDetailRepository;

    @Autowired
    private IExportingReturnTransactionRepository exportingReturnTransactionRepository;

    @Autowired
    private IImportingReturnTransactionRepository importingReturnTransactionRepository;

    @Autowired
    private IMonthlyClosingBalanceRepository monthlyClosingBalanceRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Override
    public List<MonthlyClosingBalanceDto> getDebt(HttpServletRequest request, DebtReportSearchDto debtReportSearchDto) throws IOException, JAXBException {
        List<MonthlyClosingBalanceDto> result = new ArrayList<>();
        List<AccountingTableModel> defaultDebtAccounts = this.getAccountingByCodes(request, DEFAULT_DEBT_ACCOUNT);
        List<String> defaultDebtAccountIds = defaultDebtAccounts.stream().map(d -> d.getId()).collect(Collectors.toList());
        List<DebtReportDto> debtReportDtos = getDebtReports(request, debtReportSearchDto, true);
        debtReportDtos = debtReportDtos.stream().distinct().filter(d -> defaultDebtAccountIds.contains(d.getDefaultAccountId())).collect(Collectors.toList());
        if (debtReportDtos.stream().filter(
                d -> d.getDefaultAccountId().equals(debtReportSearchDto.getCustomer().getAccounting().getId())).count() <= 0) {
            MonthlyClosingBalanceDto monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
            monthlyClosingBalanceDto.setCustomerId(debtReportSearchDto.getCustomer().getId());
            monthlyClosingBalanceDto.setClosingDate(debtReportSearchDto.getToDate());
            monthlyClosingBalanceDto.setAccountingTable(debtReportSearchDto.getCustomer().getAccounting());
            MonthlyClosingBalance oldBalance = monthlyClosingBalanceRepository.getLatestClosingBalance(debtReportSearchDto.getCustomer().getId(), debtReportSearchDto.getAgencyId());
            if (oldBalance == null) {
                monthlyClosingBalanceDto.setDebitBalance(0D);
            } else {
                if (oldBalance.getDebitBalance() != null) {
                    monthlyClosingBalanceDto.setDebitBalance(oldBalance.getDebitBalance());
                } else {
                    monthlyClosingBalanceDto.setCreditBalance(oldBalance.getCreditBalance());
                }
            }
            result.add(monthlyClosingBalanceDto);
        }
        for (int i = 0; i < debtReportDtos.size(); i++) {
            DebtReportDto currentDebtReportDto = debtReportDtos.get(i);
            String currentAccountingId = currentDebtReportDto.getDefaultAccountId();
            if (i == debtReportDtos.size() - 1) {
                MonthlyClosingBalanceDto monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
                monthlyClosingBalanceDto.setCustomerId(debtReportSearchDto.getCustomer().getId());
                monthlyClosingBalanceDto.setClosingDate(debtReportSearchDto.getToDate());
                if (currentDebtReportDto.getDebitBalance() != null) {
                    monthlyClosingBalanceDto.setDebitBalance(currentDebtReportDto.getDebitBalance());
                } else {
                    monthlyClosingBalanceDto.setCreditBalance(currentDebtReportDto.getCreditBalance());
                }
                monthlyClosingBalanceDto.setAccountingTable(defaultDebtAccounts.stream().filter(a -> a.getId().equals(currentDebtReportDto.getDefaultAccountId())).findAny().orElse(null));
                result.add(monthlyClosingBalanceDto);
                return result;
            }
            if (!currentAccountingId.equals(debtReportDtos.get(i + 1).getDefaultAccountId())) {
                MonthlyClosingBalanceDto monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
                monthlyClosingBalanceDto.setCustomerId(debtReportSearchDto.getCustomer().getId());
                monthlyClosingBalanceDto.setClosingDate(debtReportSearchDto.getToDate());
                if (currentDebtReportDto.getDebitBalance() != null) {
                    monthlyClosingBalanceDto.setDebitBalance(currentDebtReportDto.getDebitBalance());
                } else {
                    monthlyClosingBalanceDto.setCreditBalance(currentDebtReportDto.getCreditBalance());
                }
                monthlyClosingBalanceDto.setAccountingTable(defaultDebtAccounts.stream().filter(a -> a.getId().equals(currentDebtReportDto.getDefaultAccountId())).findAny().orElse(null));
                result.add(monthlyClosingBalanceDto);
            }
        }
        return result;
    }

    @Override
    public List<DebtReportDto> getDebtReports(HttpServletRequest request, DebtReportSearchDto debtReportSearchDto, boolean isCollectForGetDebt)
            throws IOException, JAXBException {
        List<CustomerModel> listCustomer = Arrays.asList(debtReportSearchDto.getCustomer());

        if (listCustomer.get(0).getId() == null) {
            CustomerSearchModel customerSearchModel = new CustomerSearchModel();
            customerSearchModel.setCompanyId(debtReportSearchDto.getCompanyId());
            customerSearchModel.getCustomerGroup1().setId(debtReportSearchDto.getCustomerGroup1Id());
            customerSearchModel.getCustomerGroup2().setId(debtReportSearchDto.getCustomerGroup2Id());
            customerSearchModel.getCustomerGroup3().setId(debtReportSearchDto.getCustomerGroup3Id());
            listCustomer = warehouseRequestService.advancedSearch(request, customerSearchModel);
        }

        if (listCustomer != null && listCustomer.size() == 0) {
            return new ArrayList<>();
        }

        List<DebtReportDto> debtReports = isCollectForGetDebt ?
                collectDebtReportForGetDebtHandle(request, debtReportSearchDto, listCustomer) :
                collectDebtReportHandle(debtReportSearchDto, listCustomer);

        CompletableFuture<Boolean> collectAccountInfo = collectAccountInfo(request, debtReports);
        CompletableFuture<Boolean> collectMerchandiseInfo = collectMerchandiseInfo(request, debtReports);

        CompletableFuture.allOf (
                collectAccountInfo,
                collectMerchandiseInfo
        );

        return debtReports;
    }

    @Override
    @Transactional
    public void deleteMonthlyClosingBalance(Date toDate, String agencyId) {
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);
        Date closingDate = new Date(c.getTime().getTime());
        monthlyClosingBalanceRepository.deleteByClosingDate(closingDate, agencyId);
    }

    @Override
    public void saveMonthlyClosingBalance(HttpServletRequest request, Date fromDate, Date toDate, String agencyId) throws IOException, JAXBException {
        List<CustomerModel> customerModelList = warehouseRequestService.getAllCustomers(request);

        List<DebtReportDto> debtReportDtoList = new ArrayList<>();
        CompletableFuture<Boolean> getDebtReportSearchLst = collectDebtReport(debtReportDtoList, fromDate, toDate, customerModelList, null, agencyId);
        CompletableFuture.allOf(
                getDebtReportSearchLst
        );

        List<MonthlyClosingBalanceDto> monthlyClosingBalanceLst = getDebtPreviousMonth(fromDate, customerModelList, agencyId);
        for (CustomerModel c : customerModelList) {
            List<DebtReportDto> debtReportSelects = debtReportDtoList.stream().filter(d -> d.getCustomerId().equals(c.getId())).collect(Collectors.toList());
            List<MonthlyClosingBalanceDto> monthlyClosingBalanceSelects = monthlyClosingBalanceLst.stream().filter(m -> m.getCustomerId().equals(c.getId())).collect(Collectors.toList());
            MonthlyClosingBalanceDto lastMonthClosingBalanceDto = monthlyClosingBalanceSelects == null || monthlyClosingBalanceSelects.size() == 0 ? null : monthlyClosingBalanceSelects.get(0);

            if (c.getAccounting() == null) {
                continue;
            }
            MonthlyClosingBalanceDto monthlyClosingBalanceDto = calculateDebt(debtReportSelects, lastMonthClosingBalanceDto, c, c.getAccounting().getId());
            monthlyClosingBalanceDto.setId(UUIDHelper.generateType4UUID().toString());
            monthlyClosingBalanceDto.setClosingDate(toDate);
            monthlyClosingBalanceDto.setCustomerId(c.getId());
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(agencyId);
            monthlyClosingBalanceDto.setAgency(agencyDto);
            MonthlyClosingBalance monthlyClosingBalance = IMonthlyClosingBalanceDtoMapper.INSTANCE.toMonthlyClosingBalance(monthlyClosingBalanceDto);
            monthlyClosingBalanceRepository.save(monthlyClosingBalance);
        }
    }

    @Override
    public void updateMonthlyClosingBalance(HttpServletRequest request, Date createdDate, CustomerModel customer, String agencyId) throws IOException, JAXBException {
        LocalDate localDate = createdDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        long monthsBetween = ChronoUnit.MONTHS.between(localDate.withDayOfMonth(1), today.withDayOfMonth(1));

        //get first month last closing balance
        Calendar c3 = Calendar.getInstance();
        c3.setTime(createdDate);
        c3.add(Calendar.MONTH, -1);
        Date lastMonthDate = new Date(c3.getTime().getTime());
        MonthlyClosingBalance lastMonthClosingBalance = monthlyClosingBalanceRepository.getByCustomerIdAndClosingDate(customer.getId(), lastMonthDate, agencyId);
        MonthlyClosingBalanceDto lastMonthClosingBalanceDto = IMonthlyClosingBalanceDtoMapper.INSTANCE.toMonthlyClosingBalanceDto(lastMonthClosingBalance);

        List<CustomerModel> customerModelList = new ArrayList<>();
        customerModelList.add(customer);
        for(int i = 0; i<monthsBetween; i++){
            Calendar c1 = Calendar.getInstance();
            c1.setTime(createdDate);
            int complementDate = c1.get(Calendar.DAY_OF_MONTH);
            c1.add(Calendar.MONTH, 1+i);
            c1.add(Calendar.DATE, -complementDate);
            Date toDate = new Date(c1.getTime().getTime());

            Calendar c2 = Calendar.getInstance();
            c2.setTime(createdDate);
            c2.add(Calendar.MONTH, i);
            c2.add(Calendar.DATE, -complementDate + 1);
            Date fromDate = new Date(c2.getTime().getTime());

            MonthlyClosingBalance oldMonthlyClosingBalance = monthlyClosingBalanceRepository.getByCustomerIdAndClosingDate(customer.getId(), fromDate, agencyId);
            List<DebtReportDto> debtReportDtoList = new ArrayList<>();
            MonthlyClosingBalanceDto monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
            CompletableFuture<Boolean> getDebtReportSearchLst = collectDebtReport(debtReportDtoList, fromDate, toDate, customerModelList, customerModelList.get(0).getAccounting().getId(), agencyId);
            CompletableFuture.allOf(
                    getDebtReportSearchLst
            );
            monthlyClosingBalanceDto = calculateDebt(debtReportDtoList, lastMonthClosingBalanceDto, customer, customer.getAccounting().getId());
            monthlyClosingBalanceDto.setClosingDate(toDate);
            monthlyClosingBalanceDto.setCustomerId(customer.getId());
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(agencyId);
            monthlyClosingBalanceDto.setAgency(agencyDto);

            String id = oldMonthlyClosingBalance != null ? oldMonthlyClosingBalance .getId() : UUIDHelper.generateType4UUID().toString();
            monthlyClosingBalanceDto.setId(id);
            lastMonthClosingBalanceDto = new MonthlyClosingBalanceDto();
            lastMonthClosingBalanceDto.setDebitBalance(monthlyClosingBalanceDto.getDebitBalance());
            lastMonthClosingBalanceDto.setCreditBalance(monthlyClosingBalanceDto.getCreditBalance());

            MonthlyClosingBalance monthlyClosingBalance = IMonthlyClosingBalanceDtoMapper.INSTANCE.toMonthlyClosingBalance(monthlyClosingBalanceDto);
            monthlyClosingBalanceRepository.save(monthlyClosingBalance);
        }
    }

    private List<DebtReportDto> collectDebtReportForGetDebtHandle(HttpServletRequest request, DebtReportSearchDto debtReportSearchDto, List<CustomerModel> customerModels) throws IOException, JAXBException {
        List<DebtReportDto> debtReportSearchLst = new ArrayList<>();
        CompletableFuture<Boolean> getDebtReportSearchLst = collectDebtReport(debtReportSearchLst, debtReportSearchDto.getFromDate(), debtReportSearchDto.getToDate(), customerModels, debtReportSearchDto.getDebtAccount().getId(), debtReportSearchDto.getAgencyId());
        CompletableFuture<Boolean> getDebtReportNotDefaultSearchLst = collectDebtReport(debtReportSearchLst, null, null, customerModels, debtReportSearchDto.getDebtAccount().getId(), debtReportSearchDto.getAgencyId());
        CompletableFuture.allOf(
                getDebtReportSearchLst,
                getDebtReportNotDefaultSearchLst
        );
        List<MonthlyClosingBalanceDto> monthlyClosingBalanceLst = getDebtPreviousMonth(debtReportSearchDto.getFromDate(), customerModels, debtReportSearchDto.getAgencyId());

        List<String> accountingIds = debtReportSearchLst.stream().map(d -> d.getDefaultAccountId()).distinct().filter(i -> i != null).collect(Collectors.toList());
        List<DebtReportDto> results = new ArrayList<>();
        for (CustomerModel customerModel : customerModels) {
            if (customerModel.getAccounting() == null) {
                continue;
            }
            List<DebtReportDto> debtReportSelects = debtReportSearchLst.stream().filter(d -> d.getCustomerId().equals(customerModel.getId())).collect(Collectors.toList());
            List<MonthlyClosingBalanceDto> monthlyClosingBalanceSelects = monthlyClosingBalanceLst == null ? null : monthlyClosingBalanceLst.stream().filter(m -> m.getCustomerId().equals(customerModel.getId())).collect(Collectors.toList());
            MonthlyClosingBalanceDto monthlyClosingBalanceDto = monthlyClosingBalanceSelects == null || monthlyClosingBalanceSelects.size() == 0 ? null : monthlyClosingBalanceSelects.get(0);

            for (String accountingId : accountingIds) {
                List<DebtReportDto> debtReports = calculateDebtReport(
                        debtReportSelects.stream().filter(d -> d.getDefaultAccountId().equals(accountingId)).collect(Collectors.toList()),
                        monthlyClosingBalanceDto,
                        customerModel,
                        accountingId
                );
                results.addAll(debtReports);
            }
        }

        return results;
    }

    private List<DebtReportDto> collectDebtReportHandle(DebtReportSearchDto debtReportSearchDto, List<CustomerModel> customerModels) {
        List<DebtReportDto> debtReportSearchLst = new ArrayList<>();
        CompletableFuture<Boolean> getDebtReportSearchLst = collectDebtReport(debtReportSearchLst, debtReportSearchDto.getFromDate(), debtReportSearchDto.getToDate(), customerModels, debtReportSearchDto.getDebtAccount().getId(), debtReportSearchDto.getAgencyId());
        CompletableFuture.allOf(
                getDebtReportSearchLst
        );
        List<MonthlyClosingBalanceDto> monthlyClosingBalanceLst = getDebtPreviousMonth(debtReportSearchDto.getFromDate(), customerModels, debtReportSearchDto.getAgencyId());
        List<DebtReportDto> debtReportOutOfSearchLst = collectDebtReportOutOfSearch(debtReportSearchDto.getFromDate(), customerModels, debtReportSearchDto.getDebtAccount().getId(), debtReportSearchDto.getAgencyId());

        List<DebtReportDto> results = new ArrayList<>();
        for (CustomerModel customerModel : customerModels) {
            if (customerModel.getAccounting() == null) {
                continue;
            }
            List<DebtReportDto> debtReportSelects = debtReportSearchLst.stream().filter(d -> d.getCustomerId().equals(customerModel.getId())).collect(Collectors.toList());
            List<MonthlyClosingBalanceDto> monthlyClosingBalanceSelects = monthlyClosingBalanceLst == null ? null : monthlyClosingBalanceLst.stream().filter(m -> m.getCustomerId().equals(customerModel.getId())).collect(Collectors.toList());
            MonthlyClosingBalanceDto monthlyClosingBalanceDto = monthlyClosingBalanceSelects == null || monthlyClosingBalanceSelects.size() == 0 ? null : monthlyClosingBalanceSelects.get(0);

            MonthlyClosingBalanceDto complementBalance = null;
            if (debtReportOutOfSearchLst != null && debtReportOutOfSearchLst.size() > 0) {
                List<DebtReportDto> debtReportOutOfSearchSelects = debtReportOutOfSearchLst.stream().filter(d -> d.getCustomerId().equals(customerModel.getId())).collect(Collectors.toList());
                complementBalance = calculateDebt(debtReportOutOfSearchSelects, monthlyClosingBalanceDto, customerModel, debtReportSearchDto.getDebtAccount().getId());
            }

            monthlyClosingBalanceDto = complementBalance != null ? complementBalance : monthlyClosingBalanceDto;
            List<DebtReportDto> debtReports = calculateDebtReport(debtReportSelects, monthlyClosingBalanceDto, customerModel, debtReportSearchDto.getDebtAccount().getId());
            results.addAll(debtReports);
        }

        return results;
    }

    private List<MonthlyClosingBalanceDto> getDebtPreviousMonth(Date fromDate, List<CustomerModel> customerModels, String agencyId) {
        Calendar previousMonthDate = Calendar.getInstance();
        previousMonthDate.setTime(fromDate);
        previousMonthDate.add(Calendar.MONTH, -1);
        List<MonthlyClosingBalance> monthlyClosingBalances = null;
        if (customerModels.size() > 1) {
            monthlyClosingBalances = monthlyClosingBalanceRepository.getByClosingDate(previousMonthDate.getTime(), agencyId);
        }
        else {
            MonthlyClosingBalance monthlyClosingBalance = monthlyClosingBalanceRepository.getByCustomerIdAndClosingDate(customerModels.get(0).getId(), previousMonthDate.getTime(), agencyId);
            if (monthlyClosingBalance != null) {
                monthlyClosingBalances = Arrays.asList(monthlyClosingBalance);
            }
        }
        List<MonthlyClosingBalanceDto> monthlyClosingBalanceLst = IMonthlyClosingBalanceDtoMapper.INSTANCE.toMonthlyClosingBalanceDtos(monthlyClosingBalances);
        return monthlyClosingBalanceLst;
    }

    private List<DebtReportDto> collectDebtReportOutOfSearch(Date fromDate, List<CustomerModel> customerModels, String accountingSearchId, String agencyId) {
        Calendar complementFromDate = Calendar.getInstance();
        complementFromDate.setTime(fromDate);

        // In case day == 1
        if (complementFromDate.get(Calendar.DAY_OF_MONTH) == 1) {
            return null;
        }

        // In case day > 1
        Calendar complementToDate = Calendar.getInstance();
        complementToDate.setTime(fromDate);
        int complementFromDays = complementFromDate.get(Calendar.DAY_OF_MONTH);
        complementFromDate.add(Calendar.DATE, -complementFromDays + 1);
        complementToDate.add(Calendar.DATE, -1);
        List<DebtReportDto> complementDebtReports = new ArrayList<>();
        CompletableFuture<Boolean> getComplementDebtReports = collectDebtReport(complementDebtReports, complementFromDate.getTime(), complementToDate.getTime(), customerModels, accountingSearchId, agencyId);
        CompletableFuture.allOf(
                getComplementDebtReports
        );
        return complementDebtReports;
    }

    @Async
    CompletableFuture<Boolean> collectDebtReport(List<DebtReportDto> debtReportDtoList, Date fromDate, Date toDate, List<CustomerModel> customerModels, String accountingSearchId, String agencyId) {
        List<DebtReportDto> getByPaymentDetailDtoList = new ArrayList<>();
        List<DebtReportDto> getByPaymentAdviceDetailDtoList = new ArrayList<>();
        List<DebtReportDto> getByReceiptDetailDtoList = new ArrayList<>();
        List<DebtReportDto> getByReceiptAdviceDetailDtoList = new ArrayList<>();
        List<DebtReportDto> getByExportingTransactionDtoList = new ArrayList<>();
        List<DebtReportDto> getByImportingTransactionDtoList = new ArrayList<>();
        List<DebtReportDto> getByDebtClearingDetailDtoList = new ArrayList<>();
        List<DebtReportDto> getByExportingReturnTransactionDtoList = new ArrayList<>();
        List<DebtReportDto> getByImportingReturnTransactionDtoList = new ArrayList<>();
        CustomerModel customerModel = (customerModels == null || customerModels.size() > 1) ? null : customerModels.get(0);

        CompletableFuture<Boolean> getPaymentDetailDtoList = getByPaymentDetailDtoList(getByPaymentDetailDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getPaymentAdviceDetailDtoList = getByPaymentAdviceDetailDtoList(getByPaymentAdviceDetailDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getReceiptDetailDtoList = getByReceiptDetailDtoList(getByReceiptDetailDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getReceiptAdviceDetailDtoList = getByReceiptAdviceDetailDtoList(getByReceiptAdviceDetailDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getExportingTransactionDtoList = getByExportingTransactionDtoList(getByExportingTransactionDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getImportingTransactionDtoList = getByImportingTransactionDtoList(getByImportingTransactionDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getDebtClearingDetailDtoList = getByDebtClearingDetailDtoList(getByDebtClearingDetailDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getExportingReturnTransactionDtoList = getByExportingReturnTransactionDtoList(getByExportingReturnTransactionDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);
        CompletableFuture<Boolean> getImportingReturnTransactionDtoList = getByImportingReturnTransactionDtoList(getByImportingReturnTransactionDtoList, fromDate, toDate, customerModel, accountingSearchId, agencyId);

        CompletableFuture.allOf (
                getPaymentDetailDtoList,
                getPaymentAdviceDetailDtoList,
                getReceiptDetailDtoList,
                getReceiptAdviceDetailDtoList,
                getExportingTransactionDtoList,
                getImportingTransactionDtoList,
                getDebtClearingDetailDtoList,
                getExportingReturnTransactionDtoList,
                getImportingReturnTransactionDtoList
        );

        for (CustomerModel customer : customerModels) {
            List<DebtReportDto> debtReports = updateCustomerInfo(
                    getByPaymentDetailDtoList,
                    getByPaymentAdviceDetailDtoList,
                    getByReceiptDetailDtoList,
                    getByReceiptAdviceDetailDtoList,
                    getByExportingTransactionDtoList,
                    getByImportingTransactionDtoList,
                    getByDebtClearingDetailDtoList,
                    getByExportingReturnTransactionDtoList,
                    getByImportingReturnTransactionDtoList,
                    customer
                    );
            debtReportDtoList.addAll(debtReports);
        }

        debtReportDtoList.sort(Comparator.comparing(DebtReportDto::getCreatedDate));

        return CompletableFuture.completedFuture(true);
    }

    private List<DebtReportDto> updateCustomerInfo (
            List<DebtReportDto> paymentDetailDtoList, List<DebtReportDto> paymentAdviceDetailDtoList,
            List<DebtReportDto> receiptDetailDtoList, List<DebtReportDto> receiptAdviceDetailDtoList,
            List<DebtReportDto> exportingTransactionDtoList, List<DebtReportDto> importingTransactionDtoList,
            List<DebtReportDto> debtClearingDetailDtoList, List<DebtReportDto> exportingReturnTransactionDtoList,
            List<DebtReportDto> importingReturnTransactionDtoList, CustomerModel customer
    ) {
        final String customerId = customer != null ? customer.getId() : "";
        List<DebtReportDto> paymentDetailSelects = paymentDetailDtoList.stream().filter(p -> customerId.equals(p.getCustomerId())).collect(Collectors.toList());
        List<DebtReportDto> paymentAdviceDetailSelects = paymentAdviceDetailDtoList.stream().filter(p -> customerId.equals(p.getCustomerId())).collect(Collectors.toList());
        List<DebtReportDto> receiptDetailSelects = receiptDetailDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> receiptAdviceDetailSelects = receiptAdviceDetailDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> exportingTransactionSelects = exportingTransactionDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> importingTransactionSelects = importingTransactionDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> debtClearingDetailSelects = debtClearingDetailDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> exportingReturnTransactionSelects = exportingReturnTransactionDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());
        List<DebtReportDto> importingReturnTransactionSelects = importingReturnTransactionDtoList.stream().filter(p -> p.getCustomerId().equals(customerId)).collect(Collectors.toList());

        List<DebtReportDto> debtReportDtoList = new ArrayList<>();
        debtReportDtoList.addAll(paymentDetailSelects);
        debtReportDtoList.addAll(paymentAdviceDetailSelects);
        debtReportDtoList.addAll(receiptDetailSelects);
        debtReportDtoList.addAll(receiptAdviceDetailSelects);
        debtReportDtoList.addAll(exportingTransactionSelects);
        debtReportDtoList.addAll(importingTransactionSelects);
        debtReportDtoList.addAll(debtClearingDetailSelects);
        debtReportDtoList.addAll(exportingReturnTransactionSelects);
        debtReportDtoList.addAll(importingReturnTransactionSelects);

        for (DebtReportDto item : debtReportDtoList) {
            if (customer != null) {
                item.setCustomerCode(customer.getCode());
                item.setCustomerName(customer.getFullName());
            }
        }

        return debtReportDtoList;
    }

    private List<DebtReportDto> calculateDebtReport(List<DebtReportDto> debtReportDtos, MonthlyClosingBalanceDto monthlyClosingBalanceDto,
                                                    CustomerModel customer, String accountingSearchId) {
        if (customer.getAccounting() == null) {
            return new ArrayList<>();
        }
        boolean isDefaultAccountReporting = customer.getAccounting().getId().equals(accountingSearchId);
        if (monthlyClosingBalanceDto == null || !isDefaultAccountReporting) {
            monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
            monthlyClosingBalanceDto.setDebitBalance(0D);
            monthlyClosingBalanceDto.setCreditBalance(null);
        }

        Double debtValue = monthlyClosingBalanceDto.getDebitBalance() != null ? monthlyClosingBalanceDto.getDebitBalance() : -monthlyClosingBalanceDto.getCreditBalance();
        for (DebtReportDto debtReport : debtReportDtos) {
            if (isDefaultAccountReporting
                    && !accountingSearchId.equals(debtReport.getCreditAccountId())
                    && !accountingSearchId.equals(debtReport.getDebitAccountId())) {
                continue;
            }
            Double amount = debtReport.getDebitAmount() != null ? debtReport.getDebitAmount() : -debtReport.getCreditAmount();
            debtValue += amount;

            if (debtValue >= 0) {
                debtReport.setDebitBalance(debtValue);
            } else {
                debtReport.setCreditBalance(-debtValue);
            }
        }
        return debtReportDtos;
    }

    private MonthlyClosingBalanceDto calculateDebt(List<DebtReportDto> debtReportDtos, MonthlyClosingBalanceDto monthlyClosingBalanceDto,
                                                   CustomerModel customer, String accountingSearchId) {
        boolean isDefaultAccountReporting = customer.getAccounting().getId().equals(accountingSearchId);
        if (monthlyClosingBalanceDto == null || !isDefaultAccountReporting) {
            monthlyClosingBalanceDto = new MonthlyClosingBalanceDto();
            monthlyClosingBalanceDto.setDebitBalance(0D);
        }

        Double debtValue = monthlyClosingBalanceDto.getDebitBalance() != null ? monthlyClosingBalanceDto.getDebitBalance() : -monthlyClosingBalanceDto.getCreditBalance();
        for (DebtReportDto debtReport : debtReportDtos) {
            if (isDefaultAccountReporting
                    && !accountingSearchId.equals(debtReport.getCreditAccountId())
                    && !accountingSearchId.equals(debtReport.getDebitAccountId())) {
                continue;
            }
            Double amount = debtReport.getDebitAmount() != null ? debtReport.getDebitAmount() : -debtReport.getCreditAmount();
            debtValue += amount;

            if (debtValue >= 0) {
                debtReport.setDebitBalance(debtValue);
            } else {
                debtReport.setCreditBalance(-debtValue);
            }
        }

        if (debtValue >= 0) {
            monthlyClosingBalanceDto.setDebitBalance(debtValue);
            monthlyClosingBalanceDto.setCreditBalance(null);
        } else {
            monthlyClosingBalanceDto.setCreditBalance(-debtValue);
            monthlyClosingBalanceDto.setDebitBalance(null);
        }

        return monthlyClosingBalanceDto;
    }

    @Async
    CompletableFuture<Boolean> getByPaymentDetailDtoList(List<DebtReportDto> getByPaymentDetailDtoList,
                                                         Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<PaymentDetail> paymentDetails = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            paymentDetails = paymentDetailRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            paymentDetails = paymentDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            paymentDetails = paymentDetailRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            paymentDetails = paymentDetailRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            paymentDetails = paymentDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<PaymentDetailFullDto> paymentDetailDtos = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailFullDtoList(paymentDetails);

        for (PaymentDetailFullDto p : paymentDetailDtos) {
            if (p.getDebitAccount().getId() == null) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = p.getPayment().getCode() + "." + formatter.format(p.getPayment().getCreatedDate()) + "." + p.getPayment().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(p.getPayment().getTransactionCustomerId());
            debtReportDto.setCode(p.getPayment().getCode());
            debtReportDto.setCreatedDate(p.getPayment().getCreatedDate());
            debtReportDto.setNumber(p.getPayment().getNumber());
            debtReportDto.setInvoiceDate(p.getPayment().getInvoiceDate());
            debtReportDto.setInvoiceCode(p.getPayment().getInvoiceCode());
            debtReportDto.setInvoiceNumber(p.getPayment().getInvoiceNumber());
            debtReportDto.setDescription(p.getDescription());
            debtReportDto.setContraAccountId(p.getCreditAccount() == null ? "" : p.getCreditAccount().getId());
            debtReportDto.setDebitAccountId(p.getDebitAccount() == null ? "" : p.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(p.getCreditAccount() == null ? "" : p.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(p.getDebitAccount().getId());
            Double total = p.getAmount() != null ? p.getAmount() : 0;
            debtReportDto.setDebitAmount(total);
            debtReportDto.setDetailDescription(p.getDescription());
            getByPaymentDetailDtoList.add(debtReportDto);
        }
        getByPaymentDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByPaymentAdviceDetailDtoList(List<DebtReportDto> getByPaymentAdviceDetailDtoList,
                                                               Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<PaymentAdviceDetail> paymentAdviceDetails = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            paymentAdviceDetails = paymentAdviceDetailRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            paymentAdviceDetails = paymentAdviceDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            paymentAdviceDetails = paymentAdviceDetailRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            paymentAdviceDetails = paymentAdviceDetailRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            paymentAdviceDetails = paymentAdviceDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<PaymentAdviceDetailFullDto> paymentAdviceDetailDtos = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailFullDtoList(paymentAdviceDetails);

        for (PaymentAdviceDetailFullDto p : paymentAdviceDetailDtos) {
            if (p.getDebitAccount().getId() == null) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = p.getPaymentAdvice().getCode() + "." + formatter.format(p.getPaymentAdvice().getCreatedDate()) + "." + p.getPaymentAdvice().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(p.getPaymentAdvice().getTransactionCustomerId());
            debtReportDto.setCode(p.getPaymentAdvice().getCode());
            debtReportDto.setCreatedDate(p.getPaymentAdvice().getCreatedDate());
            debtReportDto.setNumber(p.getPaymentAdvice().getNumber());
            debtReportDto.setInvoiceDate(p.getPaymentAdvice().getInvoiceDate());
            debtReportDto.setInvoiceCode(p.getPaymentAdvice().getInvoiceCode());
            debtReportDto.setInvoiceNumber(p.getPaymentAdvice().getInvoiceNumber());
            debtReportDto.setDescription(p.getDescription());
            debtReportDto.setContraAccountId(p.getCreditAccount() == null ? "" : p.getCreditAccount().getId());
            debtReportDto.setDebitAccountId(p.getDebitAccount() == null ? "" : p.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(p.getCreditAccount() == null ? "" : p.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(p.getDebitAccount().getId());
            Double total = p.getAmount() != null ? p.getAmount() : 0;
            debtReportDto.setDebitAmount(total);
            debtReportDto.setDetailDescription(p.getDescription());
            getByPaymentAdviceDetailDtoList.add(debtReportDto);
        }
        getByPaymentAdviceDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByReceiptDetailDtoList(List<DebtReportDto> getByReceiptDetailDtoList,
                                                         Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ReceiptDetail> receiptDetails = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            receiptDetails = receiptDetailRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            receiptDetails = receiptDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            receiptDetails = receiptDetailRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            receiptDetails = receiptDetailRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            receiptDetails = receiptDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ReceiptDetailFullDto> receiptDetailDtos = IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailFullDtoList(receiptDetails);

        for (ReceiptDetailFullDto r : receiptDetailDtos) {
            if ((r.getCreditAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = r.getReceipt().getCode() + "." + formatter.format(r.getReceipt().getCreatedDate()) + "." + r.getReceipt().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(r.getReceipt().getTransactionCustomerId());
            debtReportDto.setCode(r.getReceipt().getCode());
            debtReportDto.setCreatedDate(r.getReceipt().getCreatedDate());
            debtReportDto.setNumber(r.getReceipt().getNumber());
            debtReportDto.setInvoiceDate(r.getReceipt().getInvoiceDate());
            debtReportDto.setInvoiceCode(r.getReceipt().getInvoiceCode());
            debtReportDto.setInvoiceNumber(r.getReceipt().getInvoiceNumber());
            debtReportDto.setDescription(r.getDescription());
            debtReportDto.setContraAccountId(r.getDebitAccount() == null ? "" : r.getDebitAccount().getId());
            debtReportDto.setDebitAccountId(r.getDebitAccount() == null ? "" : r.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(r.getCreditAccount() == null ? "" : r.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(r.getCreditAccount().getId());
            Double total = r.getAmount() != null ? r.getAmount() : 0;
            debtReportDto.setCreditAmount(total);
            debtReportDto.setDetailDescription(r.getDescription());
            getByReceiptDetailDtoList.add(debtReportDto);
        }
        getByReceiptDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByReceiptAdviceDetailDtoList(List<DebtReportDto> getByReceiptAdviceDetailDtoList,
                                                               Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ReceiptAdviceDetail> receiptAdviceDetails = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            receiptAdviceDetails = receiptAdviceDetailRepository.getByCustomerIdNotDefault(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            receiptAdviceDetails = receiptAdviceDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            receiptAdviceDetails = receiptAdviceDetailRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            receiptAdviceDetails = receiptAdviceDetailRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            receiptAdviceDetails = receiptAdviceDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ReceiptAdviceDetailFullDto> receiptAdviceDetailDtos = IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailFullDtoList(receiptAdviceDetails);

        for (ReceiptAdviceDetailFullDto r : receiptAdviceDetailDtos) {
            if ((r.getCreditAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = r.getReceiptAdvice().getCode() + "." + formatter.format(r.getReceiptAdvice().getCreatedDate()) + "." + r.getReceiptAdvice().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(r.getReceiptAdvice().getTransactionCustomerId());
            debtReportDto.setCode(r.getReceiptAdvice().getCode());
            debtReportDto.setCreatedDate(r.getReceiptAdvice().getCreatedDate());
            debtReportDto.setNumber(r.getReceiptAdvice().getNumber());
            debtReportDto.setInvoiceDate(r.getReceiptAdvice().getInvoiceDate());
            debtReportDto.setInvoiceCode(r.getReceiptAdvice().getInvoiceCode());
            debtReportDto.setInvoiceNumber(r.getReceiptAdvice().getInvoiceNumber());
            debtReportDto.setDescription(r.getDescription());
            debtReportDto.setContraAccountId(r.getCreditAccount() == null ? "" : r.getCreditAccount().getId());
            debtReportDto.setDebitAccountId(r.getDebitAccount() == null ? "" : r.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(r.getCreditAccount() == null ? "" : r.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(r.getCreditAccount().getId());
            Double total = r.getAmount() != null ? r.getAmount() : 0;
            debtReportDto.setCreditAmount(total);
            debtReportDto.setDetailDescription(r.getDescription());
            getByReceiptAdviceDetailDtoList.add(debtReportDto);
        }
        getByReceiptAdviceDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByExportingTransactionDtoList(List<DebtReportDto> getByExportingWarehouseDetailDtoList,
                                                                Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ExportingTransaction> exportingTransactions = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            exportingTransactions = exportingTransactionRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            exportingTransactions = exportingTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            exportingTransactions = exportingTransactionRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            exportingTransactions = exportingTransactionRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            exportingTransactions = exportingTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ExportingTransactionFullDto> exportingTransactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionFullDtos(exportingTransactions);

        for (ExportingTransactionFullDto e : exportingTransactionDtos) {
            if ((e.getDebitAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = e.getExportingWarehouse().getCode() + "." + formatter.format(e.getExportingWarehouse().getCreatedDate()) + "." + e.getExportingWarehouse().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(e.getExportingWarehouse().getTransactionCustomerId());
            debtReportDto.setCode(e.getExportingWarehouse().getCode());
            debtReportDto.setCreatedDate(e.getExportingWarehouse().getCreatedDate());
            debtReportDto.setNumber(e.getExportingWarehouse().getNumber());
            debtReportDto.setInvoiceDate(e.getExportingWarehouse().getInvoiceDate());
            debtReportDto.setInvoiceCode(e.getExportingWarehouse().getInvoiceCode());
            debtReportDto.setInvoiceNumber(e.getExportingWarehouse().getInvoiceNumber());
            debtReportDto.setDescription(e.getExportingWarehouse().getDescription());
            debtReportDto.setContraAccount(e.getCreditAccount().getCode());
            debtReportDto.setDebitAccountId(e.getDebitAccount() == null ? "" : e.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(e.getCreditAccount() == null ? "" : e.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(e.getDebitAccount().getId());
            Double total = e.getAmount() != null ? e.getAmount() : 0;
            debtReportDto.setDebitAmount(total);
            debtReportDto.setMerchandiseId(e.getMerchandise() == null ? "" : e.getMerchandise().getId());
            debtReportDto.setQuantity(e.getQuantity());
            debtReportDto.setUnitPrice(e.getPrice());
            getByExportingWarehouseDetailDtoList.add(debtReportDto);
        }
        getByExportingWarehouseDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByImportingTransactionDtoList(List<DebtReportDto> getByImportingWarehouseDetailDtoList,
                                                                Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ImportingTransaction> importingTransactions = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            importingTransactions = importingTransactionRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            importingTransactions = importingTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            importingTransactions = importingTransactionRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            importingTransactions = importingTransactionRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            importingTransactions = importingTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ImportingTransactionFullDto> importingTransactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionFullDtoList(importingTransactions);

        for (ImportingTransactionFullDto e : importingTransactionDtos) {
            if ((e.getCreditAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = e.getImportingWarehouse().getCode() + "." + formatter.format(e.getImportingWarehouse().getCreatedDate()) + "." + e.getImportingWarehouse().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(e.getImportingWarehouse().getTransactionCustomerId());
            debtReportDto.setCode(e.getImportingWarehouse().getCode());
            debtReportDto.setCreatedDate(e.getImportingWarehouse().getCreatedDate());
            debtReportDto.setNumber(e.getImportingWarehouse().getNumber());
            debtReportDto.setInvoiceDate(e.getImportingWarehouse().getInvoiceDate());
            debtReportDto.setInvoiceCode(e.getImportingWarehouse().getInvoiceCode());
            debtReportDto.setInvoiceNumber(e.getImportingWarehouse().getInvoiceNumber());
            debtReportDto.setDescription(e.getImportingWarehouse().getDescription());
            debtReportDto.setContraAccount(e.getDebitAccount().getCode());
            debtReportDto.setDebitAccountId(e.getDebitAccount() == null ? "" : e.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(e.getCreditAccount() == null ? "" : e.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount() != null ? e.getAmount() : 0;
            debtReportDto.setCreditAmount(total);
            debtReportDto.setMerchandiseId(e.getMerchandise() == null ? "" : e.getMerchandise().getId());
            debtReportDto.setQuantity(e.getQuantity());
            debtReportDto.setUnitPrice(e.getPrice());
            getByImportingWarehouseDetailDtoList.add(debtReportDto);
        }
        getByImportingWarehouseDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByDebtClearingDetailDtoList(List<DebtReportDto> getByDebtClearingDetailDtoList,
                                                              Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<DebtClearingDetail> debtClearingDetails = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            debtClearingDetails = debtClearingDetailRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            debtClearingDetails = debtClearingDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            debtClearingDetails = debtClearingDetailRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            debtClearingDetails = debtClearingDetailRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            debtClearingDetails = debtClearingDetailRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<DebtClearingDetailFullDto> debtClearingDetailDtos = IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailFullDtoList(debtClearingDetails);

        for (DebtClearingDetailFullDto r : debtClearingDetailDtos) {
            if (r.getCreditAccount().getId() == null || r.getDebitAccount().getId() == null) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = r.getDebtClearing().getCode() + "." + formatter.format(r.getDebtClearing().getCreatedDate()) + "." + r.getDebtClearing().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(r.getCustomer().getId());
            debtReportDto.setCode(r.getDebtClearing().getCode());
            debtReportDto.setCreatedDate(r.getDebtClearing().getCreatedDate());
            debtReportDto.setNumber(r.getDebtClearing().getNumber());
            debtReportDto.setInvoiceDate(r.getDebtClearing().getInvoiceDate());
            debtReportDto.setInvoiceCode(r.getDebtClearing().getInvoiceCode());
            debtReportDto.setInvoiceNumber(r.getDebtClearing().getInvoiceNumber());
            debtReportDto.setDescription(r.getDescription());
            debtReportDto.setContraAccountId(r.getDebitAccount() == null ? "" : r.getDebitAccount().getId());
            debtReportDto.setDebitAccountId(r.getDebitAccount() == null ? "" : r.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(r.getCreditAccount() == null ? "" : r.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(r.getCreditAccount().getId());
            Double total = r.getAmount() != null ? r.getAmount() : 0;
            debtReportDto.setCreditAmount(total);
            debtReportDto.setDetailDescription(r.getDescription());

            //debt report for customer debt
            DebtReportDto debtReportForCustomerDebtDto = new DebtReportDto(debtReportDto);
            debtReportForCustomerDebtDto.setCustomerId(r.getCustomerDebt().getId());
            debtReportForCustomerDebtDto.setContraAccountId(r.getCreditAccount() == null ? "" : r.getCreditAccount().getId());
            debtReportForCustomerDebtDto.setCreditAmount(null);
            Double totalDebt = r.getAmount() != null ? r.getAmount() : 0;
            debtReportForCustomerDebtDto.setDebitAmount(totalDebt);
            debtReportDto.setDefaultAccountId(r.getDebitAccount().getId());
            getByDebtClearingDetailDtoList.add(debtReportDto);
            getByDebtClearingDetailDtoList.add(debtReportForCustomerDebtDto);
        }
        getByDebtClearingDetailDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByExportingReturnTransactionDtoList(List<DebtReportDto> getByExportingReturnTransactionDtoList,
                                                                      Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ExportingReturnTransaction> exportingReturnTransactions = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            exportingReturnTransactions = exportingReturnTransactionRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            exportingReturnTransactions = exportingReturnTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            exportingReturnTransactions = exportingReturnTransactionRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            exportingReturnTransactions = exportingReturnTransactionRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            exportingReturnTransactions = exportingReturnTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ExportingReturnTransactionFullDto> exportingReturnTransactionDtos = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionFullDtos(exportingReturnTransactions);

        for (ExportingReturnTransactionFullDto e : exportingReturnTransactionDtos) {
            if ((e.getDebitAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = e.getExportingReturn().getCode() + "." + formatter.format(e.getExportingReturn().getCreatedDate()) + "." + e.getExportingReturn().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(e.getExportingReturn().getTransactionCustomerId());
            debtReportDto.setCode(e.getExportingReturn().getCode());
            debtReportDto.setCreatedDate(e.getExportingReturn().getCreatedDate());
            debtReportDto.setNumber(e.getExportingReturn().getNumber());
            debtReportDto.setInvoiceDate(e.getExportingReturn().getInvoiceDate());
            debtReportDto.setInvoiceCode(e.getExportingReturn().getInvoiceCode());
            debtReportDto.setInvoiceNumber(e.getExportingReturn().getInvoiceNumber());
            debtReportDto.setDescription(e.getExportingReturn().getDescription());
            debtReportDto.setContraAccountId(e.getCreditAccount() == null ? "" : e.getCreditAccount().getId());
            debtReportDto.setDebitAccountId(e.getDebitAccount() == null ? "" : e.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(e.getCreditAccount() == null ? "" : e.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(e.getDebitAccount().getId());
            Double total = e.getAmount() != null ? e.getAmount() : 0;
            debtReportDto.setDebitAmount(total);
            debtReportDto.setMerchandiseId(e.getMerchandise() == null ? "" : e.getMerchandise().getId());
            debtReportDto.setQuantity(e.getQuantity());
            debtReportDto.setUnitPrice(e.getPrice());
            getByExportingReturnTransactionDtoList.add(debtReportDto);
        }
        getByExportingReturnTransactionDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByImportingReturnTransactionDtoList(List<DebtReportDto> getByImportingReturnTransactionDtoList,
                                                                      Date fromDate, Date toDate, CustomerModel customerModel, String accountingSearchId, String agencyId) {
        List<ImportingReturnTransaction> importingReturnTransactions = new ArrayList<>();
        // case get debt with not default account
        if (fromDate == null && toDate == null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            importingReturnTransactions = importingReturnTransactionRepository.getByCustomerIdNotDefaultAccount(customerModel.getId(), accountingSearchId, agencyId);
        }
        // case debt report with customer
        if (fromDate != null && toDate != null &&
                customerModel != null && customerModel.getId() != null && accountingSearchId != null) {
            importingReturnTransactions = importingReturnTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, accountingSearchId, agencyId);
        }
        // case debt report with all customer
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId != null) {
            importingReturnTransactions = importingReturnTransactionRepository.getByCreatedDate(fromDate, toDate, accountingSearchId, agencyId);
        }
        // case calculate monthly debt
        if ((customerModel == null || customerModel.getId() == null) && accountingSearchId == null) {
            importingReturnTransactions = importingReturnTransactionRepository.getByCreatedDate(fromDate, toDate, agencyId);
        }
        // case get debt
        if (customerModel != null && customerModel.getId() != null && accountingSearchId == null) {
            importingReturnTransactions = importingReturnTransactionRepository.getByCustomerIdAndCreatedDate(customerModel.getId(), fromDate, toDate, agencyId);
        }

        List<ImportingReturnTransactionFullDto> importingReturnTransactionDtos = IImportingReturnTransactionDtoMapper.INSTANCE.toImportingReturnTransactionFullDtos(importingReturnTransactions);

        for (ImportingReturnTransactionFullDto e : importingReturnTransactionDtos) {
            if ((e.getCreditAccount().getId() == null)) {
                continue;
            }
            DebtReportDto debtReportDto = new DebtReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
            String fullCode = e.getImportingReturn().getCode() + "." + formatter.format(e.getImportingReturn().getCreatedDate()) + "." + e.getImportingReturn().getNumber();
            debtReportDto.setFullCode(fullCode);
            debtReportDto.setCustomerId(e.getImportingReturn().getTransactionCustomerId());
            debtReportDto.setCode(e.getImportingReturn().getCode());
            debtReportDto.setCreatedDate(e.getImportingReturn().getCreatedDate());
            debtReportDto.setNumber(e.getImportingReturn().getNumber());
            debtReportDto.setInvoiceDate(e.getImportingReturn().getInvoiceDate());
            debtReportDto.setInvoiceCode(e.getImportingReturn().getInvoiceCode());
            debtReportDto.setInvoiceNumber(e.getImportingReturn().getInvoiceNumber());
            debtReportDto.setDescription(e.getImportingReturn().getDescription());
            debtReportDto.setContraAccountId(e.getDebitAccount() == null ? "" : e.getDebitAccount().getId());
            debtReportDto.setDebitAccountId(e.getDebitAccount() == null ? "" : e.getDebitAccount().getId());
            debtReportDto.setCreditAccountId(e.getCreditAccount() == null ? "" : e.getCreditAccount().getId());
            debtReportDto.setDefaultAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount() != null ? e.getAmount() : 0;
            debtReportDto.setCreditAmount(total);
            debtReportDto.setMerchandiseId(e.getMerchandise() == null ? "" : e.getMerchandise().getId());
            debtReportDto.setQuantity(e.getQuantity());
            debtReportDto.setUnitPrice(e.getPrice());
            getByImportingReturnTransactionDtoList.add(debtReportDto);
        }
        getByImportingReturnTransactionDtoList.sort(Comparator.comparing(DebtReportDto::getFullCode));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> collectAccountInfo(HttpServletRequest request, List<DebtReportDto> debtReports)
            throws IOException, JAXBException {
        if (debtReports == null || debtReports.size() == 0) {
            return CompletableFuture.completedFuture(true);
        }

        List<String> ids = debtReports.stream().map(d -> d.getContraAccountId()).distinct().filter(i -> i != null).collect(Collectors.toList());
        if (ids == null || ids.size() == 0) {
            return CompletableFuture.completedFuture(true);
        }

        List<AccountingTableModel> accountingTableModels = warehouseRequestService.getAccountingList(request, ids);
        for (AccountingTableModel item : accountingTableModels) {
            List<DebtReportDto> temps = debtReports.stream().filter(d -> d.getContraAccountId() != null
                    && d.getContraAccountId().equals(item.getId())).collect(Collectors.toList());
            for (DebtReportDto temp : temps) {
                temp.setContraAccount(item.getCode());
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> collectMerchandiseInfo(HttpServletRequest request, List<DebtReportDto> debtReports)
            throws IOException, JAXBException {
        if (debtReports == null || debtReports.size() == 0) {
            return CompletableFuture.completedFuture(true);
        }
        List<String> merchandiseIds = debtReports.stream().map(d -> d.getMerchandiseId()).distinct().collect(Collectors.toList());
        if (merchandiseIds == null || merchandiseIds.size() == 0) {
            return CompletableFuture.completedFuture(true);
        }
        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, merchandiseIds);
        for(MerchandiseModel item : merchandiseModels) {
            List<DebtReportDto> temps = debtReports.stream().filter(d -> d.getMerchandiseId() != null
                    && d.getMerchandiseId().equals(item.getId())).collect(Collectors.toList());
            for (DebtReportDto temp : temps) {
                temp.setMerchandiseCode(item.getCode());
                temp.setDetailDescription(item.getName());
                temp.setUnit(item.getUnit() == null ? "" : item.getUnit().getName());
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    private List<AccountingTableModel> getAccounting(HttpServletRequest request, List<String> ids) throws IOException, JAXBException {
        List<AccountingTableModel> accountingTables = warehouseRequestService.getAccountingList(request, ids);
        return accountingTables;
    }

    private List<AccountingTableModel> getAccountingByCodes(HttpServletRequest request, List<String> codes) throws IOException, JAXBException {
        List<AccountingTableModel> accountingTables = warehouseRequestService.getAccountingListByCodes(request, codes);
        return accountingTables;
    }
}
