package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.Categories;
import com.antdigital.agency.dal.repository.ICategoriesRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CategoriesDto;
import com.antdigital.agency.mappers.ICategoriesDtoMapper;
import com.antdigital.agency.services.ICategoriesService;
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
public class CategoriesServiceImpl implements ICategoriesService {
    private static final Logger logger = LoggerFactory.getLogger(CategoriesServiceImpl.class);

    @Autowired
    ICategoriesRepository categoriesRepository;

    @Override
    public List<CategoriesDto> findAll() {
        List<Categories> categories = categoriesRepository.findAll();
        return ICategoriesDtoMapper.INSTANCE.toCategoríeDtoList(categories);
    }

    @Override
    public BaseSearchDto<List<CategoriesDto>> findAll(BaseSearchDto<List<CategoriesDto>> searchDto) {
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

        Page<Categories> page = categoriesRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ICategoriesDtoMapper.INSTANCE.toCategoríeDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public CategoriesDto getByName(String name) {
        Categories categories = categoriesRepository.getByName(name);
        return ICategoriesDtoMapper.INSTANCE.toCategoriesDto(categories);
    }

    @Override
    public CategoriesDto getById(String id) {
        Categories categories = categoriesRepository.findById(id).get();
        return ICategoriesDtoMapper.INSTANCE.toCategoriesDto(categories);
    }

    @Override
    public List<CategoriesDto> getLikeName(String name) {
        List<Categories> categoriesList = categoriesRepository.getLikeName(name);
        return ICategoriesDtoMapper.INSTANCE.toCategoríeDtoList(categoriesList);
    }

    @Override
    @Transactional
    public MerchandiseGroupDto insert(MerchandiseGroupDto merchandiseGroupDto) {
        try {
            MerchandiseGroup merchandiseGroup = IMerchandiseGroupDtoMapper.INSTANCE.toMerchandiseGroup(merchandiseGroupDto);
            merchandiseGroup.setId(UUIDHelper.generateType4UUID().toString());
            MerchandiseGroup createdMerchandiseGroup = merchandiseGroupRepository.save(merchandiseGroup);
            merchandiseGroupDto.setId(createdMerchandiseGroup.getId());
            return merchandiseGroupDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public MerchandiseGroupDto update(MerchandiseGroupDto merchandiseGroupDto) {
        try {
            MerchandiseGroup merchandiseGroup = IMerchandiseGroupDtoMapper.INSTANCE.toMerchandiseGroup(merchandiseGroupDto);
            merchandiseGroupRepository.save(merchandiseGroup);
            return merchandiseGroupDto;
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
            merchandiseGroupRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
