package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.ProductDto;
import com.antdigital.agency.dtos.response.ProductFullDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IProductService;
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
@RequestMapping("/api/v1/product")
public class ProductController extends BaseController {

    @Autowired
    IProductService productService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ProductDto> productDtos = productService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), productDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<ProductDto>> searchDto) {
        BaseSearchDto<List<ProductDto>> search = productService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        ProductDto productDto = productService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hàng hóa"), HttpStatus.OK.value(), productDto));
    }

    @GetMapping("/like-slug-name")
    public ResponseEntity<?> getLikeSlugOrName(@RequestParam String name) {
        List<ProductDto> productDtoList = productService.getLikeSlugOrName(name);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), productDtoList));
    }

    @GetMapping("/like-slug")
    public ResponseEntity<?> getLikeSlug(@RequestParam String slug) {
        List<ProductDto> productDtoList = productService.getLikeSlug(slug);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), productDtoList));
    }

    @PostMapping("/list")
    public ResponseEntity<?> findById(@RequestBody List<String> ids) {
        List<ProductDto> productDtoList = productService.getProducts(ids);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), productDtoList));
    }

    @GetMapping("/get-full/{productId}")
    public ResponseEntity<?> getProductFull(@PathVariable String productId) {
        ProductFullDto productFullDto = productService.getProductFull(productId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Sản phẩm"), HttpStatus.OK.value(), productFullDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody ProductFullDto productDto) {
        List<String> errMessages = validateInserting(productDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ProductFullDto product = productService.insert(productDto);

        ResponseEntity<?> res = product != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin sản phẩm thành công"), HttpStatus.OK.value(), product))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ProductFullDto productDto) {
        List<String> errMessages = validateUpdating(productDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        ProductFullDto product = productService.update(productDto);
        ResponseEntity<?> res = product != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin sản phẩm thành công"), HttpStatus.OK.value(), product))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam String id) throws IOException, JAXBException {
        List<String> errMessages = validateDeleting(request, id);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = productService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xoá thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin sản phẩm"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(ProductFullDto productDto) {
        List<String> result = new ArrayList<>();

        ProductDto productSlug = productService.getBySlug(productDto.getNameSlug());

        if (productSlug != null) {
            result.add("Mã sản phẩm đã tồn tại");
        }

        return result;
    }

    private List<String> validateUpdating(ProductFullDto productDto) {
        List<String> result = new ArrayList<>();
        if(productService.getById(productDto.getId()) == null){
            result.add("Không tồn tại sản phẩm này");
        }
        if (productDto.getId().isEmpty()) {
            result.add("Thông tin id bắt buộc");
        }

        ProductDto productSlug = productService.getBySlug(productDto.getNameSlug());

        if (productSlug != null && !productSlug.getId().equals(productDto.getId())) {
            result.add("Mã sản phẩm đã tồn tại");
        }

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
