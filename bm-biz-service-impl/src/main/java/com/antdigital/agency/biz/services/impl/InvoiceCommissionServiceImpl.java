package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.InvoiceCommission;
import com.antdigital.agency.dal.repository.IInvoiceCommissionRepository;
import com.antdigital.agency.dal.repository.ISellingBonusRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.InvoiceCommissionDto;
import com.antdigital.agency.mappers.IInvoiceCommissionDtoMapper;
import com.antdigital.agency.services.IInvoiceCommissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Service
public class InvoiceCommissionServiceImpl implements IInvoiceCommissionService {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceCommissionServiceImpl.class);

    @Autowired
    private IInvoiceCommissionRepository invoiceCommissionRepository;
    @Autowired
    private ISellingBonusRepository sellingBonusRepository;

    @Override
    public BaseSearchDto<List<InvoiceCommissionDto>> findAll(BaseSearchDto<List<InvoiceCommissionDto>> searchDto, String companyId) {
        if (searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(companyId));
            return searchDto;
        }

        Sort sort = null;
        if (searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy())
                    : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<InvoiceCommission> page = invoiceCommissionRepository.findAll(request, companyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionList(page.getContent()));

        return searchDto;
    }

    @Override
    @Transactional
    public List<InvoiceCommissionDto> findAll(String companyId) {
        List<InvoiceCommission> invoiceCommissions = invoiceCommissionRepository.findAll(companyId);
        return IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionList(invoiceCommissions);
    }

    @Override
    public InvoiceCommissionDto getById(String id) {
        try {
            InvoiceCommission invoiceCommission = invoiceCommissionRepository.findById(id).get();
            return IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionDto(invoiceCommission);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public InvoiceCommissionDto getByRevenue(Double minRevenue, String companyId) {
        try {
            InvoiceCommission invoiceCommission = invoiceCommissionRepository.findByMinRevenue(minRevenue, companyId);
            return IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionDto(invoiceCommission);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public InvoiceCommissionDto getByName(String name, String companyId) {
        try {
            InvoiceCommission invoiceCommission = invoiceCommissionRepository.findByName(name, companyId);
            return IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionDto(invoiceCommission);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public InvoiceCommissionDto getExactByObjectAndRevenue(String applyObject, Double minRevenue, String companyId) {
        try {
            InvoiceCommission invoiceCommission = invoiceCommissionRepository.getExactByObjectAndRevenue(applyObject, minRevenue, companyId);
            return IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommissionDto(invoiceCommission);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public List<String> getIdByPrice(Double price, String companyId) {
        // Get InvoiceCommission list sort descending by minRevenue
        List<InvoiceCommission> invoiceCommissions = invoiceCommissionRepository.findAll(Sort.by(Sort.Direction.DESC, "minRevenue"));
        List<String> result = new ArrayList<>();

        for (InvoiceCommission item : invoiceCommissions) {
            if (price >= item.getMinRevenue() && item.getCompanyId().equals(companyId)) {
                result.add(item.getId());
            }
        }

        return result;
    }

    @Override
    public InvoiceCommissionDto insert(InvoiceCommissionDto invoiceCommissionDto) {
        try {
            InvoiceCommission invoiceCommission = IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommission(invoiceCommissionDto);
            invoiceCommission.setId(UUIDHelper.generateType4UUID().toString());
            invoiceCommissionRepository.save(invoiceCommission);

            return invoiceCommissionDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public InvoiceCommissionDto update(InvoiceCommissionDto invoiceCommissionDto) {
        try {
            InvoiceCommission invoiceCommission = IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommission(invoiceCommissionDto);
            invoiceCommissionRepository.save(invoiceCommission);

            return invoiceCommissionDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            invoiceCommissionRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
