package com.antdigital.agency.biz.services.impl;


import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.AccountingTableModel;
import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.core.service.impl.WarehouseRequestService;
import com.antdigital.agency.dal.dao.IDebtClearingDao;
import com.antdigital.agency.dal.data.DebtClearingSearchDetail;
import com.antdigital.agency.dal.data.SearchResult;
import com.antdigital.agency.dal.entity.DebtClearing;
import com.antdigital.agency.dal.entity.DebtClearingDetail;
import com.antdigital.agency.dal.entity.ExportingWarehouse;
import com.antdigital.agency.dal.repository.IDebtClearingDetailRepository;
import com.antdigital.agency.dal.repository.IDebtClearingRepository;
import com.antdigital.agency.dal.repository.IExportingTransactionRepository;
import com.antdigital.agency.dal.repository.IExportingWarehouseRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IDebtClearingDetailDtoMapper;
import com.antdigital.agency.mappers.IDebtClearingDtoMapper;
import com.antdigital.agency.services.IDebtClearingService;
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
public class DebtClearingServiceImpl implements IDebtClearingService {
    private static final Logger logger = LoggerFactory.getLogger(DebtClearingServiceImpl.class);

    @Autowired
    private IDebtClearingDetailRepository debtClearingDetailRepository;

    @Autowired
    private IDebtClearingRepository debtClearingRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IExportingWarehouseRepository exportingWarehouseRepository;

    @Autowired
    private WarehouseRequestService warehouseRequestService;

    @Autowired
    private IDebtClearingDao debtClearingDao;

    @Override
    public List<DebtClearingDto> findAll(String agencyId) {
        List<DebtClearing> debtClearings = debtClearingRepository.findAll(agencyId);
        return IDebtClearingDtoMapper.INSTANCE.toDebtClearingDtoList(debtClearings);
    }

    @Override
    public DebtClearingSearchDto search(HttpServletRequest request, DebtClearingSearchDto debtClearingSearchDto, String agencyId) throws IOException, JAXBException {

        // get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if (debtClearingSearchDto.getCustomerCode() != null && !debtClearingSearchDto.getCustomerCode().isEmpty()) {
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, debtClearingSearchDto.getCustomerCode());
            for (CustomerModel item : customerModelList) {
                customerIds.add(item.getId());
            }
        }
        // get customer debt id from warehouse service
        List<String> customerDebtIds = new ArrayList<>();
        if (debtClearingSearchDto.getCustomerDebtCode() != null && !debtClearingSearchDto.getCustomerDebtCode().isEmpty()) {
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, debtClearingSearchDto.getCustomerDebtCode());
            for (CustomerModel item : customerModelList) {
                customerDebtIds.add(item.getId());
            }
        }

        // get exporting warehouse ids from code
        List<String> exportingWarehouseIds = new ArrayList<>();
        if (debtClearingSearchDto.getExportingWarehouseCode() != null && !debtClearingSearchDto.getExportingWarehouseCode().isEmpty()) {
            List<ExportingWarehouse> exportingWarehouseDtoList = exportingWarehouseRepository.getLikeCode(debtClearingSearchDto.getExportingWarehouseCode(), agencyId);
            for (ExportingWarehouse item : exportingWarehouseDtoList) {
                exportingWarehouseIds.add(item.getId());
            }
        }

        SearchResult<List<DebtClearingSearchDetail>> result = debtClearingDao.search(
                debtClearingSearchDto.getCode(),
                debtClearingSearchDto.getNumber(),
                debtClearingSearchDto.getDescription(),
                debtClearingSearchDto.getNote(),
                exportingWarehouseIds,
                customerIds,
                customerDebtIds,
                debtClearingSearchDto.getStartDate(),
                debtClearingSearchDto.getEndDate(),
                debtClearingSearchDto.getCreatedDateSort(),
                debtClearingSearchDto.getStartNumber(),
                debtClearingSearchDto.getEndNumber(),
                debtClearingSearchDto.getCurrentPage(),
                debtClearingSearchDto.getRecordOfPage(),
                agencyId
        );

        // get customer and exporting warehouse id
        List<DebtClearingDetail> debtClearingList = new ArrayList<>();
        for (DebtClearingSearchDetail debtClearingSearchDetail : result.getResult()) {
            debtClearingList.add(debtClearingDetailRepository.getDebtClearingById(debtClearingSearchDetail.getId()).stream().iterator().next());
        }
        List<CustomerModel> customerModel = warehouseRequestService.getAllCustomers(request);
        for (DebtClearingSearchDetail debtClearingSearchDetail : result.getResult()) {
            DebtClearingDetail detail = debtClearingList.stream().filter(t -> t.getDebtClearing().getId().equals(debtClearingSearchDetail.getId())).findFirst().orElse(null);
            if (detail != null) {
                CustomerModel customer = customerModel.stream().filter(t -> t.getId().equals(detail.getCustomerId())).findFirst().get();
                CustomerModel customerDebt = customerModel.stream().filter(t -> t.getId().equals(detail.getCustomerDebtId())).findFirst().get();
                debtClearingSearchDetail.setCustomerId(customer.getId());
                debtClearingSearchDetail.setCustomerCode(customer.getCode());
                debtClearingSearchDetail.setCustomerName(customer.getFullName());
                debtClearingSearchDetail.setCustomerDebtId(customerDebt.getId());
                debtClearingSearchDetail.setCustomerDebtCode(customerDebt.getCode());
                debtClearingSearchDetail.setCustomerDebtName(customerDebt.getFullName());
                ExportingWarehouse exportingWarehouse = new ExportingWarehouse();
                exportingWarehouse.setId(detail.getExportingWarehouse() != null ? detail.getExportingWarehouse().getId() : null);
                debtClearingSearchDetail.setExportingWarehouse(exportingWarehouse);
            }
        }

        debtClearingSearchDto.setTotalRecords(result.getTotalRecords());
        debtClearingSearchDto.setResult(IDebtClearingDtoMapper.INSTANCE.toDebtClearingDetailDto(result.getResult()));
        return debtClearingSearchDto;
    }

    @Override
    public BaseSearchDto<List<DebtClearingDto>> findAll(BaseSearchDto<List<DebtClearingDto>> searchDto, String agencyId) {
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

        Page<DebtClearing> page = debtClearingRepository.findAll(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IDebtClearingDtoMapper.INSTANCE.toDebtClearingDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public DebtClearingDto getById(String id, String agencyId) {
        DebtClearing debtClearing = debtClearingRepository.findById(id).get();
        return IDebtClearingDtoMapper.INSTANCE.toDebtClearingDto(debtClearing);
    }

    @Override
    public DebtClearingDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        DebtClearing debtClearing = debtClearingRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IDebtClearingDtoMapper.INSTANCE.toDebtClearingDto(debtClearing);
    }

    @Override
    public DebtClearingFullDto getFullById(HttpServletRequest request, String id, String agencyId) throws IOException, JAXBException {
        DebtClearing debtClearing = debtClearingRepository.findById(id).get();
        DebtClearingFullDto debtClearingFullDto = IDebtClearingDtoMapper.INSTANCE.toDebtClearingFullDto(debtClearing);
        List<DebtClearingDetail> debtClearingDetails = debtClearingDetailRepository.getDebtClearingById(id);
        debtClearingFullDto.setDebtClearingDetails(IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailFullDtoList(debtClearingDetails));

        CompletableFuture<Boolean> getCustomers = getCustomers(request, debtClearingFullDto);
        CompletableFuture<Boolean> getAccounting = getAccounting(request, debtClearingFullDto);

        CompletableFuture.allOf (
                getCustomers,
                getAccounting
        );

        return debtClearingFullDto;
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, DebtClearingFullDto debtClearingFullDto)
            throws IOException, JAXBException {
        for (DebtClearingDetailFullDto debtClearingDetailFullDto: debtClearingFullDto.getDebtClearingDetails()){
            List<String> ids = new ArrayList<>();

            ids.add(debtClearingDetailFullDto.getCustomerDebt().getId());
            ids.add(debtClearingDetailFullDto.getCustomer().getId());

            List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
            for (CustomerModel customer: customerModels) {
                if (debtClearingDetailFullDto.getCustomerDebt() != null && debtClearingDetailFullDto.getCustomerDebt().getId() != null) {
                    if (debtClearingDetailFullDto.getCustomerDebt().getId().equals(customer.getId())) {
                        debtClearingDetailFullDto.setCustomerDebt(customer);
                    }
                }
                if (debtClearingDetailFullDto.getCustomer() != null && debtClearingDetailFullDto.getCustomer().getId() != null) {
                    if (debtClearingDetailFullDto.getCustomer().getId().equals(customer.getId())) {
                        debtClearingDetailFullDto.setCustomer(customer);
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getAccounting(HttpServletRequest request, DebtClearingFullDto debtClearingFullDto)
            throws IOException, JAXBException {
        List<DebtClearingDetailFullDto> debtClearingDetailFulls = debtClearingFullDto.getDebtClearingDetails();
        List<String> ids = new ArrayList<>();
        for(DebtClearingDetailFullDto item : debtClearingDetailFulls) {
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
            for(DebtClearingDetailFullDto item : debtClearingDetailFulls) {
                if (item.getCreditAccount().getId().equals(account.getId())) {
                    item.setCreditAccount(account);
                }

                if (item.getDebitAccount().getId().equals(account.getId())) {
                    item.setDebitAccount(account);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Override
    public String getDebtClearingNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DebtClearing result = debtClearingRepository.getDebtClearingNumber(sdf.parse(createdDate), agencyId);
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
    public int countByExportId(String exportId) {
        int count = debtClearingDetailRepository.countByExportingWarehouseId(exportId);
        return count;
    }

    @Override
    @Transactional
    public DebtClearingFullDto insert(DebtClearingFullDto debtClearingFullDto, String agencyId) {
        try {
            DebtClearing debtClearing = IDebtClearingDtoMapper.INSTANCE.toDebtClearing(debtClearingFullDto);
            debtClearing.setId(UUIDHelper.generateType4UUID().toString());
            debtClearingRepository.save(debtClearing);
            debtClearingFullDto.setId(debtClearing.getId());

            CompletableFuture<Boolean> saveDebtClearingDetail = saveDebtClearingDetail(debtClearingFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(debtClearingFullDto, null);
            CompletableFuture.allOf (
                    saveDebtClearingDetail,
                    updateExportingWarehousePaymentStatus
            );
            updateExportingWarehousePaymentStatus(debtClearingFullDto, null);
            return debtClearingFullDto;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public DebtClearingFullDto update(DebtClearingFullDto debtClearingFullDto, String agencyId) {
        try {
            List<DebtClearingDetail> debtClearingDetails = debtClearingDetailRepository.getDebtClearingById(debtClearingFullDto.getId());
            List<DebtClearingDetailDto> debtClearingDetailDtos = IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailDtoList(debtClearingDetails);
            DebtClearing debtClearing = IDebtClearingDtoMapper.INSTANCE.toDebtClearing(debtClearingFullDto);
            debtClearingRepository.save(debtClearing);
            CompletableFuture<Boolean> saveDebtClearingDetail = saveDebtClearingDetail(debtClearingFullDto);
            CompletableFuture<Boolean> updateExportingWarehousePaymentStatus = updateExportingWarehousePaymentStatus(debtClearingFullDto, debtClearingDetailDtos);
            CompletableFuture.allOf (
                    saveDebtClearingDetail,
                    updateExportingWarehousePaymentStatus
            );
            return debtClearingFullDto;
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
            DebtClearing debtClearing = debtClearingRepository.findById(id).get();
            List<DebtClearingDetail> debtClearingDetails = debtClearingDetailRepository.getDebtClearingById(id);
            List<DebtClearingDetailDto> debtClearingDetailDtos = IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailDtoList(debtClearingDetails);
            DebtClearingFullDto debtClearingFullDto = IDebtClearingDtoMapper.INSTANCE.toDebtClearingFullDto(debtClearing);
            debtClearingFullDto.setDebtClearingDetails(IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetailFullDtoList(debtClearingDetails));

            for (DebtClearingDetail item : debtClearingDetails) {
                debtClearingDetailRepository.deleteById(item.getId());
            }
            debtClearingRepository.deleteById(id);

            updateExportingWarehousePaymentStatus(debtClearingFullDto, debtClearingDetailDtos);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Async
    CompletableFuture<Boolean> saveDebtClearingDetail(DebtClearingFullDto debtClearingFullDto) {
        List<DebtClearingDetail> oldDebtClearingDetails = debtClearingDetailRepository.getDebtClearingById(debtClearingFullDto.getId());
        List<DebtClearingDetailFullDto> debtClearingDetails = debtClearingFullDto.getDebtClearingDetails();
        DebtClearing debtClearing = IDebtClearingDtoMapper.INSTANCE.toDebtClearing(debtClearingFullDto);

        for (DebtClearingDetailFullDto item : debtClearingDetails) {
            DebtClearingDetail debtClearingDetail = IDebtClearingDetailDtoMapper.INSTANCE.toDebtClearingDetail(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                debtClearingDetail.setId(UUIDHelper.generateType4UUID().toString());
            }
            if (item.getExportingWarehouse() != null
                    && (item.getExportingWarehouse().getId() == null || item.getExportingWarehouse().getId().isEmpty())) {
                debtClearingDetail.setExportingWarehouse(null);
            }
            debtClearingDetail.setDebtClearing(debtClearing);
            DebtClearingDetail created = debtClearingDetailRepository.save(debtClearingDetail);
            item.setId(created.getId());
        }

        if (oldDebtClearingDetails != null) {
            for (DebtClearingDetail item : oldDebtClearingDetails) {
                int index = debtClearingDetails.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    debtClearingDetailRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    CompletableFuture<Boolean> updateExportingWarehousePaymentStatus(DebtClearingFullDto debtClearingFullDto, List<DebtClearingDetailDto> oldDebtClearingDetails) {
        List<DebtClearingDetail> debtClearingDetails = debtClearingDetailRepository.getHasExporting(debtClearingFullDto.getId());
        List<String> exportIds = debtClearingDetails.stream().map(t -> t.getExportingWarehouse().getId()).distinct().collect(Collectors.toList());

        for (String id : exportIds) {
            Double total = exportingTransactionRepository.getTotal(id);
            Double totalDebtClearing = debtClearingDetailRepository.getTotalByExportingWarehouseId(id);
            totalDebtClearing = totalDebtClearing != null ? totalDebtClearing : 0;

            PaymentStatusEnum paymentStatusEnum = totalDebtClearing >= total ? PaymentStatusEnum.COMPLETED : PaymentStatusEnum.UNCOMPLETED;
            ExportingWarehouse exportingWarehouse = exportingWarehouseRepository.findById(id).get();
            if (exportingWarehouse.getPaymentStatus() != paymentStatusEnum) {
                exportingWarehouse.setPaymentStatus(paymentStatusEnum);
                exportingWarehouseRepository.save(exportingWarehouse);
            }
        }

        if (oldDebtClearingDetails != null && oldDebtClearingDetails.size() > 0) {
            for (DebtClearingDetailDto item : oldDebtClearingDetails) {
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
