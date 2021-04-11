package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CategoriesDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.ICategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriesController extends BaseController {
    @Autowired
    private ICategoriesService categoriesService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<CategoriesDto> categoriesDtos = categoriesService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục"), HttpStatus.OK.value(), categoriesDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<CategoriesDto>> searchDto) {
        BaseSearchDto<List<CategoriesDto>> search = categoriesService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        CategoriesDto categoriesDto = categoriesService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục"), HttpStatus.OK.value(), categoriesDto));
    }

    @GetMapping("/like-name")
    public ResponseEntity<?> getLikeName(@RequestParam String name) {
        List<CategoriesDto> categoriesDtoList = categoriesService.getLikeName(name);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục"), HttpStatus.OK.value(), categoriesDtoList));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody CategoriesDto categoriesDto) {
        List<String> errMessages = validateInserting(categoriesDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        CategoriesDto categoriesDto1 = categoriesService.insert(categoriesDto);

        ResponseEntity<?> res = categoriesDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin danh mục thành công"), HttpStatus.OK.value(), categoriesDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin danh mục"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody CategoriesDto categoriesDto) {
        List<String> errMessages = validateUpdating(categoriesDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        CategoriesDto categoriesDto1 = categoriesService.update(categoriesDto);
        ResponseEntity<?> res = categoriesDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin danh mục thành công"), HttpStatus.OK.value(), categoriesDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin danh mục"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = categoriesService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin danh mục"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(CategoriesDto categoriesDto){
        List<String> result = new ArrayList<>();
        CategoriesDto categoriesDto1 = categoriesService.getByName(categoriesDto.getName());
        if (categoriesDto1 != null) {
            result.add("Tên danh mục đã tồn tại");
        }
        return result;
    }

    private List<String> validateUpdating(CategoriesDto categoriesDto){
        List<String> result = new ArrayList<>();
        if(categoriesDto.getId().isEmpty()) {
            result.add("Thông tin id bắt buộc");
        }
        if(categoriesService.getById(categoriesDto.getId()) == null){
            result.add("Danh mục không tồn tại");
        }
        CategoriesDto categoriesDto1 = categoriesService.getByName(categoriesDto.getName());
        if (categoriesDto1 != null && !categoriesDto1.getId().equals(categoriesDto.getId())) {
            result.add("Tên danh mục đã tồn tại");
        }
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id.isEmpty()) {
            result.add("Không tồn tại danh mục này");
        }
        if(categoriesService.getById(id) == null){
            result.add("Danh mục không tồn tại");
        }
        if(categoriesService.isCategoryUsed(id)) {
            result.add("Không thể xoá danh mục này");
        }

        return result;
    }
}
