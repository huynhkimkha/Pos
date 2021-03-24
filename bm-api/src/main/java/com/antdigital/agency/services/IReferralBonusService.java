package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReferralBonusSearchDto;
import com.antdigital.agency.dtos.response.*;

import java.util.List;

public interface IReferralBonusService {
    List<ReferralBonusDto> findAllByCollaboratorRefId(String collaboratorRefId);
    List<ReferralBonusDto> getNotCompleteByCollaboratorRefId(String collaboratorRefId);
    BaseSearchDto<List<ReferralBonusDto>> findAllByCollaboratorRefId(ReferralBonusSearchDto searchDto, String agencyId);
    List<ReferralBonusDto> findAllByEmployeeRefId(String employeeRefId);
    List<ReferralBonusDto> getBonusNotCompleteByEmployeeRefId(String employeeRefId);
    BaseSearchDto<List<ReferralBonusDto>> findAllByEmployeeRefId(ReferralBonusSearchDto searchDto);
    ReferralBonusDto getByCollaboratorId(String id);
}
