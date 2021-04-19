package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.MaterialDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/material")
public class MaterialController {
    @Autowired
    private IMaterialService materialService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<MaterialDto> materialDtos = materialService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nguyên liệu"), HttpStatus.OK.value(), materialDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<MaterialDto>> searchDto) {
        BaseSearchDto<List<MaterialDto>> search = materialService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nguyên liệu"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable String id) {
        MaterialDto materialDto = materialService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nguyên liệu"), HttpStatus.OK.value(), materialDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody MaterialDto materialDto) {
        List<String> errMessages = validateInserting(materialDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        MaterialDto materialDto1 = materialService.insert(materialDto);

        ResponseEntity<?> res = materialDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin nguyên liệu thành công"), HttpStatus.OK.value(), materialDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin nguyên liệu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody MaterialDto materialDto) {
        List<String> errMessages = validateUpdating(materialDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        MaterialDto materialDto1 = materialService.update(materialDto);
        ResponseEntity<?> res = materialDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin nguyên liệu thành công"), HttpStatus.OK.value(), materialDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin nguyên liệu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = materialService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Nguyên liệu"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin nguyên liệu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(MaterialDto materialDto){
        List<String> result = new ArrayList<>();
        return result;
    }

    private List<String> validateUpdating(MaterialDto materialDto){
        List<String> result = new ArrayList<>();
        if(materialDto.getId().isEmpty()) {
            result.add("Thông tin id bắt buộc");
        }
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id.isEmpty()) {
            result.add("Không tồn tại tồn kho này");
        }
        return result;
    }
}
