package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.SellingBonusSearchDto;
import com.antdigital.agency.dtos.response.SellingBonusDto;

import java.util.List;

public interface ISellingBonusService {
    List<SellingBonusDto> findAllByCollaboratorRefId(String collaboratorRefId);
    List<SellingBonusDto> getBonusNotCompleteByCollaboratorRef(String collaboratorRefId);
    BaseSearchDto<List<SellingBonusDto>> findAllByCollaboratorRefId(SellingBonusSearchDto searchDto, String agencyId);
    List<SellingBonusDto> findAllByEmployeeRefId(String employeeRefId);
    List<SellingBonusDto> getBonusNotCompleteByEmployeeRef(String employeeRefId);
    BaseSearchDto<List<SellingBonusDto>> findAllByEmployeeRefId(SellingBonusSearchDto searchDto);
}
