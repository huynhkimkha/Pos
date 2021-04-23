package com.antdigital.agency.biz.services.impl;


import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dal.entity.CostCategory;
import com.antdigital.agency.dal.repository.ICostCategoryRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;
import com.antdigital.agency.dtos.response.CostCategoryDto;
import com.antdigital.agency.mappers.IAgencyDtoMapper;
import com.antdigital.agency.mappers.ICostCategoryDtoMapper;
import com.antdigital.agency.services.ICostCategoryService;
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
public class CostCategoryServiceImpl implements ICostCategoryService {
    private static final Logger logger = LoggerFactory.getLogger(SizeServiceImpl.class);

    @Autowired
    private ICostCategoryRepository costCategoryRepository;

    @Override
    public List<CostCategoryDto> findAll() {
        List<CostCategory> costCategoryList = costCategoryRepository.findAll();
        return ICostCategoryDtoMapper.INSTANCE.toCostCategoryDtoList(costCategoryList);
    }


    @Override
    public BaseSearchDto<List<CostCategoryDto>> findAll(BaseSearchDto<List<CostCategoryDto>> searchDto) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll());
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<CostCategory> page = costCategoryRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ICostCategoryDtoMapper.INSTANCE.toCostCategoryDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public CostCategoryDto getCostCategoryById(String id) {
        CostCategory costCategory = costCategoryRepository.findById(id).get();
        return ICostCategoryDtoMapper.INSTANCE.toCostCategoryDto(costCategory);
    }

    @Transactional
    public CostCategoryDto insert(CostCategoryDto costCategoryDto) {
        try {
            CostCategory costCategory = ICostCategoryDtoMapper.INSTANCE.toCostCategory(costCategoryDto);
            costCategory.setId(UUIDHelper.generateType4UUID().toString());
            CostCategory createdCostCategory = costCategoryRepository.save(costCategory);
            return ICostCategoryDtoMapper.INSTANCE.toCostCategoryDto(createdCostCategory);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public CostCategoryDto update(CostCategoryDto costCategoryDto) {
        try {
            CostCategory costCategory = ICostCategoryDtoMapper.INSTANCE.toCostCategory(costCategoryDto);
            CostCategory updatedCostCategory = costCategoryRepository.save(costCategory);
            return ICostCategoryDtoMapper.INSTANCE.toCostCategoryDto(updatedCostCategory);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteCostCategory(String id) {
        try {
            costCategoryRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
