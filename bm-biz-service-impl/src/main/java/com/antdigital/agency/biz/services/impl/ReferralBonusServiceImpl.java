package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.ReferralBonus;
import com.antdigital.agency.dal.repository.IReferralBonusRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReferralBonusSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IReferralBonusDtoMapper;
import com.antdigital.agency.services.IReferralBonusService;
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
public class ReferralBonusServiceImpl implements IReferralBonusService {
    private static final Logger logger = LoggerFactory.getLogger(ReferralBonusServiceImpl.class);

    @Autowired
    private IReferralBonusRepository referralBonusRepository;

    @Override
    @Transactional
    public List<ReferralBonusDto> findAllByCollaboratorRefId(String collaboratorRefId) {
        List<ReferralBonus> referralBonuses = referralBonusRepository.getByCollaboratorRef(collaboratorRefId);
        return IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);
    }

    @Override
    @Transactional
    public List<ReferralBonusDto> getNotCompleteByCollaboratorRefId(String collaboratorRefId) {
        List<ReferralBonus> referralBonuses = referralBonusRepository.getBonusNotCompleteByCollaboratorRef(collaboratorRefId);
        return IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);
    }

    @Override
    public BaseSearchDto<List<ReferralBonusDto>> findAllByCollaboratorRefId(ReferralBonusSearchDto searchDto, String agencyId) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAllByCollaboratorRefId(searchDto.getRefereeId()));
            return searchDto;
        }

        searchDto.setSortBy("paymentStatus");
        searchDto.setSortAsc(false);
        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<ReferralBonus> page = referralBonusRepository.getByCollaboratorRefPaging(request, searchDto.getRefereeId(), agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(page.getContent()));

        return searchDto;
    }

    @Override
    @Transactional
    public List<ReferralBonusDto> findAllByEmployeeRefId(String employeeRefId) {
        List<ReferralBonus> referralBonuses = referralBonusRepository.getByEmployeeRef(employeeRefId);
        return IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);
    }

    @Override
    @Transactional
    public List<ReferralBonusDto> getBonusNotCompleteByEmployeeRefId(String employeeRefId) {
        List<ReferralBonus> referralBonuses = referralBonusRepository.getBonusNotCompleteByEmployeeRef(employeeRefId);
        return IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);
    }

    @Override
    public BaseSearchDto<List<ReferralBonusDto>> findAllByEmployeeRefId(ReferralBonusSearchDto searchDto) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAllByCollaboratorRefId(searchDto.getRefereeId()));
            return searchDto;
        }

        searchDto.setSortBy("paymentStatus");
        searchDto.setSortAsc(false);
        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<ReferralBonus> page = referralBonusRepository.getByEmployeeRefPaging(request, searchDto.getRefereeId());
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(page.getContent()));

        return searchDto;
    }

    @Override
    @Transactional
    public ReferralBonusDto getByCollaboratorId(String id) {
        ReferralBonus referralBonus = referralBonusRepository.getByCollaborator(id);
        return IReferralBonusDtoMapper.INSTANCE.toReferralBonusDto(referralBonus);
    }
}
