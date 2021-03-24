package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.SettingKeysEnum;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dal.entity.Settings;
import com.antdigital.agency.dal.repository.IAgencyRepository;
import com.antdigital.agency.dal.repository.ISettingsRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AgencyServiceImpl implements IAgencyService {
    private static final Logger logger = LoggerFactory.getLogger(AgencyServiceImpl.class);

    @Autowired
    private IAgencyRepository agencyRepository;

    @Autowired
    private ISettingsRepository settingsRepository;

    @Override
    public List<AgencyDto> getByCompanyId(String companyId) {
        List<Agency> agencyList = agencyRepository.getByCompanyId(companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agencyList);
    }

    @Override
    public List<AgencyDto> findAll(String companyId) {
        List<Agency> agencyList = agencyRepository.findAll(companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agencyList);
    }

    @Override
    public AgencyDto findOne(String agencyId) {
        Agency agency = agencyRepository.findById(agencyId).get();
        return IAgencyDtoMapper.INSTANCE.toAgencyDto(agency);
    }

    @Override
    public BaseSearchDto<List<AgencyDto>> findAll(BaseSearchDto<List<AgencyDto>> searchDto, String companyId) {
        if (searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(companyId));
            return searchDto;
        }

        Sort sort = null;
        if (searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Agency> page = agencyRepository.findAll(request, companyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IAgencyDtoMapper.INSTANCE.toAgencyDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public AgencyDto getAgencyById(String agencyId) {
        Agency agency = agencyRepository.findById(agencyId).orElse(null);
        return IAgencyDtoMapper.INSTANCE.toAgencyDto(agency);
    }

    @Override
    public List<AgencyDto> getAgencyByName(String agencyName, String companyId) {
        List<Agency> agency = agencyRepository.getByName(agencyName, companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agency);
    }

    @Override
    public AgencyDto getAgencyByExactName(String agencyName, String companyId) {
        Agency agency = agencyRepository.getByExactName(agencyName, companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDto(agency);
    }

    @Override
    public List<AgencyDto> getLikeCodeOrName(String name, String companyId) {
        List<Agency> agencyList = agencyRepository.getLikeCodeOrName(name, companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agencyList);
    }

    @Override
    public List<AgencyDto> getDifferentByOrgCodeLike(String orgCode, String agencyId, String companyId) {
        List<Agency> agencyList = agencyRepository.getDifferentByOrgCodeLike(orgCode, agencyId, companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDtoList(agencyList);
    }

    @Override
    public AgencyDto getAgencyByOrgCode(String orgCode, String companyId) {
        Agency agency = agencyRepository.getByOrgCode(orgCode, companyId);
        return IAgencyDtoMapper.INSTANCE.toAgencyDto(agency);
    }

    @Override
    public AgencyDto insert(AgencyDto agencyDto) {
        try {
            agencyDto.setId(UUIDHelper.generateType4UUID().toString());
            Agency agency = IAgencyDtoMapper.INSTANCE.toAgency(agencyDto);
            agencyRepository.save(agency);

            CompletableFuture<Boolean> setDefaultSetting = setDefaultSetting(agency);

            CompletableFuture.allOf(setDefaultSetting);

            return agencyDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public AgencyDto update(AgencyDto agencyDto) {
        try {
            Agency agency = IAgencyDtoMapper.INSTANCE.toAgency(agencyDto);
            agencyRepository.save(agency);

            return agencyDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            agencyRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    CompletableFuture<Boolean> setDefaultSetting(Agency agency) {
        List<Settings> settingsList = settingsRepository.findAll();
        // Group by label
        HashSet<Object> seen = new HashSet<>();
        settingsList.removeIf(e -> !seen.add(e.getLabel()));

        for (Settings item : settingsList) {
            item.setId(null);
            item.setAgency(agency);
            if (item.getKey().equals(SettingKeysEnum.AGENCY.toString())) {
                item.setValues(agency.getId());
            }

            settingsRepository.save(item);
        }

        return CompletableFuture.completedFuture(true);
    }
}
