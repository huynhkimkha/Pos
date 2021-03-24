package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.GrantPermission;
import com.antdigital.agency.dal.entity.Role;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dal.repository.IGrantPermissionRepository;
import com.antdigital.agency.dal.repository.IRoleRepository;
import com.antdigital.agency.dtos.response.GrantPermissionDto;
import com.antdigital.agency.dtos.response.RoleDto;
import com.antdigital.agency.dtos.response.RoleFullDto;
import com.antdigital.agency.mappers.IGrantPermissionDtoMapper;
import com.antdigital.agency.mappers.IRoleDtoMapper;
import com.antdigital.agency.services.IRoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RoleServiceImpl implements IRoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IGrantPermissionRepository grantPermissionRepository;
    @Transactional
    public List<RoleDto> findAll(String agencyId) {
        List<Role> roles = roleRepository.findAllByAgencyId(agencyId);
        return IRoleDtoMapper.INSTANCE.toRoleDtoList(roles);
    }

    @Override
    @Transactional
    public BaseSearchDto<List<RoleDto>> findAll(BaseSearchDto<List<RoleDto>> searchDto, String agencyId) {
        if (searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(agencyId));
            return searchDto;
        }

        Sort sort = null;
        if (searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Role> page = roleRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IRoleDtoMapper.INSTANCE.toRoleDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public RoleFullDto getByName(String name, String agencyId) {
        Role roles = roleRepository.getByName(name, agencyId);
        return IRoleDtoMapper.INSTANCE.toRoleFullDto(roles);
    }

    @Override
    public RoleFullDto getRoleFull(String id) {
        Role roles = roleRepository.findById(id).get();
        RoleFullDto roleFullDto = IRoleDtoMapper.INSTANCE.toRoleFullDto(roles);
        roleFullDto.setGrantPermissions(new ArrayList<>());
        List<GrantPermission> grantPermissions =grantPermissionRepository.getRoleId(id);
        for(GrantPermission grantPermission : grantPermissions) {
            roleFullDto.getGrantPermissions().add(IGrantPermissionDtoMapper.INSTANCE.toGrantPermissionDto(grantPermission));
        }
        return roleFullDto;
    }
    @Override
    @Transactional
    public RoleFullDto insert(RoleFullDto roleFullDto) {
        try {
            Role roles = IRoleDtoMapper.INSTANCE.toRole(roleFullDto);
            roles.setId(UUIDHelper.generateType4UUID().toString());
            Role createdRole = roleRepository.save(roles);
            for (GrantPermissionDto grant : roleFullDto.getGrantPermissions()) {
                GrantPermission grantPermission = IGrantPermissionDtoMapper.INSTANCE.toGrantPermission(grant);
                grantPermission.setId(UUIDHelper.generateType4UUID().toString());
                grantPermission.setRole(createdRole);
                grantPermission = grantPermissionRepository.save(grantPermission);
                grant.setId(grantPermission.getId());
            }
            roleFullDto.setId(roles.getId());
            return roleFullDto;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public RoleFullDto update(RoleFullDto roleFullDto) {
        try{
            Role roles = IRoleDtoMapper.INSTANCE.toRole(roleFullDto);
            RoleFullDto roleOld = getRoleFull(roles.getId());
            for(GrantPermissionDto grantPermission : roleOld.getGrantPermissions()) {
                grantPermissionRepository.deleteById(grantPermission.getId());
            }

            for(GrantPermissionDto grant : roleFullDto.getGrantPermissions()) {
                GrantPermission grantPermission = IGrantPermissionDtoMapper.INSTANCE.toGrantPermission(grant);
                grantPermission.setRole(roles);
                grantPermission.setId(UUIDHelper.generateType4UUID().toString());
                grantPermission = grantPermissionRepository.save(grantPermission);
                grant.setId(grantPermission.getId());
            }

            roleRepository.save(roles);
            return roleFullDto;

        }catch(Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }
    @Override
    @Transactional
    public boolean delete(String id) {
        try{

            RoleFullDto roleFullDto = getRoleFull(id);
            for(GrantPermissionDto grantPermission :roleFullDto.getGrantPermissions()) {
                grantPermissionRepository.deleteById((grantPermission.getId()));
            }
            roleRepository.deleteById(id);
            return true;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
