package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Cost;
import com.antdigital.agency.dal.repository.ICostRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CostDto;
import com.antdigital.agency.mappers.ICostDtoMapper;
import com.antdigital.agency.services.ICostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CostServiceImpl implements ICostService {
    private static final Logger logger = LoggerFactory.getLogger(CostServiceImpl.class);

    @Autowired
    private ICostRepository costRepository;

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
}
