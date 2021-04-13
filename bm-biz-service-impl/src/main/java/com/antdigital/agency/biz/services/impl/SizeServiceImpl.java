package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.Size;
import com.antdigital.agency.dal.repository.ISizeRepository;
import com.antdigital.agency.dtos.response.SizeDto;
import com.antdigital.agency.mappers.ISizeDtoMapper;
import com.antdigital.agency.services.ISizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SizeServiceImpl implements ISizeService {
    private static final Logger logger = LoggerFactory.getLogger(SizeServiceImpl.class);

    @Autowired
    private ISizeRepository sizeRepository;

    @Override
    public List<SizeDto> findAll() {
        List<Size> sizesList = sizeRepository.findAll();
        return ISizeDtoMapper.INSTANCE.toSizeDtoList(sizesList);
    }
}
