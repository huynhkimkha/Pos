package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IAgencyService;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IExportingReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.DateUtils;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/v1/exporting-return")
public class ExportingReturnController extends BaseController {
    @Autowired
    IExportingReturnService exportingReturnService;
    @Autowired
    IDebtReportService debtReportService;
    @Autowired
    IAgencyService agencyService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ExportingReturnSearchDto exportingReturnSearchDto) throws IOException, JAXBException {
        ExportingReturnSearchDto searchDto = exportingReturnService.search(request, exportingReturnSearchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất trả"), HttpStatus.OK.value(), searchDto));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ExportingReturnDto> exportingReturns = exportingReturnService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất hàng hóa trả lại người bán"), HttpStatus.OK.value(), exportingReturns));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<ExportingReturnDto>> searchDto) {
        BaseSearchDto<List<ExportingReturnDto>> search = exportingReturnService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất hàng hóa trả lại người bán"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ExportingReturnFullDto exportingReturnFull = exportingReturnService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất hàng hóa trả lại người bán"), HttpStatus.OK.value(), exportingReturnFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String number = exportingReturnService.getNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất hàng hóa trả lại người bán"), HttpStatus.OK.value(), number));
    }

    @GetMapping("/getByImportingWarehouse/{importingWarehouseId}")
    public ResponseEntity<?> getByImportingWarehouseId(@PathVariable String importingWarehouseId) {
        List<ExportingReturnTransactionDto> exportingReturnTransactionDtos = exportingReturnService.getByImportingWarehouseId(importingWarehouseId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất hàng hóa trả lại người bán"), HttpStatus.OK.value(), exportingReturnTransactionDtos));
    }

    @PostMapping("/insert")
    @RolesAllowed("EXPORTING_RETURN_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ExportingReturnFullDto exportingReturnFull) throws IOException, JAXBException {
        exportingReturnFull.setAgency(agencyService.getAgencyById(this.getAgencyId()));

        List<String> msg = validateInsert(exportingReturnFull);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingReturnFullDto exportingReturnFullDto = exportingReturnService.insert(request, exportingReturnFull);

        //update monthly balance
        if (exportingReturnFullDto != null) {
            updateMonthlyBalance(request, exportingReturnFullDto);
        }

        ResponseEntity<?> res = exportingReturnFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu xuất hàng hóa trả lại người bán thành công"), HttpStatus.OK.value(), exportingReturnFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu xuất hàng hóa trả lại người bán"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("EXPORTING_RETURN_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ExportingReturnFullDto exportingReturnFull) throws IOException, JAXBException {
        if(exportingReturnFull.getAgency() == null || exportingReturnFull.getAgency().getId() == null || exportingReturnFull.getAgency().getId().isEmpty()){
            exportingReturnFull.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        }
        List<String> msg = validateUpdate(exportingReturnFull);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingReturnFullDto oldExportingReturnDto = exportingReturnService.getFullById(request, exportingReturnFull.getId());
        ExportingReturnFullDto exportingReturnFullDto = exportingReturnService.update(request, exportingReturnFull);

        //update monthly balance
        if (exportingReturnFullDto != null) {
            updateMonthlyBalance(request, oldExportingReturnDto, exportingReturnFullDto);
        }

        ResponseEntity<?> res = exportingReturnFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu xuất hàng hóa trả lại người bán thành công"), HttpStatus.OK.value(), exportingReturnFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu xuất hàng hóa trả lại người bán"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{exportingId}")
    @RolesAllowed("EXPORTING_RETURN_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String exportingId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(exportingId);
        if (msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingReturnFullDto exportingReturnFullDto = exportingReturnService.getFullById(request, exportingId);
        boolean result = exportingReturnService.delete(request, exportingId);

        //update monthly balance
        if (result){
            updateMonthlyBalance(request, exportingReturnFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu xuất hàng hóa trả lại người bán thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu xuất hàng hóa trả lại người bán"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(ExportingReturnFullDto exportingReturnFull) {
        List<String> result = new ArrayList<>();
        ExportingReturnDto exportingReturnDto = exportingReturnService.getByCodeAndNumber(
                exportingReturnFull.getCode(),
                exportingReturnFull.getNumber(),
                DateUtils.year(exportingReturnFull.getCreatedDate()),
                exportingReturnFull.getAgency().getId()
        );
        if (exportingReturnDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (exportingReturnFull.getTransactionCustomer() == null || exportingReturnFull.getTransactionCustomer().getId() == null
                || exportingReturnFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Chưa nhập khách hàng giao dịch");
        }

        if (exportingReturnFull.getExportReturnTransactionFulls() == null || exportingReturnFull.getExportReturnTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateUpdate(ExportingReturnFullDto exportingReturnFull) {
        List<String> result = new ArrayList<>();

        if (exportingReturnFull.getId() == null || exportingReturnFull.getId().isEmpty()) {
            result.add("Phiếu xuất hàng hóa trả lại người bán không hợp lệ");
        }

        if (exportingReturnFull.getExportReturnTransactionFulls() == null || exportingReturnFull.getExportReturnTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if (id == null || id.isEmpty()) {
            result.add("Không tồn tại phiếu xuất hàng hóa trả lại người bán này");
        }
        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, ExportingReturnFullDto exportingReturnFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(exportingReturnFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, exportingReturnFullDto.getCreatedDate(), exportingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ExportingReturnFullDto oldExportingReturnDto, ExportingReturnFullDto exportingReturnFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldExportingReturnDto.getCreatedDate());
        createdDate.setTime(exportingReturnFullDto.getCreatedDate());

        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldExportingReturnDto.getCreatedDate(), oldExportingReturnDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldExportingReturnDto.getTransactionCustomer().getId().equals(exportingReturnFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, exportingReturnFullDto.getCreatedDate(), exportingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, exportingReturnFullDto.getCreatedDate(), exportingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldExportingReturnDto.getTransactionCustomer().getId().equals(exportingReturnFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldExportingReturnDto.getCreatedDate(), oldExportingReturnDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
