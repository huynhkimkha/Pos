package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


@RestController
@RequestMapping("/api/v1/validate")
public class ValidationController extends BaseController {
    @Autowired
    private IValidationService validationService;

    @GetMapping("/check-exist-customer-transaction")
    public ResponseEntity<?> checkExistCustomerTransaction(@RequestParam String customerId) {
        boolean result = validationService.checkExistCustomerTransaction(customerId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kiểm tra tồn tại giao dịch của khách hàng"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/check-exist-collaborator-employee")
    public ResponseEntity<?> checkExistCollaboratorOrEmployee(@RequestParam String collaboratorId, @RequestParam String employeeId) {
        boolean result = validationService.checkExistCollaboratorOrEmployee(collaboratorId, employeeId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kiểm tra tồn tại cộng tác viên và nhân viên"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/check-exist-collaborator-phone")
    public ResponseEntity<?> checkExistCollaboratorPhone(@RequestParam String phone) {
        boolean result = validationService.checkExistCollaboratorPhone(phone);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kiểm tra tồn tại số điện thoại cộng tác viên"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/check-exist-collaborator-email")
    public ResponseEntity<?> checkExistCollaboratorEmail(@RequestParam String email) {
        boolean result = validationService.checkExistCollaboratorEmail(email);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kiểm tra tồn tại Email cộng tác viên"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/check-exist-merchandise")
    public ResponseEntity<?> checkExistMerchandise(@RequestParam String merchandiseId) {
        boolean result = validationService.checkExistMerchandise(merchandiseId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kiểm tra ràng buộc xoá hàng hoá"), HttpStatus.OK.value(), result));
    }
}
