package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.models.warehouse.MerchandiseModel;
import com.antdigital.agency.core.models.warehouse.request.CustomerSearchModel;
import com.antdigital.agency.core.models.warehouse.request.MerchandiseSearchModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.*;
import com.antdigital.agency.dal.data.*;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IGeneralJournalReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextListener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@WebListener
public class GeneralJournalReportServiceImpl extends RequestContextListener implements IGeneralJournalReportService {
    private static final Logger logger = LoggerFactory.getLogger(GeneralJournalReportServiceImpl.class);

    @Autowired
    private IExportingWarehouseDao exportingWarehouseDao;

    @Autowired
    private IImportingWarehouseDao importingWarehouseDao;

    @Autowired
    private IPaymentDao paymentDao;

    @Autowired
    private IReceiptDao receiptDao;

    @Autowired
    private IPaymentAdviceDao paymentAdviceDao;

    @Autowired
    private IReceiptAdviceDao receiptAdviceDao;

    @Autowired
    private IDebtClearingDao debtClearingDao;

    @Autowired
    private IExportingReturnDao exportingReturnDao;

    @Autowired
    private IImportingReturnDao importingReturnDao;

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
    private IWarehouseRequestService warehouseRequestService;

    @Override
    public List<GeneralJournalReportDto> getOneGeneralJournalReport(HttpServletRequest request, LicenseSearchDto licenseSearchDto) throws IOException, JAXBException {
        //Get customer id from warehouse service
        if (licenseSearchDto.getCustomerCode() != null && licenseSearchDto.getCustomerCode().isEmpty()) {
            licenseSearchDto.setCustomerCode(null);
        }
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
        if (licenseSearchDto.getMerchandiseCode() != null && licenseSearchDto.getMerchandiseCode().isEmpty()) {
            licenseSearchDto.setMerchandiseCode(null);
        }
        List<String> merchandiseIds = new ArrayList<>();
        List<MerchandiseModel> merchandiseModelList = new ArrayList<>();
        // Case no merchandise information search
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

        List<GeneralJournalReportDto> generalJournalReportDtos = collectData(licenseSearchDto, customerIds, merchandiseIds);
        List<String> accountCollectedIds = new ArrayList<>();
        List<String> customerCollectedIds = new ArrayList<>();
        List<String> merchandiseCollectedIds = new ArrayList<>();

        for (GeneralJournalReportDto item : generalJournalReportDtos) {
            int indexContraAccount = accountCollectedIds.indexOf(item.getContraAccountId());
            int indexCustomer = customerCollectedIds.indexOf(item.getCustomerId());
            int indexMerchandise = merchandiseCollectedIds.indexOf(item.getMerchandiseId());

            if (indexContraAccount == -1) {
                accountCollectedIds.add(item.getContraAccountId());
            }

            if (indexCustomer == -1) {
                customerCollectedIds.add(item.getCustomerId());
            }

            if (indexMerchandise == -1) {
                merchandiseCollectedIds.add(item.getMerchandiseId());
            }
        }
        List<AccountingTableModel> collectAccountingTable = new ArrayList<>();
        List<CustomerModel> collectCustomer = new ArrayList<>();
        List<MerchandiseModel> collectMerchandise = new ArrayList<>();
        CompletableFuture<Boolean> getAccountingTable = collectAccountingTable(request, accountCollectedIds, collectAccountingTable);
        CompletableFuture<Boolean> getCustomer = collectCustomer(request, customerCollectedIds, collectCustomer);
        CompletableFuture<Boolean> getMerchandise = collectMerchandise(request, merchandiseCollectedIds, collectMerchandise);

        CompletableFuture.allOf(
                getAccountingTable,
                getCustomer,
                getMerchandise
        );

        for (GeneralJournalReportDto item : generalJournalReportDtos) {
            for (AccountingTableModel account : collectAccountingTable) {
                for (CustomerModel customer : collectCustomer) {
                    if (item.getContraAccountId() != null && item.getContraAccountId().equals(account.getId())) {
                        item.setContraAccount(account.getCode());
                    }
                    if (item.getCustomerId() != null && item.getCustomerId().equals(customer.getId())) {
                        item.setCustomerCode(customer.getCode());
                        item.setCustomerName(customer.getFullName());
                    }
                    for (MerchandiseModel merchandise : collectMerchandise) {
                        if (item.getMerchandiseId() != null && item.getMerchandiseId().equals(merchandise.getId())) {
                            item.setDetailDescription(merchandise.getName());
                        }
                    }
                }
            }
        }
        return generalJournalReportDtos;
    }

    @Async
    CompletableFuture<Boolean> collectAccountingTable(HttpServletRequest request, List<String> accountCollectedIds, List<AccountingTableModel> collectedAccountingTable) throws IOException, JAXBException {
        collectedAccountingTable.addAll(warehouseRequestService.getAccountingList(request, accountCollectedIds));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> collectCustomer(HttpServletRequest request, List<String> customerCollectedIds, List<CustomerModel> collectedCustomer) throws IOException, JAXBException {
        collectedCustomer.addAll(warehouseRequestService.getCustomers(request, customerCollectedIds));
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> collectMerchandise(HttpServletRequest request, List<String> merchandiseCollectedIds, List<MerchandiseModel> collectedMerchandise) throws IOException, JAXBException {
        collectedMerchandise.addAll(Arrays.asList(warehouseRequestService.getMerchandises(request, merchandiseCollectedIds)));
        return CompletableFuture.completedFuture(true);
    }

    private List<GeneralJournalReportDto> collectData(LicenseSearchDto licenseSearchDto, List<String> customerIds, List<String> merchandiseIds){
        List<GeneralJournalReportDto> generalJournalReportDtos = new ArrayList<>();

        List<GeneralJournalReportDto> getByPaymentDetailDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByPaymentAdviceDetailDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByReceiptDetailDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByReceiptAdviceDetailDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByExportingTransactionDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByImportingTransactionDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByDebtClearingDetailDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByExportingReturnTransactionDtoList = new ArrayList<>();
        List<GeneralJournalReportDto> getByImportingReturnTransactionDtoList = new ArrayList<>();

        CompletableFuture<Boolean> getExportingTransactionDtoList = getByExportingTransactionDtoList(getByExportingTransactionDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getImportingTransactionDtoList = getByImportingTransactionDtoList(getByImportingTransactionDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getPaymentDetailDtoList = getByPaymentDetailDtoList(getByPaymentDetailDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getReceiptDetailDtoList = getByReceiptDetailDtoList(getByReceiptDetailDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getPaymentAdviceDetailDtoList = getByPaymentAdviceDetailDtoList(getByPaymentAdviceDetailDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getReceiptAdviceDetailDtoList = getByReceiptAdviceDetailDtoList(getByReceiptAdviceDetailDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getDebtClearingDetailDtoList = getByDebtClearingDetailDtoList(getByDebtClearingDetailDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getExportingReturnTransactionDtoList = getByExportingReturnTransactionDtoList(getByExportingReturnTransactionDtoList, licenseSearchDto, customerIds, merchandiseIds);
        CompletableFuture<Boolean> getImportingReturnTransactionDtoList = getByImportingReturnTransactionDtoList(getByImportingReturnTransactionDtoList, licenseSearchDto, customerIds, merchandiseIds);

        CompletableFuture.allOf(
                getExportingTransactionDtoList,
                getImportingTransactionDtoList,
                getPaymentDetailDtoList,
                getReceiptDetailDtoList,
                getPaymentAdviceDetailDtoList,
                getReceiptAdviceDetailDtoList,
                getDebtClearingDetailDtoList,
                getExportingReturnTransactionDtoList,
                getImportingReturnTransactionDtoList
        );

        generalJournalReportDtos.addAll(getByExportingTransactionDtoList);
        generalJournalReportDtos.addAll(getByImportingTransactionDtoList);
        generalJournalReportDtos.addAll(getByPaymentDetailDtoList);
        generalJournalReportDtos.addAll(getByReceiptDetailDtoList);
        generalJournalReportDtos.addAll(getByPaymentAdviceDetailDtoList);
        generalJournalReportDtos.addAll(getByReceiptAdviceDetailDtoList);
        generalJournalReportDtos.addAll(getByDebtClearingDetailDtoList);
        generalJournalReportDtos.addAll(getByExportingReturnTransactionDtoList);
        generalJournalReportDtos.addAll(getByImportingReturnTransactionDtoList);
        generalJournalReportDtos.sort(Comparator.comparing(GeneralJournalReportDto::getCreatedDate));
        return generalJournalReportDtos;
    }

    @Async
    CompletableFuture<Boolean> getByExportingTransactionDtoList(List<GeneralJournalReportDto> getByExportingWarehouseDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ExportingWarehouseDetail> exportingWarehouseDetails = exportingWarehouseDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ExportingTransaction> totalExportingTransactions = new ArrayList<>();
        for (ExportingWarehouseDetail exportingWarehouseDetail : exportingWarehouseDetails) {
            List<ExportingTransaction> exportingTransactions = exportingTransactionRepository.getByExportingId(exportingWarehouseDetail.getId());
            totalExportingTransactions.addAll(exportingTransactions);
        }
        List<ExportingTransactionFullDto> totalExportingTransactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionFullDtos(totalExportingTransactions);

        for (ExportingTransactionFullDto e : totalExportingTransactionDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getExportingWarehouse().getCode() + "." + formatter.format(e.getExportingWarehouse().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getExportingWarehouse().getCode());
            generalJournalReportDto.setCreatedDate(e.getExportingWarehouse().getCreatedDate());
            generalJournalReportDto.setNumber(e.getExportingWarehouse().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getExportingWarehouse().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getExportingWarehouse().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getExportingWarehouse().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getExportingWarehouse().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getExportingWarehouse().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setDebitAmount(total);
            generalJournalReportDto.setMerchandiseId(e.getMerchandise().getId());
            generalJournalReportDto.setDetailDescription(e.getMerchandise().getName());
            getByExportingWarehouseDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByImportingTransactionDtoList(List<GeneralJournalReportDto> getByImportingWarehouseDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ImportingWarehouseDetail> importingWarehouseDetails = importingWarehouseDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ImportingTransaction> totalImportingTransactions = new ArrayList<>();
        for (ImportingWarehouseDetail importingWarehouseDetail : importingWarehouseDetails) {
            List<ImportingTransaction> importingTransactions = importingTransactionRepository.getByImportingId(importingWarehouseDetail.getId());
            totalImportingTransactions.addAll(importingTransactions);
        }
        List<ImportingTransactionFullDto> totalImportingTransactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionFullDtoList(totalImportingTransactions);

        for (ImportingTransactionFullDto e : totalImportingTransactionDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getImportingWarehouse().getCode() + "." + formatter.format(e.getImportingWarehouse().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getImportingWarehouse().getCode());
            generalJournalReportDto.setCreatedDate(e.getImportingWarehouse().getCreatedDate());
            generalJournalReportDto.setNumber(e.getImportingWarehouse().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getImportingWarehouse().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getImportingWarehouse().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getImportingWarehouse().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getImportingWarehouse().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getImportingWarehouse().getDescription());
            generalJournalReportDto.setContraAccountId(e.getDebitAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setCreditAmount(total);
            generalJournalReportDto.setMerchandiseId(e.getMerchandise().getId());
            generalJournalReportDto.setDetailDescription(e.getMerchandise().getName());
            getByImportingWarehouseDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByPaymentDetailDtoList(List<GeneralJournalReportDto> getByPaymentDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<PaymentSearchDetail> paymentSearchDetails = paymentDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<PaymentDetail> totalPaymentDetails = new ArrayList<>();
        for (PaymentSearchDetail paymentSearchDetail : paymentSearchDetails) {
            List<PaymentDetail> paymentDetails = paymentDetailRepository.getByPaymentId(paymentSearchDetail.getId());
            totalPaymentDetails.addAll(paymentDetails);
        }
        List<PaymentDetailFullDto> totalPaymentDetailDtos = IPaymentDetailDtoMapper.INSTANCE.toPaymentDetailFullDtoList(totalPaymentDetails);

        for (PaymentDetailFullDto e : totalPaymentDetailDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getPayment().getCode() + "." + formatter.format(e.getPayment().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getPayment().getCode());
            generalJournalReportDto.setCreatedDate(e.getPayment().getCreatedDate());
            generalJournalReportDto.setNumber(e.getPayment().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getPayment().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getPayment().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getPayment().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getPayment().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getPayment().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setDebitAmount(total);
            generalJournalReportDto.setDetailDescription(e.getDescription());
            getByPaymentDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByReceiptDetailDtoList(List<GeneralJournalReportDto> getByReceiptDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ReceiptSearchDetail> receiptSearchDetails = receiptDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ReceiptDetail> totalReceiptDetails = new ArrayList<>();
        for (ReceiptSearchDetail receiptSearchDetail : receiptSearchDetails) {
            List<ReceiptDetail> receiptDetails = receiptDetailRepository.getByReceiptId(receiptSearchDetail.getId());
            totalReceiptDetails.addAll(receiptDetails);
        }
        List<ReceiptDetailFullDto> totalReceiptDetailDtos = IReceiptDetailDtoMapper.INSTANCE.toReceiptDetailFullDtoList(totalReceiptDetails);

        for (ReceiptDetailFullDto e : totalReceiptDetailDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getReceipt().getCode() + "." + formatter.format(e.getReceipt().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getReceipt().getCode());
            generalJournalReportDto.setCreatedDate(e.getReceipt().getCreatedDate());
            generalJournalReportDto.setNumber(e.getReceipt().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getReceipt().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getReceipt().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getReceipt().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getReceipt().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getReceipt().getDescription());
            generalJournalReportDto.setContraAccountId(e.getDebitAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setCreditAmount(total);
            generalJournalReportDto.setDetailDescription(e.getDescription());
            getByReceiptDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByPaymentAdviceDetailDtoList(List<GeneralJournalReportDto> getByPaymentAdviceDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<PaymentAdviceSearchDetail> paymentAdviceSearchDetails = paymentAdviceDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<PaymentAdviceDetail> totalPaymentAdviceDetails = new ArrayList<>();
        for (PaymentAdviceSearchDetail paymentAdviceSearchDetail : paymentAdviceSearchDetails) {
            List<PaymentAdviceDetail> paymentAdviceDetails = paymentAdviceDetailRepository.getByPaymentAdviceId(paymentAdviceSearchDetail.getId());
            totalPaymentAdviceDetails.addAll(paymentAdviceDetails);
        }
        List<PaymentAdviceDetailFullDto> totalPaymentAdviceDetailDtos = IPaymentAdviceDetailDtoMapper.INSTANCE.toPaymentAdviceDetailFullDtoList(totalPaymentAdviceDetails);

        for (PaymentAdviceDetailFullDto e : totalPaymentAdviceDetailDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getPaymentAdvice().getCode() + "." + formatter.format(e.getPaymentAdvice().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getPaymentAdvice().getCode());
            generalJournalReportDto.setCreatedDate(e.getPaymentAdvice().getCreatedDate());
            generalJournalReportDto.setNumber(e.getPaymentAdvice().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getPaymentAdvice().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getPaymentAdvice().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getPaymentAdvice().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getPaymentAdvice().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getPaymentAdvice().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setDebitAmount(total);
            generalJournalReportDto.setDetailDescription(e.getDescription());
            getByPaymentAdviceDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByReceiptAdviceDetailDtoList(List<GeneralJournalReportDto> getByReceiptAdviceDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ReceiptAdviceSearchDetail> receiptAdviceSearchDetails = receiptAdviceDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ReceiptAdviceDetail> totalReceiptAdviceDetails = new ArrayList<>();
        for (ReceiptAdviceSearchDetail receiptAdviceSearchDetail : receiptAdviceSearchDetails) {
            List<ReceiptAdviceDetail> receiptAdviceDetails = receiptAdviceDetailRepository.getByReceiptAdviceId(receiptAdviceSearchDetail.getId());
            totalReceiptAdviceDetails.addAll(receiptAdviceDetails);
        }
        List<ReceiptAdviceDetailFullDto> totalReceiptAdviceDetailDtos = IReceiptAdviceDetailDtoMapper.INSTANCE.toReceiptAdviceDetailFullDtoList(totalReceiptAdviceDetails);

        for (ReceiptAdviceDetailFullDto e : totalReceiptAdviceDetailDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getReceiptAdvice().getCode() + "." + formatter.format(e.getReceiptAdvice().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getReceiptAdvice().getCode());
            generalJournalReportDto.setCreatedDate(e.getReceiptAdvice().getCreatedDate());
            generalJournalReportDto.setNumber(e.getReceiptAdvice().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getReceiptAdvice().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getReceiptAdvice().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getReceiptAdvice().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getReceiptAdvice().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getReceiptAdvice().getDescription());
            generalJournalReportDto.setContraAccountId(e.getDebitAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setCreditAmount(total);
            generalJournalReportDto.setDetailDescription(e.getDescription());
            getByReceiptAdviceDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByDebtClearingDetailDtoList(List<GeneralJournalReportDto> getByDebtClearingDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<DebtClearingSearchDetail> debtClearingSearchDetails = debtClearingDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<DebtClearingDetail> totalDebtClearingDetails = new ArrayList<>();
        for (DebtClearingSearchDetail debtClearingSearchDetail : debtClearingSearchDetails) {
            List<DebtClearingDetail> debtClearingDetails = debtClearingDetailRepository.getDebtClearingById(debtClearingSearchDetail.getId());
            totalDebtClearingDetails.addAll(debtClearingDetails);
        }
        List<DebtClearingDetailFullDto> totalDebtClearingDetailDtos = IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailFullDtoList(totalDebtClearingDetails);

        for (DebtClearingDetailFullDto e : totalDebtClearingDetailDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getDebtClearing().getCode() + "." + formatter.format(e.getDebtClearing().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getDebtClearing().getCode());
            generalJournalReportDto.setCreatedDate(e.getDebtClearing().getCreatedDate());
            generalJournalReportDto.setNumber(e.getDebtClearing().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getDebtClearing().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getDebtClearing().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getDebtClearing().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getCustomerDebt().getId());
            generalJournalReportDto.setDescription(e.getDebtClearing().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setDebitAmount(total);
            generalJournalReportDto.setDetailDescription(e.getDescription());
            getByDebtClearingDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByExportingReturnTransactionDtoList(List<GeneralJournalReportDto> getByExportingReturnDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ExportingReturnSearchDetail> exportingReturnSearchDetails = exportingReturnDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ExportingReturnTransaction> totalExportingReturnTransactions = new ArrayList<>();
        for (ExportingReturnSearchDetail exportingReturnSearchDetail : exportingReturnSearchDetails) {
            List<ExportingReturnTransaction> exportingReturnTransactions = exportingReturnTransactionRepository.getByExportingReturnId(exportingReturnSearchDetail.getId());
            totalExportingReturnTransactions.addAll(exportingReturnTransactions);
        }
        List<ExportingReturnTransactionFullDto> totalExportingReturnTransactionDtos = IExportingReturnTransactionDtoMapper.INSTANCE.toExportingReturnTransactionFullDtos(totalExportingReturnTransactions);

        for (ExportingReturnTransactionFullDto e : totalExportingReturnTransactionDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getExportingReturn().getCode() + "." + formatter.format(e.getExportingReturn().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getExportingReturn().getCode());
            generalJournalReportDto.setCreatedDate(e.getExportingReturn().getCreatedDate());
            generalJournalReportDto.setNumber(e.getExportingReturn().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getExportingReturn().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getExportingReturn().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getExportingReturn().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getExportingReturn().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getExportingReturn().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setCreditAmount(total);
            generalJournalReportDto.setMerchandiseId(e.getMerchandise().getId());
            generalJournalReportDto.setDetailDescription(e.getMerchandise().getName());
            getByExportingReturnDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getByImportingReturnTransactionDtoList(List<GeneralJournalReportDto> getByImportingReturnDetailDtoList, LicenseSearchDto search, List<String> customerIds, List<String> merchandiseIds) {
        List<ImportingReturnSearchDetail> importingReturnSearchDetails = importingReturnDao.report(
                search.getFromDate(),
                search.getToDate(),
                search.getCode(),
                search.getAccountingTableId(),
                customerIds,
                merchandiseIds,
                search.getAgencyId()
        );


        List<ImportingReturnTransaction> totalImportingReturnTransactions = new ArrayList<>();
        for (ImportingReturnSearchDetail importingReturnSearchDetail : importingReturnSearchDetails) {
            List<ImportingReturnTransaction> importingReturnTransactions = importingReturnTransactionRepository.getByImportingId(importingReturnSearchDetail.getId());
            totalImportingReturnTransactions.addAll(importingReturnTransactions);
        }
        List<ImportingReturnTransactionFullDto> totalImportingReturnTransactionDtos = IImportingReturnTransactionDtoMapper.INSTANCE.toImportingReturnTransactionFullDtos(totalImportingReturnTransactions);

        for (ImportingReturnTransactionFullDto e : totalImportingReturnTransactionDtos) {
            GeneralJournalReportDto generalJournalReportDto = new GeneralJournalReportDto();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String fullCode = e.getImportingReturn().getCode() + "." + formatter.format(e.getImportingReturn().getCreatedDate());
            generalJournalReportDto.setFullCode(fullCode);
            generalJournalReportDto.setCode(e.getImportingReturn().getCode());
            generalJournalReportDto.setCreatedDate(e.getImportingReturn().getCreatedDate());
            generalJournalReportDto.setNumber(e.getImportingReturn().getNumber());
            generalJournalReportDto.setInvoiceDate(e.getImportingReturn().getInvoiceDate());
            generalJournalReportDto.setInvoiceCode(e.getImportingReturn().getInvoiceCode());
            generalJournalReportDto.setInvoiceNumber(e.getImportingReturn().getInvoiceNumber());
            generalJournalReportDto.setCustomerId(e.getImportingReturn().getTransactionCustomerId());
            generalJournalReportDto.setDescription(e.getImportingReturn().getDescription());
            generalJournalReportDto.setContraAccountId(e.getCreditAccount().getId());
            Double total = e.getAmount();
            generalJournalReportDto.setDebitAmount(total);
            generalJournalReportDto.setMerchandiseId(e.getMerchandise().getId());
            generalJournalReportDto.setDetailDescription(e.getMerchandise().getName());
            getByImportingReturnDetailDtoList.add(generalJournalReportDto);
        }
        return CompletableFuture.completedFuture(true);
    }
}
