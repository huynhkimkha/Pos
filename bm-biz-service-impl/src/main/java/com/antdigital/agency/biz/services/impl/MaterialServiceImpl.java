package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Material;
import com.antdigital.agency.dal.repository.IMaterialRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.MaterialDto;
import com.antdigital.agency.mappers.IMaterialDtoMapper;
import com.antdigital.agency.services.IMaterialService;
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
public class MaterialServiceImpl implements IMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(MaterialServiceImpl.class);

    @Autowired
    IMaterialRepository materialRepository;

    @Override
    public List<MaterialDto> findAll() {
        List<Material> materialList = materialRepository.findAll();
        return IMaterialDtoMapper.INSTANCE.toMaterialDtoList(materialList);
    }

    @Override
    public BaseSearchDto<List<MaterialDto>> findAll(BaseSearchDto<List<MaterialDto>> searchDto) {
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

        Page<Material> page = materialRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IMaterialDtoMapper.INSTANCE.toMaterialDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public MaterialDto getById(String id) {
        Material material = materialRepository.findById(id).get();
        return IMaterialDtoMapper.INSTANCE.toMaterialDto(material);
    }

    @Override
    @Transactional
    public MaterialDto insert(MaterialDto materialDto) {
        try {
            Material material = IMaterialDtoMapper.INSTANCE.toMaterial(materialDto);
            material.setId(UUIDHelper.generateType4UUID().toString());
            material = materialRepository.save(material);
            materialDto.setId(material.getId());
            return materialDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public MaterialDto update(MaterialDto materialDto) {
        try {
            Material material = IMaterialDtoMapper.INSTANCE.toMaterial(materialDto);
            materialRepository.save(material);
            return materialDto;
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
            materialRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public List<MaterialDto> getLikeName(String name) {
        List<Material> productList = materialRepository.getLikeName(name);
        return IMaterialDtoMapper.INSTANCE.toMaterialDtoList(productList);
    }
}
