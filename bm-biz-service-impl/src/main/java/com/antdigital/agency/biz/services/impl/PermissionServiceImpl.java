package com.antdigital.agency.biz.services.impl;
import com.antdigital.agency.common.constant.Constant;
import com.antdigital.agency.core.service.IEzSoftwareRequestService;
import com.antdigital.agency.dal.entity.GrantPermission;
import com.antdigital.agency.dal.entity.Permission;
import com.antdigital.agency.dal.entity.Role;
import com.antdigital.agency.dal.repository.IAgencyRepository;
import com.antdigital.agency.dal.repository.IGrantPermissionRepository;
import com.antdigital.agency.dal.repository.IPermissionRepository;
import com.antdigital.agency.dal.repository.IRoleRepository;
import com.antdigital.agency.dtos.response.PermissionDto;
import com.antdigital.agency.mappers.IPermissionDtoMapper;
import com.antdigital.agency.services.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements IPermissionService {

    @Autowired
    private IPermissionRepository permissionRepository;

    @Autowired
    private IAgencyRepository agencyRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IGrantPermissionRepository grantPermissionRepository;

    @Autowired
    private IEzSoftwareRequestService ezSoftwareRequestService;

    @Override
    @Transactional
    public List<PermissionDto> findAll() {
        List<Permission> permissions = permissionRepository.findAll();
        permissions.sort(Comparator.comparing(Permission::getRequirement));
        return IPermissionDtoMapper.INSTANCE.toPermissionDtoList(permissions);
    }

    @Override
    public List<PermissionDto> getAllByLicense(HttpServletRequest request, String companyId) throws IOException, JAXBException {
        List<String> permissionCodes = ezSoftwareRequestService.getLicensePermissionCodes(request, companyId, Constant.softId);
        List<Permission> permissions = permissionRepository.getByCodeList(permissionCodes);
        return IPermissionDtoMapper.INSTANCE.toPermissionDtoList(permissions);
    }

    @Override
    public List<PermissionDto> getAllOnLicense(String companyId) {
        List<PermissionDto> permissionDtoList = this.findAll();

        List<String> agencyList = agencyRepository.findAll(companyId).stream()
                .map(agency -> agency.getId()).collect(Collectors.toList());

        List<Role> roleList = roleRepository.findAllByNameAndAgencyIdIn("GLOBAL ADMIN", agencyList);
        if (roleList == null || roleList.size() == 0) {
            return null;
        }

        List<GrantPermission> grantPermissionList = grantPermissionRepository.getRoleId(roleList.get(0).getId());

        permissionDtoList = permissionDtoList.stream().filter(permissionDto -> {
            for (GrantPermission item : grantPermissionList) {
                if (item.getPermission().getCode().equals(permissionDto.getCode())) {
                    return true;
                }
            }

            return false;
        }).collect(Collectors.toList());

        return permissionDtoList;
    }

}
