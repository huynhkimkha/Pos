package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IBillService;
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
@RequestMapping("/api/v1/bill")
public class BillController extends BaseController {
    @Autowired
    IBillService billService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<BillDto> billsDtos = billService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hóa đơn"), HttpStatus.OK.value(), billsDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<BillDto>> searchDto) {
        BaseSearchDto<List<BillDto>> search = billService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hóa đơn"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/get-full/{billId}")
    public ResponseEntity<?> getBillFull(@PathVariable String billId) {
        BillFullDto billsFullDto = billService.getBillFull(billId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hóa đơn"), HttpStatus.OK.value(), billsFullDto));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String number = billService.getNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hóa đơn"), HttpStatus.OK.value(), number));
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody BillFullDto billDto) {
        if(billDto.getAgency() == null || billDto.getAgency().getId() == null || billDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            billDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateInserting(billDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        BillFullDto bill = billService.insert(billDto);

        ResponseEntity<?> res = bill != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin hóa đơn thành công"), HttpStatus.OK.value(), bill))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin hóa đơn"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody BillFullDto billDto) {
        if(billDto.getAgency() == null || billDto.getAgency().getId() == null || billDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            billDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateUpdating(billDto);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        BillFullDto bill = billService.update(billDto);
        ResponseEntity<?> res = bill != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin hóa đơn thành công"), HttpStatus.OK.value(), bill))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin hóa đơn"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(HttpServletRequest request, @RequestParam String id) throws IOException, JAXBException {
        List<String> errMessages = validateDeleting(request, id);
        if (errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = billService.delete(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xoá hóa đơn thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin hóa đơn"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateInserting(BillFullDto billFullDto) {
        List<String> result = new ArrayList<>();

        return result;
    }

    private List<String> validateUpdating(BillFullDto billFullDto) {
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
