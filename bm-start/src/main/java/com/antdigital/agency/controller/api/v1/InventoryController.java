package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.AgencyDto;
import com.antdigital.agency.dtos.response.InventoryDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController extends BaseController {

    @Autowired
    private IInventoryService inventoryService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<InventoryDto> inventoryDtos = inventoryService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tồn kho"), HttpStatus.OK.value(), inventoryDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<InventoryDto>> searchDto) {
        BaseSearchDto<List<InventoryDto>> search = inventoryService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tồn kho"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findOne(@PathVariable String id) {
        InventoryDto inventoryDto = inventoryService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tồn kho"), HttpStatus.OK.value(), inventoryDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody InventoryDto inventoryDto) {
        if(inventoryDto.getAgency() == null || inventoryDto.getAgency().getId() == null || inventoryDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            inventoryDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateInserting(inventoryDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        InventoryDto inventoryDto1 = inventoryService.insert(inventoryDto);

        ResponseEntity<?> res = inventoryDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin tồn kho thành công"), HttpStatus.OK.value(), inventoryDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin tồn kho"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody InventoryDto inventoryDto) {
        if(inventoryDto.getAgency() == null || inventoryDto.getAgency().getId() == null || inventoryDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            inventoryDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateUpdating(inventoryDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        InventoryDto inventoryDto1 = inventoryService.update(inventoryDto);
        ResponseEntity<?> res = inventoryDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin tồn kho thành công"), HttpStatus.OK.value(), inventoryDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin tồn kho"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = inventoryService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Tồn kho"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin tồn kho"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(InventoryDto inventoryDto){
        List<String> result = new ArrayList<>();
        return result;
    }

    private List<String> validateUpdating(InventoryDto inventoryDto){
        List<String> result = new ArrayList<>();
        if(inventoryDto.getId().isEmpty()) {
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
