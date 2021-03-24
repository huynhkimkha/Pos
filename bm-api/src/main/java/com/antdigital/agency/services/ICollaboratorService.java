package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ICollaboratorService {
    List<CollaboratorDto> findAll(String agencyId);
    BaseSearchDto<List<CollaboratorDto>> findAll(BaseSearchDto<List<CollaboratorDto>> searchDto, String agencyId);
    CollaboratorSearchDto findAllAffiliate(CollaboratorSearchDto searchDto, String agencyId);
    List<CollaboratorDto> getLikeName(String name, String agencyId);
    CollaboratorDto getById(String collaboratorId);
    CollaboratorDto getByEmail(String email, String companyId);
    CollaboratorDto getByPhone(String phone, String companyId);
    CollaboratorFullDto getFullById(HttpServletRequest request, String collaboratorId);
    List<CollaboratorTempDto> getCollaboratorsById(List<String> ids);
    CollaboratorFullDto register(CollaboratorFullDto collaboratorFullDto);
    CollaboratorFullDto insert(CollaboratorFullDto collaboratorFullDto);
    CollaboratorFullDto update(CollaboratorFullDto collaboratorFullDto);
    CollaboratorDto changePassword(String userName, ChangePasswordDto changePasswordDto, String companyId);
    boolean delete(HttpServletRequest request ,String id);
    List<CollaboratorDto> getCollaboratorRefLikeNameOrEmail (String collaboratorName, String agencyId);
    Double getBonusByRangeDate(RangeDateDto rangeDateDto, String collaboratorId);
    String resetPassword(String collaboratorId);
}
