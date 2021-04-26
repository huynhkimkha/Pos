package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.dao.IBillDao;
import com.antdigital.agency.dal.data.*;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements IBillService {
    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    @Autowired
    private IBillRepository billRepository;

    @Autowired
    private IBillProductSizeRepository billProductSizeRepository;

    @Autowired
    private IBillDao billDao;

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

    @Override
    public List<MonthBillDetailDto> getMonthBill(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<MonthBillDetail> monthBillDetails = billDao.getMonthBill(fromDateNew, toDateNew, agencyId);
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
        List<MonthBillDetail> monthBillDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            if (fromYear - toYear == 0){
                toMonth =  toCal.get(Calendar.MONTH) + 1;
            }
            while(fromMonth <= toMonth){
                MonthBillDetail monthBillDetail = new MonthBillDetail();
                monthBillDetail.setMonthDate(fromMonth);
                monthBillDetail.setYearDate(fromYear);
                monthBillDetail.setTotal(0);
                monthBillDetailList.add(monthBillDetail);
                fromMonth += 1;
            }
            fromMonth = 1;
            fromYear += 1;
        }
        for(MonthBillDetail monthBillDetail: monthBillDetails){
            List<MonthBillDetail> detailLst = monthBillDetailList.stream().filter(item -> item.getMonthDate() == monthBillDetail.getMonthDate()).collect(Collectors.toList());
            for (MonthBillDetail temp : detailLst) {
                temp.setTotal(monthBillDetail.getTotal());
            }
        }
        List<MonthBillDetailDto> monthBillDetailDtos = IMonthBillDetailDtoMapper.INSTANCE.toMonthBillDtoList(monthBillDetailList);

        return monthBillDetailDtos;
    }

    @Override
    public List<DateBillDetailDto> getDateBill(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<DateBillDetail> dateBillDetails = billDao.getDateBill(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromDate = fromCal.get(Calendar.DAY_OF_MONTH);
        Integer toDate =  toCal.get(Calendar.DAY_OF_MONTH);
        Integer month = fromCal.get(Calendar.MONTH) + 1;
        Integer year = fromCal.get(Calendar.YEAR);

        List<DateBillDetail> dateBillDetailList = new ArrayList<>();
        while(fromDate <= toDate){
            DateBillDetail dateBillDetail = new DateBillDetail();
            dateBillDetail.setDate(fromDate);
            dateBillDetail.setMonth(month);
            dateBillDetail.setYear(year);
            dateBillDetail.setTotal(0);
            dateBillDetailList.add(dateBillDetail);
            fromDate += 1;
        }

        for(DateBillDetail dateBillDetail: dateBillDetails){
            List<DateBillDetail> detailLst = dateBillDetailList.stream().filter(item -> item.getDate() == dateBillDetail.getDate()).collect(Collectors.toList());
            for (DateBillDetail temp : detailLst) {
                temp.setTotal(dateBillDetail.getTotal());
            }
        }
        List<DateBillDetailDto> dateBillDetailDtos = IDateBillDetailDtoMapper.INSTANCE.toDateBillDtoList(dateBillDetailList);

        return dateBillDetailDtos;
    }

    @Override
    public List<YearBillDetailDto> getYearBill(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<YearBillDetail> yearBillDetails = billDao.getYearBill(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear = toCal.get(Calendar.YEAR);

        List<YearBillDetail> yearBillDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            YearBillDetail yearBillDetail = new YearBillDetail();
            yearBillDetail.setYear(fromYear);
            yearBillDetail.setTotal(0);
            yearBillDetailList.add(yearBillDetail);
            fromYear += 1;
        }

        for(YearBillDetail yearBillDetail: yearBillDetails) {
            List<YearBillDetail> detailLst = yearBillDetailList.stream().filter(item -> item.getYear() == yearBillDetail.getYear()).collect(Collectors.toList());
            for (YearBillDetail temp : detailLst) {
                temp.setTotal(yearBillDetail.getTotal());
            }
        }
        List<YearBillDetailDto> yearBillDetailDtos = IYearBillDetailDtoMapper.INSTANCE.toYearBillDtoList(yearBillDetailList);

        return yearBillDetailDtos;
    }

}
