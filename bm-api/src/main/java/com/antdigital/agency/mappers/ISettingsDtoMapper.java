package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Settings;
import com.antdigital.agency.dtos.response.SettingsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ISettingsDtoMapper {
    ISettingsDtoMapper INSTANCE = Mappers.getMapper( ISettingsDtoMapper.class );

    SettingsDto toSettingsDto(Settings settings);

    Settings toSettings(SettingsDto settingsDto);

    List<SettingsDto> toSettingsDtoList(List<Settings> settingsList);

    List<Settings> toSettingsList(List<SettingsDto> settingsDtos);
}
