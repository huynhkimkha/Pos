package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agency")
public class AgencyController {
    @Autowired
    private IAgencyService agencyService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<AgencyDto> agencys = agencyService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), agencys));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<AgencyDto>> searchDto) {
        BaseSearchDto<List<AgencyDto>> search = agencyService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String id) {
        AgencyDto agency = agencyService.getAgencyById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), agency));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody AgencyDto agencyDto) {
        List<String> errMessages = validateInserting(agencyDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        AgencyDto agencyDtoDetail = agencyService.insert(agencyDto);

        ResponseEntity<?> res = agencyDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin chi nhánh thành công"), HttpStatus.OK.value(), agencyDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AgencyDto agencyDto) {
        List<String> errMessages = validateUpdating(agencyDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        AgencyDto agencyDtoDetail = agencyService.update(agencyDto);
        ResponseEntity<?> res = agencyDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin chi nhánh thành công"), HttpStatus.OK.value(), agencyDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = agencyService.deleteAgency(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateUpdating(AgencyDto agencyDto){
        List<String> result = new ArrayList<>();
        if (agencyDto == null || agencyDto.getId() == null || agencyDto.getId().isEmpty()) {
            result.add("Thông tin không hợp lệ");
        }

        return result;
    }

    private List<String> validateInserting(AgencyDto agencyDto) {
        List<String> result = new ArrayList<>();
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại khách hàng này");
        }
        // TODO check all agency services. allow deleting if it wasn't used in any agency service.

        return result;
    }
}
