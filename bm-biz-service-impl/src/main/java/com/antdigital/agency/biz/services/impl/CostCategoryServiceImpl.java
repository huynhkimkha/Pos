package com.antdigital.agency.biz.services.impl;


import com.antdigital.agency.dal.entity.CostCategory;
import com.antdigital.agency.dal.repository.ICostCategoryRepository;
import com.antdigital.agency.dtos.response.CostCategoryDto;
import com.antdigital.agency.mappers.ICostCategoryDtoMapper;
import com.antdigital.agency.services.ICostCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
