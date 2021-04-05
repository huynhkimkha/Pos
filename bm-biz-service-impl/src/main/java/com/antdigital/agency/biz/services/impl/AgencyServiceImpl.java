package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dal.repository.IAgencyRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;
import com.antdigital.agency.mappers.IAgencyDtoMapper;
import com.antdigital.agency.services.IAgencyService;
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
public class AgencyServiceImpl implements IAgencyService {
    private static final Logger logger = LoggerFactory.getLogger(AgencyServiceImpl.class);

    @Autowired
    private IAgencyRepository agencyRepository;

    @Override
    public List<AgencyDto> findAll() {
        List<Agency> agencys = agencyRepository.findAll();
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agencys);
    }

    @Override
    public BaseSearchDto<List<AgencyDto>> findAll(BaseSearchDto<List<AgencyDto>> searchDto) {
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

        Page<Agency> page = agencyRepository.findAll(request);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IAgencyDtoMapper.INSTANCE.toAgencyDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public AgencyDto getAgencyById(String id) {
        Agency agency = agencyRepository.findById(id).get();
        return IAgencyDtoMapper.INSTANCE.toAgencyDto(agency);
    }

    @Transactional
    public AgencyDto insert(AgencyDto agencyDto) {
        try {
            Agency agency = IAgencyDtoMapper.INSTANCE.toAgency(agencyDto);
            agency.setId(UUIDHelper.generateType4UUID().toString());
            Agency createdAgency = agencyRepository.save(agency);
            return IAgencyDtoMapper.INSTANCE.toAgencyDto(createdAgency);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public AgencyDto update(AgencyDto agencyDto) {
        try {
            Agency agency = IAgencyDtoMapper.INSTANCE.toAgency(agencyDto);
            Agency updatedAgency = agencyRepository.save(agency);
            return IAgencyDtoMapper.INSTANCE.toAgencyDto(updatedAgency);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Transactional
    public boolean deleteAgency(String id) {
        try {
            agencyRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
