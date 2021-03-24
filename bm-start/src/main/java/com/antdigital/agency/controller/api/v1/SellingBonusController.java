package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.SellingBonusSearchDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.dtos.response.SellingBonusDto;
import com.antdigital.agency.services.ISellingBonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/selling-bonus")
public class SellingBonusController extends BaseController {
    @Autowired
    private ISellingBonusService sellingBonusService;

    @GetMapping("/findAllByCollaboratorRefId")
    public ResponseEntity<?> findAllByCollaboratorRefId(@RequestParam String collaboratorRefId) {
        List<SellingBonusDto> referralBonusDtos = sellingBonusService.findAllByCollaboratorRefId(collaboratorRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @GetMapping("/getBonusNotCompleteByCollaboratorRef")
    public ResponseEntity<?> getBonusNotCompleteByCollaboratorRef(@RequestParam String collaboratorRefId) {
        List<SellingBonusDto> referralBonusDtos = sellingBonusService.getBonusNotCompleteByCollaboratorRef(collaboratorRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @PostMapping("/findByCollaboratorRefId")
    public ResponseEntity<?> findByCollaboratorRefId(@RequestBody SellingBonusSearchDto searchDto) {
        String agencyId = getAgencyId();
        BaseSearchDto<List<SellingBonusDto>> search = sellingBonusService.findAllByCollaboratorRefId(searchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAllByEmployeeRefId")
    public ResponseEntity<?> findAllByEmployeeRefId(@RequestParam String employeeRefId) {
        List<SellingBonusDto> referralBonusDtos = sellingBonusService.findAllByEmployeeRefId(employeeRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @GetMapping("/getBonusNotCompleteByEmployeeRef")
    public ResponseEntity<?> getBonusNotCompleteByEmployeeRef(@RequestParam String employeeRefId) {
        List<SellingBonusDto> referralBonusDtos = sellingBonusService.getBonusNotCompleteByEmployeeRef(employeeRefId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), referralBonusDtos));
    }

    @PostMapping("/findByEmployeeRefId")
    public ResponseEntity<?> findByEmployeeRefId(@RequestBody SellingBonusSearchDto searchDto) {
        BaseSearchDto<List<SellingBonusDto>> search = sellingBonusService.findAllByEmployeeRefId(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hoa hồng trên hoá đơn"), HttpStatus.OK.value(), search));
    }
}
