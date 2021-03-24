package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReferralBonusSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IReferralBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/referral-bonus")
public class ReferralBonusController extends BaseController {
    @Autowired
    private IReferralBonusService referralBonusService;

    @GetMapping("/findAllByCollaboratorRefId")
    public ResponseEntity<?> findAllByCollaboratorRefId(@RequestParam String collaboratorRefId) {
        List<ReferralBonusDto> referralBonusDtos = referralBonusService.findAllByCollaboratorRefId(collaboratorRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @GetMapping("/getNotCompleteByCollaboratorRefId")
    public ResponseEntity<?> getNotCompleteByCollaboratorRefId(@RequestParam String collaboratorRefId) {
        List<ReferralBonusDto> referralBonusDtos = referralBonusService.getNotCompleteByCollaboratorRefId(collaboratorRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @PostMapping("/findByCollaboratorRefId")
    public ResponseEntity<?> findByCollaboratorRefId(@RequestBody ReferralBonusSearchDto searchDto) {
        String agencyId = getAgencyId();
        BaseSearchDto<List<ReferralBonusDto>> search = referralBonusService.findAllByCollaboratorRefId(searchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAllByEmployeeRefId")
    public ResponseEntity<?> findAllByEmployeeRefId(@RequestParam String employeeRefId) {
        List<ReferralBonusDto> referralBonusDtos = referralBonusService.findAllByEmployeeRefId(employeeRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }
    @GetMapping("/getBonusNotCompleteByEmployeeRefId")
    public ResponseEntity<?> getBonusNotCompleteByEmployeeRefId(@RequestParam String employeeRefId) {
        List<ReferralBonusDto> referralBonusDtos = referralBonusService.getBonusNotCompleteByEmployeeRefId(employeeRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @PostMapping("/findByEmployeeRefId")
    public ResponseEntity<?> findByEmployeeRefId(@RequestBody ReferralBonusSearchDto searchDto) {
        BaseSearchDto<List<ReferralBonusDto>> search = referralBonusService.findAllByEmployeeRefId(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), search));
    }
}
