package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CostCategoryDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.ICostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cost-category")
public class CostCategoryController {
    @Autowired
    private ICostCategoryService costCategoryService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<CostCategoryDto> costCategoryList = costCategoryService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục chi phí"), HttpStatus.OK.value(), costCategoryList));
    }


    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<CostCategoryDto>> searchDto) {
        BaseSearchDto<List<CostCategoryDto>> search = costCategoryService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục chi phí"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String id) {
        CostCategoryDto costCategory = costCategoryService.getCostCategoryById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục chi phí"), HttpStatus.OK.value(), costCategory));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody CostCategoryDto costCategoryDto) {
        List<String> errMessages = validateInserting(costCategoryDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        CostCategoryDto costCategoryDtoDetail = costCategoryService.insert(costCategoryDto);

        ResponseEntity<?> res = costCategoryDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin Danh mục chi phí thành công"), HttpStatus.OK.value(), costCategoryDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin Danh mục chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody CostCategoryDto costCategoryDto) {
        List<String> errMessages = validateUpdating(costCategoryDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        CostCategoryDto costCategoryDtoDetail = costCategoryService.update(costCategoryDto);
        ResponseEntity<?> res = costCategoryDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin Danh mục chi phí thành công"), HttpStatus.OK.value(), costCategoryDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin Danh mục chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = costCategoryService.deleteCostCategory(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục chi phí"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin Danh mục chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateUpdating(CostCategoryDto costCategoryDto){
        List<String> result = new ArrayList<>();
        if (costCategoryDto == null || costCategoryDto.getId() == null || costCategoryDto.getId().isEmpty()) {
            result.add("Thông tin không hợp lệ");
        }

        return result;
    }

    private List<String> validateInserting(CostCategoryDto costCategoryDto) {
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
