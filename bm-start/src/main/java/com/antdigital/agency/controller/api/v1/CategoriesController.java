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
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhóm hàng hóa"), HttpStatus.OK.value(), categoriesDto));
    }

    @GetMapping("/like-name")
    public ResponseEntity<?> getLikeName(@RequestParam String name) {
        List<CategoriesDto> categoriesDtoList = categoriesService.getLikeName(name);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhóm hàng hóa"), HttpStatus.OK.value(), categoriesDtoList));
    }

    @PostMapping("/insert")
    @RolesAllowed("MERCHANDISE_GROUP_MANAGEMENT")
    public ResponseEntity<?> insert(@Valid @RequestBody CategoriesDto categoriesDto) {
        List<String> errMessages = validateInserting(categoriesDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        CategoriesDto categoriesDto1 = categoriesService.insert(categoriesDto);

        ResponseEntity<?> res = categoriesDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin nhóm hàng hóa thành công"), HttpStatus.OK.value(), categoriesDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin hàng hóa"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("MERCHANDISE_GROUP_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody MerchandiseGroupDto merchandiseGroupDto) {
        merchandiseGroupDto.setCompanyId(this.getCompanyId());

        List<String> errMessages = validateUpdating(merchandiseGroupDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        MerchandiseGroupDto merchandiseGroup = merchandiseGroupService.update(merchandiseGroupDto);
        ResponseEntity<?> res = merchandiseGroup != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin nhóm hàng hóa thành công"), HttpStatus.OK.value(), merchandiseGroup))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin nhóm hàng hóa"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    @RolesAllowed("MERCHANDISE_GROUP_MANAGEMENT")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = merchandiseGroupService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Nhóm hàng hóa"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin nhóm hàng hóa"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(CategoriesDto categoriesDto){
        List<String> result = new ArrayList<>();
        CategoriesDto categoriesDto1 = categoriesService.getByName(categoriesDto.getName());
        if (categoriesDto1 != null) {
            result.add("Tên nhóm hàng hóa đã tồn tại");
        }
        return result;
    }

    private List<String> validateUpdating(MerchandiseGroupDto merchandiseGroupDto){
        List<String> result = new ArrayList<>();
        if(merchandiseGroupDto.getId().isEmpty()) {
            result.add("Thông tin id bắt buộc");
        }
        if(merchandiseGroupService.getById(merchandiseGroupDto.getId(), merchandiseGroupDto.getCompanyId()) == null){
            result.add("Nhóm hàng hoá không tồn tại");
        }
        MerchandiseGroupDto merchandiseGroup = merchandiseGroupService.getByName(merchandiseGroupDto.getName(), merchandiseGroupDto.getCompanyId());
        if (merchandiseGroup != null && !merchandiseGroup.getId().equals(merchandiseGroupDto.getId())) {
            result.add("Tên nhóm hàng hóa đã tồn tại");
        }
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id.isEmpty()) {
            result.add("Không tồn tại nhóm hàng hóa này");
        }
        if(merchandiseGroupService.getById(id, this.getCompanyId()) == null){
            result.add("Nhóm hàng hoá không tồn tại");
        }
        if(merchandiseService.checkByGroup1(id, this.getCompanyId())) {
            result.add("Không thể xoá nhóm hàng hóa này");
        }

        return result;
    }
}
