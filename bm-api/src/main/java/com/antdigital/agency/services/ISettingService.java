package com.antdigital.agency.services;

import com.antdigital.agency.dtos.response.SettingsDto;

import java.util.List;

public interface ISettingService {
    List<SettingsDto> findAll(String agencyId);
    List<SettingsDto> update(List<SettingsDto> settings);
    SettingsDto getWarehouseId();
}
