package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.dal.entity.Settings;
import com.antdigital.agency.dal.repository.ISettingsRepository;
import com.antdigital.agency.dtos.response.SettingsDto;
import com.antdigital.agency.mappers.ISettingsDtoMapper;
import com.antdigital.agency.services.ISettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class SettingServiceImpl implements ISettingService {
    private static final Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);

    @Autowired
    private ISettingsRepository settingsRepository;

    @Override
    public List<SettingsDto> findAll() {
        List<Settings> settings = settingsRepository.findAll();
        return ISettingsDtoMapper.INSTANCE.toSettingsDtoList(settings);
    }

    @Transactional
    public List<SettingsDto> update(List<SettingsDto> settingsDtos) {
        try {
            List<Settings> settings = ISettingsDtoMapper.INSTANCE.toSettingsList(settingsDtos);
            // Init updateDate of all settings
            int settingLength = settings.size();
            for (int index = 0; index < settingLength; index++) {
                System.out.println(settings.get(index).toString());
            }

            List<Settings> updatedSettings = settingsRepository.saveAll(settings);

            return ISettingsDtoMapper.INSTANCE.toSettingsDtoList(updatedSettings);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

}
