package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;

import java.util.List;

public interface IAgencyService {
    List<AgencyDto> getByCompanyId(String companyId);
    AgencyDto findOne(String agencyId);
    List<AgencyDto> findAll(String companyId);
    BaseSearchDto<List<AgencyDto>> findAll(BaseSearchDto<List<AgencyDto>> searchDto, String companyId);
    AgencyDto getAgencyById(String agencyId);
    List<AgencyDto> getAgencyByName(String agencyName, String companyId);
    AgencyDto getAgencyByExactName(String agencyName, String companyId);
    List<AgencyDto> getLikeCodeOrName(String name, String companyId);
    List<AgencyDto> getDifferentByOrgCodeLike(String orgCode, String agencyId, String companyId);
    AgencyDto getAgencyByOrgCode(String orgCode, String companyId);
    AgencyDto insert(AgencyDto agencyDto);
    AgencyDto update(AgencyDto agencyDto);
    boolean delete(String id);
}
