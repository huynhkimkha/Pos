package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.CustomersDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController extends BaseController {
    @Autowired
    private ICustomerService customerService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<CustomersDto> customers = customerService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Khách hàng"), HttpStatus.OK.value(), customers));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<CustomersDto>> searchDto) {
        BaseSearchDto<List<CustomersDto>> search = customerService.findAll(searchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Khách hàng"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String id) {
        CustomersDto customerDto = customerService.getCustomerById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Khách hàng"), HttpStatus.OK.value(), customerDto));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody CustomersDto customerDto) {
        List<String> errMessages = validateInserting(customerDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        CustomersDto customerDtoDetail = customerService.insert(customerDto);

        ResponseEntity<?> res = customerDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin khách hàng thành công"), HttpStatus.OK.value(), customerDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin khách hàng"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody CustomersDto customerDto) {
        List<String> errMessages = validateUpdating(customerDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        CustomersDto customerDtoDetail = customerService.update(customerDto);
        ResponseEntity<?> res = customerDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin khách hàng thành công"), HttpStatus.OK.value(), customerDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin khách hàng"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = customerService.deleteCustomer(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Khách hàng"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin khách hàng"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateUpdating(CustomersDto customerDto){
        List<String> result = new ArrayList<>();
        CustomersDto customerDtoPhone = customerService.getCustomerByPhone(customerDto.getPhone());

        if (customerService.getCustomerById(customerDto.getId()) == null) {
            result.add("Không tồn tại khách hàng này");
        }
        if (customerDto == null || customerDto.getId() == null || customerDto.getId().isEmpty()) {
            result.add("Thông tin không hợp lệ");
        }
        if(customerDtoPhone != null  && !customerDtoPhone.getId().equals(customerDto.getId())){
            result.add("Số điện thoại đã tồn tại");
        }

        return result;
    }

    private List<String> validateInserting(CustomersDto customerDto) {
        List<String> result = new ArrayList<>();
        CustomersDto customerDtoPhone = customerService.getCustomerByPhone(customerDto.getPhone());

        if(customerDtoPhone != null){
            result.add("Số điện thoại đã tồn tại");
        }

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
