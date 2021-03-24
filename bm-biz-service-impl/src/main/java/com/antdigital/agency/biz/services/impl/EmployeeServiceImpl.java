package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.constant.Constant;
import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.common.enums.SettingKeysEnum;
import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private IEmployeesRepository employeesRepository;
    @Autowired
    private IRoleDetailRepository roleDetailRepository;
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IReferralBonusRepository referralBonusRepository;
    @Autowired
    private ISellingBonusRepository sellingBonusRepository;
    @Autowired
    private IGrantPermissionRepository grantPermissionRepository;
    @Autowired
    private ISettingsRepository settingsRepository;

    @Override
    @Transactional
    public List<EmployeesDto> findAll(String agencyId) {
        List<Employees> employees = employeesRepository.findAllByAgencyId(agencyId);
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(employees);
    }

    @Override
    public EmployeesDto getEmployeeByEmail(String email, String agencyId) {
        Employees employee = employeesRepository.getEmployeeByEmail(email, agencyId);
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDto(employee);
    }

    @Override
    public List<EmployeesDto> getEmployees(List<String> ids) {
        List<Employees> employees = employeesRepository.getEmployeesById(ids);
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(employees);
    }

    @Override
    public List<EmployeesDto> getLikeName(String employeeName, String agencyId) {
        List<Employees> employee = employeesRepository.getLikeName(employeeName, agencyId);
        return IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(employee);
    }

    @Override
    @Transactional
    public EmployeeFullDto insert(EmployeeFullDto employeeFullDto) {
        try {
            Employees employees = IEmployeesDtoMapper.INSTANCE.toEmployees(employeeFullDto);

            employees.setId(UUIDHelper.generateType4UUID().toString());
            Employees createdEmployee = employeesRepository.save(employees);

            for(RoleDetailFullDto detail : employeeFullDto.getRoleDetails()) {
                if (detail.getRole() == null || detail.getRole().getId() == null
                        || detail.getRole().getId().isEmpty()) {
                    continue;
                }

                RoleDetail tempDetail = IRoleDetailDtoMapper.INSTANCE.toRoleDetailFull(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setEmployee(createdEmployee);

                roleDetailRepository.save(tempDetail);
            }
            if (employeeFullDto.getReferee() != null) {
                ReferralBonus referee = IReferralBonusDtoMapper.INSTANCE.toReferralBonus(employeeFullDto.getReferee());
                Employees employees1 = new Employees();
                employees1.setId(createdEmployee.getId());
                referee.setEmployee(employees1);
                referee.setAgency(createdEmployee.getAgency());
                referee.setId(UUIDHelper.generateType4UUID().toString());
                referee.setActivatedStatus(ActivatedStatusEnum.NONACTIVATED);
                try {
                    if (referee.getCollaboratorRef() != null) {
                        referee.setAmount(Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.COLLABORATOR_REF_BONUS.toString()).getValues()));
                    } else {
                        referee.setAmount(Double.parseDouble(settingsRepository.getByKey(SettingKeysEnum.EMPLOYEE_REF_BONUS.toString()).getValues()));
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    logger.error(ex.getStackTrace().toString());
                    employeesRepository.deleteById(createdEmployee.getId());
                    return null;
                }
                referralBonusRepository.save(referee);
            }
            employeeFullDto.setId(createdEmployee.getId());
            return employeeFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public EmployeeFullDto update(EmployeeFullDto employeeFullDto) {
        try {
            Employees old = employeesRepository.findById(employeeFullDto.getId()).get();
            Employees employees = IEmployeesDtoMapper.INSTANCE.toEmployees(employeeFullDto);
            employees.setPassword(old.getPassword());
            employeesRepository.save(employees);
            // collect role detail was removed.
            List<RoleDetail> roleDetails = roleDetailRepository.getDetailsByEmployeeId(employeeFullDto.getId());
            List<String> detailDelete = new ArrayList<>();
            for(RoleDetail item : roleDetails) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = employeeFullDto.getRoleDetails().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = detailDelete.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    detailDelete.add(item.getId());
                }
            }

            for(String id : detailDelete) {
                roleDetailRepository.deleteById(id);
            }

            for(RoleDetailFullDto roleDetailDto : employeeFullDto.getRoleDetails()) {
                if (roleDetailDto.getId() == null || roleDetailDto.getId().isEmpty()) {
                    EmployeesDto employeesDto = new EmployeesDto();
                    employeesDto.setId(employeeFullDto.getId());

                    roleDetailDto.setId(UUIDHelper.generateType4UUID().toString());
                    roleDetailDto.setEmployee(employeesDto);
                }
                RoleDetail roleDetail = IRoleDetailDtoMapper.INSTANCE.toRoleDetailFull(roleDetailDto);
                roleDetail = roleDetailRepository.save(roleDetail);
                roleDetailDto = IRoleDetailDtoMapper.INSTANCE.toRoleDetailFullDto(roleDetail);
            }

            return employeeFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean deleteEmployee(String id) {
        try {
            EmployeeFullDto employeeFull = this.getEmployeeFullById(id);
            for(RoleDetailFullDto detailDto : employeeFull.getRoleDetails()) {
                roleDetailRepository.deleteById(detailDto.getId());
            }
            employeesRepository.deleteById(id);
            return true;
        }  catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public BaseSearchDto<List<EmployeesDto>> findAll(BaseSearchDto<List<EmployeesDto>> searchDto, String agencyId) {
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll(agencyId));
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Employees> page = employeesRepository.findAllByAgencyId(request, agencyId);
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public EmployeeSearchDto findAllAffiliate(EmployeeSearchDto searchDto, String agencyId) {
        List<EmployeeDetailDto> employeeDetailDtos = new ArrayList<>();
        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Employees> page = employeesRepository.findAllByAgencyId(request, agencyId);
        List<Employees> employees = page.getContent();

        List<String> employeeIds = new ArrayList<>();
        for (Employees c : employees) {
            employeeIds.add(c.getId());
            c.setPassword(null);
        }
        List<EmployeesDto> employeeDtos = IEmployeesDtoMapper.INSTANCE.toEmployeesDtoList(employees);
        employeeDetailDtos = IEmployeesDtoMapper.INSTANCE.toEmployeeDetailDtos(employeeDtos);

        List<ReferralBonus> referralBonuses = referralBonusRepository.getByEmployeeAndEmployeeRef(employeeIds);
        List<ReferralBonusDto> referralBonusDtos = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralBonuses);

        List<SellingBonus> sellingBonuses = sellingBonusRepository.getByEmployeesUnpaid(employeeIds);
        List<SellingBonusDto> sellingBonusDtos = ISellingBonusDtoMapper.INSTANCE.toSellingBonusDtos(sellingBonuses);
        for (EmployeeDetailDto employeeDetailDto : employeeDetailDtos) {
            Double bonus = 0D;
            ReferralBonusDto refereeDto = referralBonusDtos.stream().filter(c -> c.getEmployee() != null && c.getEmployee().getId() != null && c.getEmployee().getId().equals(employeeDetailDto.getId())).findFirst().orElse(null);
            employeeDetailDto.setReferee(refereeDto);
            for (SellingBonusDto bonusDto : sellingBonusDtos) {
                if (bonusDto.getEmployee() != null && bonusDto.getEmployee().getId().equals(employeeDetailDto.getId())
                        || bonusDto.getEmployeeRef() != null && bonusDto.getEmployeeRef().getId().equals(employeeDetailDto.getId())){
                    bonus += bonusDto.getAmount();
                }
            }
            for (ReferralBonusDto refBonusDto : referralBonusDtos) {
                if (refBonusDto.getEmployeeRef() != null && refBonusDto.getEmployeeRef().getId().equals(employeeDetailDto.getId()) && refBonusDto.getPaymentStatus() != PaymentStatusEnum.COMPLETED) {
                    bonus += refBonusDto.getAmount();
                }
            }
            employeeDetailDto.setUnpaidBonus(bonus);
        }

        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(employeeDetailDtos);

        return searchDto;
    }

    @Override
    public EmployeesDto getEmployeeById(String id) {
        try {
            Employees employee = employeesRepository.findById(id).get();
            EmployeesDto employeeDto = IEmployeesDtoMapper.INSTANCE.toEmployeesDto(employee);
            return employeeDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public EmployeeFullDto getEmployeeFullById(String id) {
        try {
            Employees employee = employeesRepository.findById(id).get();
            List<RoleDetail> details = roleDetailRepository.getDetailsByEmployeeId(employee.getId());
            EmployeeFullDto employeeDto = IEmployeesDtoMapper.INSTANCE.toEmployeeFullDto(employee);
            List<RoleDetailFullDto> detailDto = IRoleDetailDtoMapper.INSTANCE.toRoleDetailFullListDto(details);
            ReferralBonus referee = referralBonusRepository.getByEmployee(id);
            ReferralBonusDto refereeDto = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDto(referee);
            List<ReferralBonus> referralSales = referralBonusRepository.getByEmployeeRef(id);
            List<ReferralBonusDto> referralSaleDtos = IReferralBonusDtoMapper.INSTANCE.toReferralBonusDtos(referralSales);
            employeeDto.setRoleDetails(detailDto);
            employeeDto.setReferee(refereeDto);
            employeeDto.setReferralSales(referralSaleDtos);
            return employeeDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }
    @Override
    @Transactional
    public EmployeeFullDto getEmployeeFull(String email, String companyId) {
        Employees employee = employeesRepository.getEmployee(email, companyId);
        EmployeeFullDto employeeFullDto =IEmployeesDtoMapper.INSTANCE.toEmployeeFullDto(employee);
        if (employeeFullDto == null) {
            return employeeFullDto;
        }
        employeeFullDto = getRoleDetails(employeeFullDto);

        return employeeFullDto;
    }

    private EmployeeFullDto getRoleDetails(EmployeeFullDto employeeFullDto) {
        employeeFullDto.setRoleDetails(new ArrayList<>());
        List<RoleDetail> roleDetails = roleDetailRepository.getDetailsByEmployeeId(employeeFullDto.getId());
        if(roleDetails != null && roleDetails.size() > 0) {
            for (RoleDetail item : roleDetails) {
                RoleDetailFullDto roleDetailDto = IRoleDetailDtoMapper.INSTANCE.toRoleDetailFullDto(item);
                List<GrantPermission> grantPermissions = grantPermissionRepository.getRoleId(item.getRole().getId());
                if (grantPermissions == null || grantPermissions.size() == 0) {
                    continue;
                }

                roleDetailDto.setRole(IRoleDtoMapper.INSTANCE.toRoleFullDto(grantPermissions.get(0).getRole()));
                roleDetailDto.getRole().setGrantPermissions((new ArrayList<>()));
                for (GrantPermission grantPermission : grantPermissions) {
                    roleDetailDto.getRole().getGrantPermissions().add(IGrantPermissionDtoMapper.INSTANCE.toGrantPermissionDto(grantPermission));
                }
                employeeFullDto.getRoleDetails().add(roleDetailDto);
            }
        }
        return employeeFullDto;
    }

    @Override
    @Transactional
    public EmployeesDto changePassword(String userName, ChangePasswordDto changePasswordDto, String agencyId) {
        try {
            // get Employee by userName
            Employees employees = employeesRepository.getEmployeeByEmail(userName, agencyId);
            employees.setPassword(BCryptHelper.encode(changePasswordDto.getNewPassword()));

            Employees updatedEmployee = employeesRepository.save(employees);
            return IEmployeesDtoMapper.INSTANCE.toEmployeesDto(updatedEmployee);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public Double getBonusByRangeDate(RangeDateDto rangeDateDto, String employeeId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDate = format1.format(rangeDateDto.getFromDate());
        String toDate = format1.format(rangeDateDto.getToDate());
        Double totalBonus = 0D;
        Double referralBonus = referralBonusRepository.getTotalByRangeDateAndEmployeeRefId(fromDate, toDate, employeeId);
        Double sellingBonus = sellingBonusRepository.getTotalByRangeDateAndEmployeeRefId(fromDate, toDate, employeeId);
        referralBonus = referralBonus != null ? referralBonus : 0;
        sellingBonus = sellingBonus != null ? sellingBonus : 0;
        totalBonus = totalBonus + referralBonus + sellingBonus;
        return totalBonus;
    }
}
