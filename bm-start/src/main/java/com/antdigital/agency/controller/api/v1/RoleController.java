package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IEmployeeService;
import com.antdigital.agency.services.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController extends BaseController {
    @Autowired
    IRoleService roleService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<RoleDto> roles = roleService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Employees"), HttpStatus.OK.value(), roles));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<RoleDto>> searchDto) {
        BaseSearchDto<List<RoleDto>> search = roleService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải quyền thành công!"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<?> getById(@PathVariable String roleId) {
        RoleFullDto roleFullDto = roleService.getRoleFull(roleId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải quyền thành công!"), HttpStatus.OK.value(), roleFullDto));
    }

    @PostMapping("/insert")
    @RolesAllowed("ROLES_MANAGEMENT")
    public ResponseEntity<?> insert(@Valid @RequestBody RoleFullDto roleFullDto) {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(getAgencyId());
        roleFullDto.setAgency(agencyDto);
        List<String> errMessages = validateInsert(roleFullDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        RoleFullDto roles = roleService.insert(roleFullDto);
        ResponseEntity<?> res = roles != null  ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu quyền thành công"), HttpStatus.OK.value(), roles))
        : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu quyền"),  HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }
    @PutMapping("/update")
    @RolesAllowed("ROLES_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody RoleFullDto roleFullDto) {
        if(roleFullDto.getAgency() == null || roleFullDto.getAgency().getId() == null || roleFullDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            roleFullDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateUpdate(roleFullDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        RoleFullDto roles = roleService.update(roleFullDto);
        ResponseEntity<?> res = roles != null  ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật quyền thành công"), HttpStatus.OK.value(), roles))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật quyền"),  HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }
    @DeleteMapping("/delete")
    @RolesAllowed("ROLES_MANAGEMENT")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDelete(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = roleService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa quyền thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa quyền"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }
    private List<String> validateInsert(RoleFullDto roleFullDto) {
        List<String> result = new ArrayList<>();
        RoleFullDto roleName = roleService.getByName(roleFullDto.getName(), roleFullDto.getAgency().getId());
        if (roleName != null) {
            result.add("Tên quyền đã tồn tại");
        }

        return result;
    }
    private List<String> validateUpdate(RoleFullDto roleFullDto) {
        List<String> result = new ArrayList<>();
        RoleFullDto roleName = roleService.getByName(roleFullDto.getName(), roleFullDto.getAgency().getId());
        if (roleName != null && !roleName.getId().equals(roleFullDto.getId())) {
            result.add("Tên quyền đã tồn tại");
        }
        return result;
    }
    private List<String> validateDelete(String id) {
        List<String> result = new ArrayList<>();
        if (id.isEmpty()){
            result.add("Quyền không tồn tại");
        }
        return result;
    }
  }
