package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.dao.ICostDao;
import com.antdigital.agency.dal.data.DateCostDetail;
import com.antdigital.agency.dal.data.MonthCostDetail;
import com.antdigital.agency.dal.data.YearCostDetail;
import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dal.entity.ImportingMaterial;
import com.antdigital.agency.dal.repository.ICostRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.ICostDtoMapper;
import com.antdigital.agency.mappers.IDateCostDetailDtoMapper;
import com.antdigital.agency.mappers.IMonthCostDetailDtoMapper;
import com.antdigital.agency.mappers.IYearCostDetailDtoMapper;
import com.antdigital.agency.services.ICostService;
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
public class CostServiceImpl implements ICostService {
    private static final Logger logger = LoggerFactory.getLogger(CostServiceImpl.class);

    @Autowired
    private ICostRepository costRepository;

    @Autowired
    private ICostDao costDao;

    @Override
    public List<CostDto> findAll(String agencyId) {
        List<Cost> costs = costRepository.findAllByAgency(agencyId);
        return ICostDtoMapper.INSTANCE.toCostDtoList(costs);
    }

    @Override
    public BaseSearchDto<List<CostDto>> findAll(BaseSearchDto<List<CostDto>> searchDto, String agencyId) {
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

        Page<Cost> page = costRepository.findAllPageByAgency(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ICostDtoMapper.INSTANCE.toCostDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public CostDto getCostById(String id) {
        Cost cost = costRepository.findById(id).get();
        return ICostDtoMapper.INSTANCE.toCostDto(cost);
    }

    @Transactional
    public CostDto insert(CostDto costDto) {
        try {
            Cost cost = ICostDtoMapper.INSTANCE.toCost(costDto);
            cost.setId(UUIDHelper.generateType4UUID().toString());
            Cost createdCost = costRepository.save(cost);
            return ICostDtoMapper.INSTANCE.toCostDto(createdCost);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public CostDto update(CostDto costDto) {
        try {
            Cost cost = ICostDtoMapper.INSTANCE.toCost(costDto);
            Cost updatedCost = costRepository.save(cost);
            return ICostDtoMapper.INSTANCE.toCostDto(updatedCost);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteCost(String id) {
        try {
            costRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public String getNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Cost result = costRepository.getCostNumber(sdf.parse(createdDate), agencyId);
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
    public List<MonthCostDetailDto> getMonthCost(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<MonthCostDetail> monthCostDetails = costDao.getMonthCost(fromDateNew, toDateNew, agencyId);
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
        for(MonthCostDetail monthCostDetail: monthCostDetails){
            List<MonthCostDetail> detailLst = monthCostDetailList.stream().filter(item -> item.getMonthDate() == monthCostDetail.getMonthDate()).collect(Collectors.toList());
            for (MonthCostDetail temp : detailLst) {
                temp.setTotal(monthCostDetail.getTotal());
            }
        }
        List<MonthCostDetailDto> monthCostDetailDtos = IMonthCostDetailDtoMapper.INSTANCE.toMonthCostDtoList(monthCostDetailList);

        return monthCostDetailDtos;
    }

    @Override
    public List<DateCostDetailDto> getDateCost(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<DateCostDetail> dateCostDetails = costDao.getDateCost(fromDateNew, toDateNew, agencyId);
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

        for(DateCostDetail dateCostDetail: dateCostDetails){
            List<DateCostDetail> detailLst = dateCostDetailList.stream().filter(item -> item.getDate() == dateCostDetail.getDate()).collect(Collectors.toList());
            for (DateCostDetail temp : detailLst) {
                temp.setTotal(dateCostDetail.getTotal());
            }
        }
        List<DateCostDetailDto> dateCostDetailDtos = IDateCostDetailDtoMapper.INSTANCE.toDateCostDtoList(dateCostDetailList);

        return dateCostDetailDtos;
    }

    @Override
    public List<YearCostDetailDto> getYearCost(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<YearCostDetail> yearCostDetails = costDao.getYearCost(fromDateNew, toDateNew, agencyId);
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

        for(YearCostDetail yearCostDetail: yearCostDetails) {
            List<YearCostDetail> detailLst = yearCostDetailList.stream().filter(item -> item.getYear() == yearCostDetail.getYear()).collect(Collectors.toList());
            for (YearCostDetail temp : detailLst) {
                temp.setTotal(yearCostDetail.getTotal());
            }
        }
        List<YearCostDetailDto> yearCostDetailDtos = IYearCostDetailDtoMapper.INSTANCE.toYearCostDtoList(yearCostDetailList);

        return yearCostDetailDtos;
    }

    @Override
    public List<CostDto> getCostStatistic(RangeDateDto rangeDateDto, String agencyId){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<Cost> costs = costRepository.findByrangeDate(fromDateNew, toDateNew, agencyId);
        return ICostDtoMapper.INSTANCE.toCostDtoList(costs);
    }
}
