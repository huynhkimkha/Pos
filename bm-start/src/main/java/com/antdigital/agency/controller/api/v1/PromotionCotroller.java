package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotion")
public class PromotionCotroller {
    @Autowired
    private IPromotionService promotionService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<PromotionDto> sizeDtos = promotionService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giảm giá"), HttpStatus.OK.value(), sizeDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<PromotionDto>> searchDto) {
        BaseSearchDto<List<PromotionDto>> search = promotionService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giảm giá"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/get-full/{promotionId}")
    public ResponseEntity<?> getPromotionDtoFull(@PathVariable String promotionId) {
        PromotionFullDto promotionFullDto = promotionService.getPromotionFull(promotionId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giảm giá"), HttpStatus.OK.value(), promotionFullDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody PromotionFullDto promotionDto) {
        List<String> errMessages = validateInserting(promotionDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PromotionFullDto promotion = promotionService.insert(promotionDto);

        ResponseEntity<?> res = promotion != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin sản phẩm thành công"), HttpStatus.OK.value(), promotion))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody PromotionFullDto promotionDto) {
        List<String> errMessages = validateUpdating(promotionDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        PromotionFullDto promotion = promotionService.update(promotionDto);
        ResponseEntity<?> res = promotion != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin sản phẩm thành công"), HttpStatus.OK.value(), promotion))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam String id) throws IOException, JAXBException {
        List<String> errMessages = validateDeleting(request, id);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = promotionService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xoá thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(PromotionFullDto promotionDto) {
        List<String> result = new ArrayList<>();


        return result;
    }

    private List<String> validateUpdating(PromotionFullDto promotionDto) {
        List<String> result = new ArrayList<>();

        return result;
    }

    private List<String> validateDeleting(HttpServletRequest request, String id) throws IOException, JAXBException {
        List<String> result = new ArrayList<>();
        if (id.isEmpty()) {
            result.add("Không tồn tại hàng hóa này");
        }

        // TODO check product in bill_product

        return result;
    }
}
