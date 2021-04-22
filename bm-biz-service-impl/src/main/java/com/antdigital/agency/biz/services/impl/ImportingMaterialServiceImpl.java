package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.ImportingMaterial;
import com.antdigital.agency.dal.entity.ImportingTransaction;
import com.antdigital.agency.dal.repository.IImportingMaterialRepository;
import com.antdigital.agency.dal.repository.IImportingTransactionRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IImportingMaterialDtoMapper;
import com.antdigital.agency.mappers.IImportingTransactionDtoMapper;
import com.antdigital.agency.services.IImportingMaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportingMaterialServiceImpl implements IImportingMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(IImportingMaterialService.class);

    @Autowired
    private IImportingMaterialRepository importingMaterialRepository;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Override
    public List<ImportingMaterialDto> findAll(String agencyId) {
        List<ImportingMaterial> importingMaterialList = importingMaterialRepository.findAllByAgency(agencyId);
        return IImportingMaterialDtoMapper.INSTANCE.toImportingMaterialDtoList(importingMaterialList);
    }

    @Override
    public BaseSearchDto<List<ImportingMaterialDto>> findAll(BaseSearchDto<List<ImportingMaterialDto>> searchDto, String agencyId) {
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

        Page<ImportingMaterial> page = importingMaterialRepository.findAllPageByAgency(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IImportingMaterialDtoMapper.INSTANCE.toImportingMaterialDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public ImportingMaterialFullDto getFullById(String id) {
        ImportingMaterial importingMaterial = importingMaterialRepository.findById(id).get();
        ImportingMaterialFullDto importingMaterialFullDto = IImportingMaterialDtoMapper.INSTANCE.toImportingMaterialFullDto(importingMaterial);
        List<ImportingTransaction> importingTransactionList = importingTransactionRepository.getByImportingMaterialId(importingMaterialFullDto.getId());
        List<ImportingTransactionDto> importingTransactionDtoList = IImportingTransactionDtoMapper.INSTANCE.toImportingTransactionDtoList(importingTransactionList);
        importingMaterialFullDto.setImportingTransactionList(importingTransactionDtoList);
        return importingMaterialFullDto;
    }

    @Override
    @Transactional
    public ImportingMaterialFullDto insert(ImportingMaterialFullDto importingMaterialFullDto) {
        try {
            ImportingMaterial importingMaterial = IImportingMaterialDtoMapper.INSTANCE.toImportingMaterial(importingMaterialFullDto);
            importingMaterial.setId(UUIDHelper.generateType4UUID().toString());
            importingMaterial = importingMaterialRepository.save(importingMaterial);

            for(ImportingTransactionDto detail : importingMaterialFullDto.getImportingTransactionList()) {
                if (detail.getMaterial() == null || detail.getMaterial().getId() == null
                        || detail.getMaterial().getId().isEmpty()) {
                    continue;
                }

                ImportingTransaction tempDetail = IImportingTransactionDtoMapper.INSTANCE.toImportingTransaction(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setImportingMaterial(importingMaterial);

                importingTransactionRepository.save(tempDetail);
            }

            importingMaterialFullDto.setId(importingMaterial.getId());
            return importingMaterialFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }


    @Override
    @Transactional
    public ImportingMaterialFullDto update(ImportingMaterialFullDto importingMaterialDto) {
        try {
            ImportingMaterial old = importingMaterialRepository.findById(importingMaterialDto.getId()).get();
            ImportingMaterial importingMaterial = IImportingMaterialDtoMapper.INSTANCE.toImportingMaterial(importingMaterialDto);

            importingMaterialRepository.save(importingMaterial);
            // collect detail was removed.
            List<ImportingTransaction> importingTransactionList = importingTransactionRepository.getByImportingMaterialId(importingMaterialDto.getId());
            List<String> detailDelete = new ArrayList<>();
            for(ImportingTransaction item : importingTransactionList) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = importingMaterialDto.getImportingTransactionList().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = detailDelete.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    detailDelete.add(item.getId());
                }
            }
            for(String id : detailDelete) {
                importingTransactionRepository.deleteById(id);
            }

            for(ImportingTransactionDto importingTransactionDto : importingMaterialDto.getImportingTransactionList()) {
                if (importingTransactionDto.getId() == null || importingTransactionDto.getId().isEmpty()) {
                    ImportingMaterialDto importingMaterialDto1 = new ImportingMaterialDto();
                    importingMaterialDto1.setId(importingMaterialDto.getId());

                    importingTransactionDto.setId(UUIDHelper.generateType4UUID().toString());
                    importingTransactionDto.setImportingMaterial(importingMaterialDto1);
                }
                ImportingTransaction importingTransaction = IImportingTransactionDtoMapper.INSTANCE.toImportingTransaction(importingTransactionDto);
                importingTransaction = importingTransactionRepository.save(importingTransaction);
                importingTransactionDto = IImportingTransactionDtoMapper.INSTANCE.toImportingTransactionDto(importingTransaction);
            }

            return importingMaterialDto;
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
            ImportingMaterialFullDto importingMaterialFullDto = this.getFullById(id);
            for(ImportingTransactionDto detailDto : importingMaterialFullDto.getImportingTransactionList()) {
                importingTransactionRepository.deleteById(detailDto.getId());
            }
            importingMaterialRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
