package com.antdigital.agency.biz.services.impl;


import com.antdigital.agency.dal.repository.IGrantPermissionRepository;
import com.antdigital.agency.dtos.response.GrantPermissionDto;
import com.antdigital.agency.mappers.IGrantPermissionDtoMapper;
import com.antdigital.agency.services.IGrantPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service
public class GrantPermissionServiceImpl implements IGrantPermissionService {
    @Autowired
    private IGrantPermissionRepository grantPermissionRepository;

    @Override
    @Transactional
    public List<GrantPermissionDto> getByRoleId(String roleId) {
        return IGrantPermissionDtoMapper.INSTANCE.toGrantPermissionDtoList(grantPermissionRepository.getRoleId(roleId));
    }
}
