package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.constant.Constant;
import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.BlockStatusEnum;
import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.enums.SettingKeysEnum;
import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.common.utils.RandomTextHelper;
import com.antdigital.agency.core.models.warehouse.SaleRankModel;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.entity.Collaborator;
import com.antdigital.agency.dal.entity.ReferralBonus;
import com.antdigital.agency.dal.entity.SellingBonus;
import com.antdigital.agency.dal.entity.Settings;
import com.antdigital.agency.dal.repository.ICollaboratorRepository;
import com.antdigital.agency.dal.repository.IReferralBonusRepository;
import com.antdigital.agency.dal.repository.ISellingBonusRepository;
import com.antdigital.agency.dal.repository.ISettingsRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.dtos.response.CollaboratorDto;
import com.antdigital.agency.dtos.response.CollaboratorFullDto;
import com.antdigital.agency.dtos.response.CollaboratorTempDto;
import com.antdigital.agency.dtos.response.ReferralBonusDto;
import com.antdigital.agency.mappers.ICollaboratorDtoMapper;
import com.antdigital.agency.mappers.IReferralBonusDtoMapper;
import com.antdigital.agency.mappers.ISellingBonusDtoMapper;
import com.antdigital.agency.services.ICollaboratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.antdigital.agency.common.utils.UUIDHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class CollaboratorServiceImpl implements ICollaboratorService {
    private static final Logger logger = LoggerFactory.getLogger(CollaboratorServiceImpl.class);

    @Autowired
    private ICollaboratorRepository collaboratorRepository;

    @Autowired
    private IReferralBonusRepository referralBonusRepository;

    @Autowired
    private ISellingBonusRepository sellingBonusRepository;

    @Autowired
    private ISettingsRepository settingsRepository;
    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Override
    @Transactional
    public List<CollaboratorDto> findAll(String agencyId) {
        List<Collaborator> collaborators = collaboratorRepository.findAllByAgencyId(agencyId);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDtoList(collaborators);
    }

    @Override
    public BaseSearchDto<List<CollaboratorDto>> findAll(BaseSearchDto<List<CollaboratorDto>> searchDto, String agencyId) {
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

        Page<Collaborator> page = collaboratorRepository.findAllBaseSearchByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(ICollaboratorDtoMapper.INSTANCE.toCollaboratorDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public CollaboratorSearchDto findAllAffiliate(CollaboratorSearchDto searchDto, String agencyId) {
        List<CollaboratorDetailDto> collaboratorDetailDtos = new ArrayList<>();
        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Collaborator> page = collaboratorRepository.findAllNonActivated(request, agencyId);
        List<Collaborator> collaborators = page.getContent();

        List<String> collaboratorIds = new ArrayList<>();
        for (Collaborator c : collaborators) {
            collaboratorIds.add(c.getId());
            c.setPassword(null);
        }
        List<CollaboratorDto> collaboratorDtos = ICollaboratorDtoMapper.INSTANCE.toCollaboratorDtoList(collaborators);
        collaboratorDetailDtos = ICollaboratorDtoMapper.INSTANCE.toCollaboratorDetailDtoList(collaboratorDtos);

        // get Referee and Referral Bonus
        List<ReferralBonus> referralBonuses = referralBonusRepository.getByCollaboratorAndCollaboratorRef(collaboratorIds);
        List<ReferralBonusDto> referralBonusDtos = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);

        // get Selling Bonus
        List<SellingBonus> sellingBonuses = sellingBonusRepository.getByCollaboratorsUnpaid(collaboratorIds);
        List<SellingBonusDto> sellingBonusDtos = ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(sellingBonuses);
        for (CollaboratorDetailDto collaboratorDetailDto : collaboratorDetailDtos) {
            Double bonus = 0D;
            ReferralBonusDto refereeDto = referralBonusDtos.stream().filter(c -> c.getCollaborator() != null && c.getCollaborator().getId() != null && c.getCollaborator().getId().equals(collaboratorDetailDto.getId())).findFirst().orElse(null);
            collaboratorDetailDto.setReferee(refereeDto);
            for (SellingBonusDto bonusDto : sellingBonusDtos) {
                if (bonusDto.getCollaborator() != null && bonusDto.getCollaborator().getId().equals(collaboratorDetailDto.getId())
                || bonusDto.getCollaboratorRef() != null && bonusDto.getCollaboratorRef().getId().equals(collaboratorDetailDto.getId())){
                    bonus += bonusDto.getAmount();
                }
            }
            for (ReferralBonusDto refBonusDto : referralBonusDtos) {
                if (refBonusDto.getCollaboratorRef() != null && refBonusDto.getCollaboratorRef().getId().equals(collaboratorDetailDto.getId()) && refBonusDto.getPaymentStatus() != PaymentStatusEnum.COMPLETED) {
                    bonus += refBonusDto.getAmount();
                }
            }
            collaboratorDetailDto.setUnpaidBonus(bonus);
        }

        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(collaboratorDetailDtos);

        return searchDto;
    }

    @Override
    public CollaboratorDto getByEmail(String email, String companyId) {
        Collaborator collaborator = collaboratorRepository.getByEmail(email, companyId);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDto(collaborator);
    }

    @Override
    public CollaboratorDto getByPhone(String phone, String companyId) {
        Collaborator collaborator = collaboratorRepository.getByPhone(phone, companyId);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDto(collaborator);
    }

    @Override
    public CollaboratorDto getById(String collaboratorId) {
        Collaborator collaborator = collaboratorRepository.findById(collaboratorId).get();
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDto(collaborator);
    }

    @Override
    public List<CollaboratorDto> getLikeName(String name, String agencyId) {
        List<Collaborator> collaborators = collaboratorRepository.getLikeName(name, agencyId);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDtoList(collaborators);
    }

    @Override
    public List<CollaboratorTempDto> getCollaboratorsById(List<String> ids) {
        List<Collaborator> collaboratorList = collaboratorRepository.getCollaboratorsById(ids);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorTempDtoList(collaboratorList);
    }

    @Override
    public CollaboratorFullDto getFullById(HttpServletRequest request, String collaboratorId) {
        try {
            Collaborator collaborator = collaboratorRepository.findById(collaboratorId).get();
            CollaboratorFullDto collaboratorFullDto = ICollaboratorDtoMapper.INSTANCE.toCollaboratorFullDto(collaborator);

            ReferralBonus referee = referralBonusRepository.getByCollaborator(collaboratorId);
            ReferralBonusDto refereeDto = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDto(referee);
            List<ReferralBonus> referralBonuses = referralBonusRepository.getByCollaboratorRef(collaboratorId);
            List<ReferralBonusDto> referralBonusDtos = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);

            CompletableFuture<Boolean> getSaleRanks = getSaleRanks(request, collaboratorFullDto);
            CompletableFuture.allOf (
                    getSaleRanks
            );
            collaboratorFullDto.setReferee(refereeDto);
            collaboratorFullDto.setReferralSales(referralBonusDtos);
            return collaboratorFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public List<CollaboratorDto> getCollaboratorRefLikeNameOrEmail (String collaboratorName, String agencyId) {
        List<Collaborator> collaborators = collaboratorRepository.getCollaboratorLikeNameOrEmail(collaboratorName, agencyId);
        return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDtoList(collaborators);
    }

    @Override
    @Transactional
    public CollaboratorFullDto register(CollaboratorFullDto collaboratorFullDto) {
        try {
            Collaborator collaborator = ICollaboratorDtoMapper.INSTANCE.toCollaborator(collaboratorFullDto);
            collaborator.setId(UUIDHelper.generateType4UUID().toString());
            collaborator.setActivatedStatus(ActivatedStatusEnum.NONACTIVATED);
            collaborator.setBlockedStatus(BlockStatusEnum.APPROVED);
            Collaborator createdCollaborator = collaboratorRepository.save(collaborator);
            if (collaboratorFullDto.getReferee() != null) {
                ReferralBonus referee = IReferralBonusDtoMapper.INSTANCE.toReferralBonus(collaboratorFullDto.getReferee());
                Collaborator collaborator1 = new Collaborator();
                collaborator1.setId(createdCollaborator.getId());
                referee.setCollaborator(collaborator1);
                referee.setId(UUIDHelper.generateType4UUID().toString());
                referee.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                referee.setActivatedStatus(ActivatedStatusEnum.NONACTIVATED);
                try {
                    if (referee.getCollaboratorRef() != null) {
                        referee.setAmount(Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.COLLABORATOR_REF_BONUS.toString()).getValues()));
                    } else {
                        referee.setAmount(Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.EMPLOYEE_REF_BONUS.toString()).getValues()));
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    logger.error(ex.getStackTrace().toString());
                    collaboratorRepository.deleteById(createdCollaborator.getId());
                    return null;
                }
                referralBonusRepository.save(referee);
            }
            collaboratorFullDto.setId(createdCollaborator.getId());
            return collaboratorFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public CollaboratorFullDto insert(CollaboratorFullDto collaboratorFullDto) {
        try {
            Collaborator collaborator = ICollaboratorDtoMapper.INSTANCE.toCollaborator(collaboratorFullDto);
            collaborator.setId(UUIDHelper.generateType4UUID().toString());
            collaborator.setActivatedStatus(ActivatedStatusEnum.ACTIVATED);
            collaborator.setBlockedStatus(BlockStatusEnum.APPROVED);
            Collaborator createdCollaborator = collaboratorRepository.save(collaborator);
            if (collaboratorFullDto.getReferee() != null) {
                collaboratorFullDto.getReferee().setEmployee(null);
                ReferralBonus referee = IReferralBonusDtoMapper.INSTANCE.toReferralBonus(collaboratorFullDto.getReferee());
                Collaborator collaborator1 = new Collaborator();
                collaborator1.setId(createdCollaborator.getId());
                referee.setCollaborator(collaborator1);
                referee.setId(UUIDHelper.generateType4UUID().toString());
                referee.setPaymentStatus(PaymentStatusEnum.UNCOMPLETED);
                referee.setActivatedStatus(ActivatedStatusEnum.ACTIVATED);
                referee.setAmount(Constant.REF_BONUS);

                if (referee.getCollaboratorRef() != null && referee.getCollaboratorRef().getId() != null
                        && !referee.getCollaboratorRef().getId().isEmpty())  {
                    Settings refLimit = settingsRepository.getByKey("COLLABORATOR_REF_LIMIT");
                    List<ReferralBonus> refList = referralBonusRepository.getByCollaboratorRefWithPayment(referee.getCollaboratorRef().getId());
                    if (refList.size() >= Integer.parseInt(refLimit.getValues())){
                        referee.setAmount(0.0d);
                        referee.setPaymentStatus(null);
                    }
                } else {
                    referee.setCollaboratorRef(null);
                }
                if (referee.getEmployeeRef() != null && referee.getEmployeeRef().getId() != null
                        && !referee.getEmployeeRef().getId().isEmpty()) {
                    Settings refLimit = settingsRepository.getByKey("EMPLOYEE_REF_LIMIT");
                    List<ReferralBonus> refList = referralBonusRepository.getByEmployeeRefWithPayment(referee.getEmployeeRef().getId());
                    if (refList.size() >= Integer.parseInt(refLimit.getValues())){
                        referee.setAmount(0.0d);
                        referee.setPaymentStatus(null);
                    }
                } else {
                    referee.setEmployeeRef(null);
                }
                referralBonusRepository.save(referee);
            }
            collaboratorFullDto.setId(createdCollaborator.getId());
            return collaboratorFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public CollaboratorFullDto update(CollaboratorFullDto collaboratorFullDto) {
        try {
            Collaborator collaborator = ICollaboratorDtoMapper.INSTANCE.toCollaborator(collaboratorFullDto);
            Collaborator oldCollaborator = collaboratorRepository.getOne(collaboratorFullDto.getId());
            collaborator.setPassword(oldCollaborator.getPassword());
            BlockStatusEnum blockStatus = collaborator.getBlockedStatus() == BlockStatusEnum.BLOCKED ? BlockStatusEnum.BLOCKED : BlockStatusEnum.APPROVED;
            collaborator.setBlockedStatus(blockStatus);
            Collaborator createdCollaborator = collaboratorRepository.save(collaborator);
            collaboratorFullDto.setId(createdCollaborator.getId());

            if (collaborator.getActivatedStatus() == ActivatedStatusEnum.ACTIVATED) {
                ReferralBonus referee = referralBonusRepository.getByCollaborator(collaborator.getId());
                if (referee != null && referee.getId() != null) {
                    Collaborator collaborator1 = new Collaborator();
                    collaborator1.setId(createdCollaborator.getId());
                    referee.setCollaborator(collaborator1);
                    referee.setActivatedStatus(ActivatedStatusEnum.ACTIVATED);
                } else {
                    referee.setActivatedStatus(ActivatedStatusEnum.NONACTIVATED);
                }

                if (referee.getCollaboratorRef() != null && referee.getCollaboratorRef().getId() != null
                        && !referee.getCollaboratorRef().getId().isEmpty())  {
                    Settings refLimit = settingsRepository.getByKey("COLLABORATOR_REF_LIMIT");
                    List<ReferralBonus> refList = referralBonusRepository.getByCollaboratorRefWithPayment(referee.getCollaboratorRef().getId());
                    if (refList.size() > Integer.parseInt(refLimit.getValues())){
                        referee.setAmount(0.0d);
                        referee.setPaymentStatus(null);
                    }
                } else {
                    referee.setCollaboratorRef(null);
                }

                if (referee.getEmployeeRef() != null && referee.getEmployeeRef().getId() != null
                        && !referee.getEmployeeRef().getId().isEmpty()) {
                    Settings refLimit = settingsRepository.getByKey("EMPLOYEE_REF_LIMIT");
                    List<ReferralBonus> refList = referralBonusRepository.getByEmployeeRefWithPayment(referee.getEmployeeRef().getId());
                    if (refList.size() > Integer.parseInt(refLimit.getValues())){
                        referee.setAmount(0.0d);
                        referee.setPaymentStatus(null);
                    }
                } else {
                    referee.setEmployeeRef(null);
                }

                Collaborator collaborator1 = new Collaborator();
                collaborator1.setId(createdCollaborator.getId());
                referee.setCollaborator(collaborator1);
                referee.setEmployee(null);
                referralBonusRepository.save(referee);
            }

            return collaboratorFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(HttpServletRequest request ,String id) {
        try {
            ReferralBonus referralBonus = referralBonusRepository.getByCollaborator(id);
            referralBonusRepository.deleteById(referralBonus.getId());
            collaboratorRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    @Transactional
    public CollaboratorDto changePassword(String userName, ChangePasswordDto changePasswordDto, String companyId) {
        try {
            // get Collaborator by userName
            Collaborator collaborator = collaboratorRepository.getByEmail(userName, companyId);
            collaborator.setPassword(BCryptHelper.encode(changePasswordDto.getNewPassword()));

            Collaborator updatedCollaborator = collaboratorRepository.save(collaborator);

            return ICollaboratorDtoMapper.INSTANCE.toCollaboratorDto(updatedCollaborator);
        }
        catch (Exception ex){
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public String resetPassword(String collaboratorId) {
        try {
            Collaborator collaborator = collaboratorRepository.getOne(collaboratorId);
            String newPassword = RandomTextHelper.generateRandomPassword(7);
            String cryptPassword = BCryptHelper.encode(newPassword);
            collaborator.setPassword(cryptPassword);
            Collaborator updatedCollaborator = collaboratorRepository.save(collaborator);
            return newPassword;
        }
        catch (Exception ex){
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Async
    CompletableFuture<Boolean> getSaleRanks(HttpServletRequest request, CollaboratorFullDto collaboratorDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(collaboratorDto.getSaleRank().getId());
        List<SaleRankModel> saleRankModels = warehouseRequestService.getSaleRanks(request, ids);
        for (SaleRankModel saleRank: saleRankModels) {
            if (collaboratorDto.getSaleRank() != null && collaboratorDto.getSaleRank().getId() != null) {
                if (collaboratorDto.getSaleRank().getId().equals(saleRank.getId())) {
                    collaboratorDto.setSaleRank(saleRank);
                }
            }
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public Double getBonusByRangeDate(RangeDateDto rangeDateDto, String collaboratorId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = format1.format(rangeDateDto.getFromDate());
        String toDate = format1.format(rangeDateDto.getToDate());
        Double totalBonus = 0D;
        Double referralBonus = referralBonusRepository.getTotalByRangeDateAndCollaboratorRefId(fromDate, toDate, collaboratorId);
        Double sellingBonus = sellingBonusRepository.getTotalByRangeDateAndCollaboratorRefId(fromDate, toDate, collaboratorId);
        referralBonus = referralBonus != null ? referralBonus : 0;
        sellingBonus = sellingBonus != null ? sellingBonus : 0;
        totalBonus = totalBonus + referralBonus + sellingBonus;
        return totalBonus;
    }
}
