package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dal.repository.IEmployeesRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController extends BaseController {
    @Autowired
    IEmployeeService employeeService;
    @Autowired
    IEmployeesRepository employeesRepository;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<EmployeesDto> employees = employeeService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhân viên"), HttpStatus.OK.value(), employees));
    }


    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String employeeId) {
        EmployeesDto employees = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhân viên"), HttpStatus.OK.value(), employees));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<EmployeesDto>> searchDto) {
        BaseSearchDto<List<EmployeesDto>> search = employeeService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhân viên"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/getEmployeeByEmail")
    public ResponseEntity<?> getEmployeeByEmail(@RequestParam String email) {
        EmployeesDto employeesEmail = employeeService.getEmployeeByEmail(email);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhân viên"), HttpStatus.OK.value(), employeesEmail));
    }


    @PostMapping("/insert")
    public ResponseEntity<?> insert( @Valid @RequestBody EmployeesDto employeesDto) {
        // Validation before saving
        List<String> errMessages = validateInserting(employeesDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        EmployeesDto result = employeeService.insert(employeesDto);
        ResponseEntity<?> res = result != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu nhân viên thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody EmployeesDto employeesDto) {
        // Validation before saving
        List<String> errMessages = validateUpdating(employeesDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        EmployeesDto result = employeeService.update(employeesDto);
        ResponseEntity<?> res = result != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật nhân viên thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        if(id.isEmpty()) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Không tồn tại nhân viên này"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = employeeService.deleteEmployee(id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa nhân viên thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa nhân viên"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateUpdating(EmployeesDto employeesDto){
        List<String> result = new ArrayList<>();
        // Skip if id is same
        EmployeesDto employeesEmail  = employeeService.getEmployeeByEmail(employeesDto.getEmail());
        if(employeesEmail != null && !employeesEmail.getId().equals(employeesDto.getId())) {
            result.add("Email nhân viên đã tồn tại");
        }
        return result;
    }

    private List<String> validateInserting(EmployeesDto employeesDto){
        List<String> result = new ArrayList<>();
        EmployeesDto employeesEmail  = employeeService.getEmployeeByEmail(employeesDto.getEmail());
        if(employeesEmail != null) {
            result.add("Email nhân viên đã tồn tại");
        }
        return result;
    }
}
