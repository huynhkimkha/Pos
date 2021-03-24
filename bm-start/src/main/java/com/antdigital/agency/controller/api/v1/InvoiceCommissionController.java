package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dal.entity.InvoiceCommission;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.InvoiceCommissionDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.mappers.IInvoiceCommissionDtoMapper;
import com.antdigital.agency.services.IInvoiceCommissionService;
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
@RequestMapping("/api/v1/invoice-commission")
public class InvoiceCommissionController extends BaseController {
    @Autowired
    IInvoiceCommissionService invoiceCommissionService;

    @Autowired
    IInvoiceCommissionRepository invoiceCommissionRepository;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<InvoiceCommissionDto> invoiceCommissions = invoiceCommissionService.findAll(this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hoa hồng"), HttpStatus.OK.value(), invoiceCommissions));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<InvoiceCommissionDto>> searchDto) {
        BaseSearchDto<List<InvoiceCommissionDto>> search = invoiceCommissionService.findAll(searchDto, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách định mức hoa hồng"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        InvoiceCommissionDto invoiceCommission = invoiceCommissionService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Hoa hồng"), HttpStatus.OK.value(), invoiceCommission));
    }

    @GetMapping("/get-id-by-price")
    @RolesAllowed("INVOICE_COMMISSION_MANAGEMENT")
    public ResponseEntity<?> getIdByPrice(@RequestParam Double price) {
        List<String> commissioId = invoiceCommissionService.getIdByPrice(price, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Id"), HttpStatus.OK.value(), commissioId));
    }

    @PostMapping("/insert")
    @RolesAllowed("INVOICE_COMMISSION_MANAGEMENT")
    public ResponseEntity<?> insert(@Valid @RequestBody InvoiceCommissionDto invoiceCommissionDto) {
        invoiceCommissionDto.setCompanyId(this.getCompanyId());
        List<String> msg = validateInsert(invoiceCommissionDto);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        InvoiceCommissionDto invoiceCommission = invoiceCommissionService.insert(invoiceCommissionDto);

        ResponseEntity<?> res = invoiceCommission != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm định mức hoa hồng thành công"), HttpStatus.OK.value(), invoiceCommission))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm định mức hoa hồng"), HttpStatus.OK.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("INVOICE_COMMISSION_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody InvoiceCommissionDto invoiceCommissionDto) {
        invoiceCommissionDto.setCompanyId(this.getCompanyId());
        List<String> msg = validateUpdate(invoiceCommissionDto);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        InvoiceCommissionDto invoiceCommission = invoiceCommissionService.update(invoiceCommissionDto);

        ResponseEntity<?> res = invoiceCommission != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thành công"), HttpStatus.OK.value(), invoiceCommission))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật"), HttpStatus.OK.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("INVOICE_COMMISSION_MANAGEMENT")
    public ResponseEntity<?> delete(@PathVariable String id) {
        List<String> msg = validateDelete(id);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = invoiceCommissionService.delete(id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xoá định mức hoa hồng thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xoá định mức hoa hồng"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateUpdate(InvoiceCommissionDto invoiceCommissionDto) {
        List<String> result = new ArrayList<>();
        InvoiceCommission invoiceCommission = IInvoiceCommissionDtoMapper.INSTANCE.toInvoiceCommission(invoiceCommissionService.getById(invoiceCommissionDto.getId()));

        if (invoiceCommission == null) {
            result.add("Định mức không tồn tại");
        }
        if (invoiceCommissionDto.getMinRevenue() == null) {
            result.add("Thiếu mức doanh thu tối thiểu");
        }
        if (invoiceCommissionDto.getName().isEmpty() || invoiceCommissionDto.getName() == null) {
            result.add("Thiếu tên định mức");
        }
        if (invoiceCommissionDto.getApplyObject() == null) {
            result.add("Thiếu đối tượng");
        }
        if (invoiceCommissionDto.getBonus() == null) {
            result.add("Thiếu mức hoa hồng");
        }
        if (invoiceCommissionDto.getMinRevenue().compareTo(invoiceCommissionService.getById(invoiceCommissionDto.getId()).getMinRevenue()) != 0) {
            if (invoiceCommissionService.getExactByObjectAndRevenue(
                    invoiceCommissionDto.getApplyObject().toString(),
                    invoiceCommissionDto.getMinRevenue(),
                    invoiceCommission.getCompanyId()) != null) {
                result.add("Định mức hoa hồng đã tồn tại");
            }
        }
        if (invoiceCommissionDto.getName().compareTo(invoiceCommissionService.getById(invoiceCommissionDto.getId()).getName()) != 0) {
            if (invoiceCommissionService.getByName(invoiceCommissionDto.getName(), invoiceCommissionDto.getCompanyId()) != null) {
                result.add("Tên định mức đã tồn tại");
            }
        }
        if (invoiceCommissionDto.getBonus() < 0) {
            result.add("Mức hoa hồng phải lớn hơn 0");
        }

        return result;
    }

    private List<String> validateInsert(InvoiceCommissionDto invoiceCommissionDto) {
        List<String> result = new ArrayList<>();

        if (invoiceCommissionDto.getMinRevenue() == null) {
            result.add("Thiếu mức doanh thu tối thiểu");
        }
        if (invoiceCommissionDto.getName().isEmpty() || invoiceCommissionDto.getName() == null) {
            result.add("Thiếu tên định mức");
        }
        if (invoiceCommissionDto.getBonus() == null) {
            result.add("Thiếu mức hoa hồng");
        }
        if (invoiceCommissionDto.getApplyObject() == null) {
            result.add("Thiếu đối tượng");
        }
        if (invoiceCommissionService.getExactByObjectAndRevenue(
                invoiceCommissionDto.getApplyObject().toString(),
                invoiceCommissionDto.getMinRevenue(),
                invoiceCommissionDto.getCompanyId()) != null) {
            result.add("Định mức hoa hồng đã tồn tại");
        }
        if (invoiceCommissionService.getByName(invoiceCommissionDto.getName(), invoiceCommissionDto.getCompanyId()) != null) {
            result.add("Tên định mức đã tồn tại");
        }
        if (invoiceCommissionDto.getBonus() < 0) {
            result.add("Mức hoa hồng phải lớn hơn 0");
        }

        return result;
    }

    private List<String> validateDelete(String id) {
        List<String> result = new ArrayList<>();

        if (id == null || id.isEmpty() || invoiceCommissionService.getById(id) == null) {
            result.add("Định mức không tồn tại");
        }

        return result;
    }
}
