package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.constant.Constant;
import com.antdigital.agency.common.utils.BCryptHelper;
import com.antdigital.agency.core.service.IEzSoftwareRequestService;
import com.antdigital.agency.dal.repository.IEmployeesRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ChangePasswordDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController extends BaseController {
    @Autowired
    IEmployeeService employeeService;
    @Autowired
    IEzSoftwareRequestService ezSoftwareRequestService;
    @Autowired
    IEmployeesRepository employeesRepository;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<EmployeesDto> employees = employeeService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Employees"), HttpStatus.OK.value(), employees));
    }

    @PostMapping("/insert")
    @RolesAllowed("EMPLOYEE_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody EmployeeFullDto employeeFullDto) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(getAgencyId());
        agencyDto.setCompanyId(this.getCompanyId());
        employeeFullDto.setAgency(agencyDto);
        // Validation before saving
        List<String> errMessages = validateInserting(request, employeeFullDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        EmployeeFullDto employees = employeeService.insert(employeeFullDto);
        ResponseEntity<?> res = employees != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu nhân viên thành công"), HttpStatus.OK.value(), employees))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }
    @PutMapping("/update")
    @RolesAllowed("EMPLOYEE_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeeFullDto employeeFullDto) {
        if(employeeFullDto.getAgency() == null || employeeFullDto.getAgency().getId() == null || employeeFullDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            employeeFullDto.setAgency(agencyDto);
        }
        // Validation before saving
        List<String> errMessages = validateUpdating(employeeFullDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        EmployeeFullDto employees = employeeService.update(employeeFullDto);
        ResponseEntity<?> res = employees != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật nhân viên thành công"), HttpStatus.OK.value(), employees))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }
    @DeleteMapping("/delete")
    @RolesAllowed("EMPLOYEE_MANAGEMENT")
    public ResponseEntity<?> delete(@RequestParam String id) {
        if(id.isEmpty()) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Không tồn tại nhân viên này"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = employeeService.deleteEmployee(id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa nhân viên thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployee(@PathVariable String employeeId) {
        EmployeesDto employees = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Employees"), HttpStatus.OK.value(), employees));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String employeeId) {
        EmployeesDto employees = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Employees"), HttpStatus.OK.value(), employees));
    }

    @GetMapping("/get-full/{employeeId}")
    public ResponseEntity<?> getEmployeeFull(@PathVariable String employeeId) {
        EmployeeFullDto employees = employeeService.getEmployeeFullById(employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Employees"), HttpStatus.OK.value(), employees));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<EmployeesDto>> searchDto) {
        BaseSearchDto<List<EmployeesDto>> search = employeeService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách nhân viên"), HttpStatus.OK.value(), search));
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@Valid @RequestBody EmployeeSearchDto employeeSearchDto) {
        EmployeeSearchDto search = employeeService.findAllAffiliate(employeeSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách nhân viên"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/getLikeName")
    public ResponseEntity<?> getLikeName(@RequestParam String employeeName) {
        List<EmployeesDto> employeeList = employeeService.getLikeName(employeeName, getAgencyId());

        ResponseEntity<?> res = employeeList != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Employee"), HttpStatus.OK.value(), employeeList))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhân viên không tồn tại"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @GetMapping("/getEmployeeByEmail")
    public ResponseEntity<?> getEmployeeByEmail(@RequestParam String email) {
        EmployeesDto employeesEmail = employeeService.getEmployeeByEmail(email, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("EmployeesEmail"), HttpStatus.OK.value(), employeesEmail));
    }

    @PostMapping("/list")
    public ResponseEntity<?> getEmployees(@RequestBody List<String> ids) {
        List<EmployeesDto> employees = employeeService.getEmployees(ids);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách nhân viên"), HttpStatus.OK.value(), employees));
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        List<String> msg = changePassValidation(changePasswordDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        EmployeesDto curEmployee = getCurrentEmployee();
        EmployeesDto employee = employeeService.changePassword(curEmployee.getEmail(), changePasswordDto, curEmployee.getAgency().getId());

        ResponseEntity<?> res = employee != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Employee"), HttpStatus.OK.value(), employee))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Đổi mật khẩu không thành công"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/getBonusByRangeDate/{employeeId}")
    public ResponseEntity<?> getBonusByRangeDate(@RequestBody RangeDateDto rangeDateDto, @PathVariable String employeeId) {
        Double bonus = employeeService.getBonusByRangeDate(rangeDateDto, employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hoa hồng theo khoảng ngày "), HttpStatus.OK.value(), bonus));
    }

    private List<String> validateUpdating(EmployeeFullDto employeeFullDto){
        List<String> result = new ArrayList<>();
        // Skip if id is same
        EmployeesDto employeesEmail  = employeeService.getEmployeeByEmail(employeeFullDto.getEmail(), employeeFullDto.getAgency().getId());
        employeeFullDto.setPassword(BCryptHelper.encode(employeeFullDto.getPassword()));
        if(employeesEmail != null && !employeesEmail.getId().equals(employeeFullDto.getId())) {
            result.add("Email nhân viên đã tồn tại");
        }
        return result;
    }

    private List<String> validateInserting(HttpServletRequest request, EmployeeFullDto employeeFullDto) throws IOException, JAXBException {
        List<String> result = new ArrayList<>();
        employeeFullDto.setPassword(BCryptHelper.encode(employeeFullDto.getPassword()));
        EmployeesDto employeesEmail  = employeeService.getEmployeeByEmail(employeeFullDto.getEmail(), employeeFullDto.getAgency().getId());
        if(employeesEmail != null) {
            result.add("Email nhân viên đã tồn tại");
        }

        // Check user limit
        int numberOfCurEmployees = employeesRepository.countEmployeeByCompanyId(employeeFullDto.getAgency().getCompanyId());
        String companyId = employeeFullDto.getAgency().getCompanyId();
        if (ezSoftwareRequestService.reachUserLimit(request, companyId, Constant.softId, numberOfCurEmployees)) {
            result.add("Đã đạt mức giới hạn người dùng");
        }
        return result;
    }

    private List<String> changePassValidation(ChangePasswordDto changePasswordDto) {
        List<String> result = new ArrayList<>();

        EmployeesDto curEmployee = getCurrentEmployee();
        // crypt oldPassword and compare password
        if(!BCryptHelper.check(changePasswordDto.getOldPassword(), curEmployee.getPassword())) {
            result.add("Mật khẩu hiện tại không chính xác");
        }

        // valid input new Password
        if(!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmation())) {
            result.add("Mật khẩu mới không khớp nhau");
        }

        // duplicate password
        if(BCryptHelper.check(changePasswordDto.getNewPassword(), curEmployee.getPassword())) {
            result.add("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        return result;
    }
}
