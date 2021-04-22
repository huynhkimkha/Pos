package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IImportingMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/importing-material")
public class ImportingMaterialController extends BaseController{
    @Autowired
    private IImportingMaterialService importingMaterialService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ImportingMaterialDto> importingMaterialDtos = importingMaterialService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingMaterialDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<ImportingMaterialDto>> searchDto) {
        BaseSearchDto<List<ImportingMaterialDto>> search = importingMaterialService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/get-full/{id}")
    public ResponseEntity<?> getProductFull(@PathVariable String id) {
        ImportingMaterialFullDto importingMaterialDto = importingMaterialService.getFullById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingMaterialDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody ImportingMaterialFullDto importingMaterialDto) {
        if(importingMaterialDto.getAgency() == null || importingMaterialDto.getAgency().getId() == null || importingMaterialDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            importingMaterialDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateInserting(importingMaterialDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ImportingMaterialFullDto importingMaterialDto1 = importingMaterialService.insert(importingMaterialDto);

        ResponseEntity<?> res = importingMaterialDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin phiếu nhập thành công"), HttpStatus.OK.value(), importingMaterialDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ImportingMaterialFullDto importingMaterialDto) {
        if(importingMaterialDto.getAgency() == null || importingMaterialDto.getAgency().getId() == null || importingMaterialDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            importingMaterialDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateUpdating(importingMaterialDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        ImportingMaterialFullDto importingMaterialDto1 = importingMaterialService.update(importingMaterialDto);
        ResponseEntity<?> res = importingMaterialDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin phiếu nhập thành công"), HttpStatus.OK.value(), importingMaterialDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = importingMaterialService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(ImportingMaterialFullDto importingMaterialDto){
        List<String> result = new ArrayList<>();
        return result;
    }

    private List<String> validateUpdating(ImportingMaterialFullDto importingMaterialDto){
        List<String> result = new ArrayList<>();
        if(importingMaterialDto.getId().isEmpty()) {
            result.add("Thông tin id bắt buộc");
        }
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id.isEmpty()) {
            result.add("Không tồn tại phiếu nhập này");
        }
        return result;
    }
}
