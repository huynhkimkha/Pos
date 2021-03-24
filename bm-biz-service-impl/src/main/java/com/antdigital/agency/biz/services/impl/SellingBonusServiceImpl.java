package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.ReferralBonus;
import com.antdigital.agency.dal.entity.SellingBonus;
import com.antdigital.agency.dal.repository.ISellingBonusRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReferralBonusSearchDto;
import com.antdigital.agency.dtos.request.SellingBonusSearchDto;
import com.antdigital.agency.dtos.response.ReferralBonusDto;
import com.antdigital.agency.dtos.response.SellingBonusDto;
import com.antdigital.agency.mappers.IReferralBonusDtoMapper;
import com.antdigital.agency.mappers.ISellingBonusDtoMapper;
import com.antdigital.agency.services.IReferralBonusService;
import com.antdigital.agency.services.ISellingBonusService;
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
public class SellingBonusServiceImpl implements ISellingBonusService {
    private static final Logger logger = LoggerFactory.getLogger(SellingBonusServiceImpl.class);

    @Autowired
    private ISellingBonusRepository sellingBonusRepository;

    @Override
    @Transactional
    public List<SellingBonusDto> findAllByCollaboratorRefId(String collaboratorRefId) {
        List<SellingBonus> referralBonuses = sellingBonusRepository.getByCollaboratorRef(collaboratorRefId);
        return ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(referralBonuses);
    }

    @Override
    @Transactional
    public List<SellingBonusDto> getBonusNotCompleteByCollaboratorRef(String collaboratorRefId) {
        List<SellingBonus> referralBonuses = sellingBonusRepository.getBonusNotCompleteByCollaboratorRef(collaboratorRefId);
        return ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(referralBonuses);
    }

    @Override
    public BaseSearchDto<List<SellingBonusDto>> findAllByCollaboratorRefId(SellingBonusSearchDto searchDto, String agencyId) {
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

        Page<SellingBonus> page = sellingBonusRepository.getByCollaboratorRefPaging(request, searchDto.getRefereeId(), agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(page.getContent()));

        return searchDto;
    }

    @Override
    @Transactional
    public List<SellingBonusDto> findAllByEmployeeRefId(String employeeRefId) {
        List<SellingBonus> referralBonuses = sellingBonusRepository.getByEmployeeRef(employeeRefId);
        return ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(referralBonuses);
    }

    @Override
    @Transactional
    public List<SellingBonusDto> getBonusNotCompleteByEmployeeRef(String employeeRefId) {
        List<SellingBonus> referralBonuses = sellingBonusRepository.getBonusNotCompleteByEmployeeRef(employeeRefId);
        return ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(referralBonuses);
    }

    @Override
    public BaseSearchDto<List<SellingBonusDto>> findAllByEmployeeRefId(SellingBonusSearchDto searchDto) {
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

        Page<SellingBonus> page = sellingBonusRepository.getByEmployeeRefPaging(request, searchDto.getRefereeId());
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(page.getContent()));

        return searchDto;
    }
}
