package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.IBillProductSizeRepository;
import com.antdigital.agency.dal.repository.IBillRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements IBillService {
    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Autowired
    private IBillRepository billRepository;

    @Autowired
    private IBillProductSizeRepository billProductSizeRepository;

    @Override
    public List<BillDto> findAll(String agencyId) {
        List<Bill> billList = billRepository.findAllByAgency(agencyId);
        return IBillDtoMapper.INSTANCE.toBillDtoList(billList);
    }

    @Override
    public BaseSearchDto<List<BillDto>> findAll(BaseSearchDto<List<BillDto>> searchDto, String agencyId) {
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

        Page<Bill> page = billRepository.findAllPageByAgency(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IBillDtoMapper.INSTANCE.toBillDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public BillFullDto getBillFull(String billId) {
        try {
            Bill bill = billRepository.findById(billId).get();
            List<BillProductSize> details = billProductSizeRepository.getByBillId(bill.getId());
            List<BillProductSizeDto> detailDto = IBillProductSizeDtoMapper.INSTANCE.toBillProductSizeDtoList(details);
            BillFullDto billFullDto = IBillDtoMapper.INSTANCE.toBillFullDto(bill);
            billFullDto.setBillProductSizeList(detailDto);
            return billFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public BillFullDto insert(BillFullDto billFullDto) {
        try {
            Bill bill = IBillDtoMapper.INSTANCE.toBill(billFullDto);
            bill.setId(UUIDHelper.generateType4UUID().toString());
            bill = billRepository.save(bill);

            for(BillProductSizeDto detail : billFullDto.getBillProductSizeList()) {
                if (detail.getProductSize() == null || detail.getProductSize().getId() == null
                        || detail.getProductSize().getId().isEmpty()) {
                    continue;
                }

                BillProductSize tempDetail = IBillProductSizeDtoMapper.INSTANCE.toBillProductSize(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setBill(bill);

                billProductSizeRepository.save(tempDetail);
            }

            billFullDto.setId(bill.getId());
            return billFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }


    @Override
    @Transactional
    public BillFullDto update(BillFullDto billDto) {
        try {
            Bill old = billRepository.findById(billDto.getId()).get();
            Bill bill = IBillDtoMapper.INSTANCE.toBill(billDto);

            billRepository.save(bill);
            // collect detail was removed.
            List<BillProductSize> billProductList = billProductSizeRepository.getByBillId(billDto.getId());
            List<String> detailDelete = new ArrayList<>();
            for(BillProductSize item : billProductList) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = billDto.getBillProductSizeList().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = detailDelete.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    detailDelete.add(item.getId());
                }
            }
            for(String id : detailDelete) {
                billProductSizeRepository.deleteById(id);
            }

            for(BillProductSizeDto billProductDto : billDto.getBillProductSizeList()) {
                if (billProductDto.getId() == null || billProductDto.getId().isEmpty()) {
                    BillDto billDto1 = new BillDto();
                    billDto1.setId(billDto.getId());

                    billProductDto.setId(UUIDHelper.generateType4UUID().toString());
                    billProductDto.setBill(billDto1);
                }
                BillProductSize billProduct = IBillProductSizeDtoMapper.INSTANCE.toBillProductSize(billProductDto);
                billProduct = billProductSizeRepository.save(billProduct);
                billProductDto = IBillProductSizeDtoMapper.INSTANCE.toBillProductSizeDto(billProduct);
            }

            return billDto;
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
            BillFullDto billFullDto = this.getBillFull(id);
            for(BillProductSizeDto detailDto : billFullDto.getBillProductSizeList()) {
                billProductSizeRepository.deleteById(detailDto.getId());
            }
            billRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Bill result = billRepository.getBillNumber(sdf.parse(createdDate), agencyId);
            if (result == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(createdDate));
                int month = cal.get(Calendar.MONTH) + 1;
                String monthStr = month > 9 ? String.valueOf(month) : "0" + month;
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

}
