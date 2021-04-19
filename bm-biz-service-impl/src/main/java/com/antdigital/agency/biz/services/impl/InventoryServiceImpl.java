package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Inventory;
import com.antdigital.agency.dal.entity.Material;
import com.antdigital.agency.dal.repository.IInventoryRepository;
import com.antdigital.agency.dal.repository.IMaterialRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.InventoryDto;
import com.antdigital.agency.mappers.IInventoryDtoMapper;
import com.antdigital.agency.services.IInventoryService;
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
public class InventoryServiceImpl implements IInventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);


    @Autowired
    IInventoryRepository inventoryRepository;

    @Autowired
    IMaterialRepository materialRepository;

    @Override
    public List<InventoryDto> findAll(String agencyId) {
        List<Inventory> inventoryList = inventoryRepository.findAllByAgency(agencyId);
        return IInventoryDtoMapper.INSTANCE.toInventoryDtoList(inventoryList);
    }

    @Override
    public BaseSearchDto<List<InventoryDto>> findAll(BaseSearchDto<List<InventoryDto>> searchDto, String agencyId) {
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

        Page<Inventory> page = inventoryRepository.findAllPageByAgency(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IInventoryDtoMapper.INSTANCE.toInventoryDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public InventoryDto getById(String id) {
        Inventory inventory = inventoryRepository.findById(id).get();
        return IInventoryDtoMapper.INSTANCE.toInventoryDto(inventory);
    }

    @Override
    @Transactional
    public InventoryDto insert(InventoryDto inventoryDto) {
        try {
            Inventory inventory = IInventoryDtoMapper.INSTANCE.toInventory(inventoryDto);
            inventory.setId(UUIDHelper.generateType4UUID().toString());
            inventory = inventoryRepository.save(inventory);
            inventoryDto.setId(inventory.getId());
            return inventoryDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public InventoryDto update(InventoryDto inventoryDto) {
        try {
            Inventory inventory = IInventoryDtoMapper.INSTANCE.toInventory(inventoryDto);
            inventoryRepository.save(inventory);
            return inventoryDto;
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
            inventoryRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
