package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;

import java.util.List;

public interface IAgencyService {
    BaseSearchDto<List<AgencyDto>> findAll(BaseSearchDto<List<AgencyDto>> searchDto);
    List<AgencyDto> findAll();
    AgencyDto getAgencyById(String id);
    AgencyDto update(AgencyDto agencyDto);
    AgencyDto insert(AgencyDto agencyDto);
    boolean deleteAgency(String id);
}
