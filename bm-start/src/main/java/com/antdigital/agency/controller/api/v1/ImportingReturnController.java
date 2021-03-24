package com.antdigital.agency.controller.api.v1;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.IAgencyDtoMapper;
import com.antdigital.agency.services.IAgencyService;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IImportingReturnService;

import com.antdigital.agency.services.IPaymentService;
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
@RequestMapping("/api/v1/importing-return")
public class ImportingReturnController extends BaseController {
    @Autowired
    IImportingReturnService importingReturnService;

    @Autowired
    IPaymentService paymentService;

    @Autowired
    IDebtReportService debtReportService;

    @Autowired
    IAgencyService agencyService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ImportingReturnSearchDto importingReturnSearchDto) throws IOException, JAXBException {
        ImportingReturnSearchDto searchDto = importingReturnService.search(request, importingReturnSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập trả"), HttpStatus.OK.value(), searchDto));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        String agencyId = getAgencyId();
        List<ImportingReturnDto> importingReturns = importingReturnService.findAll(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), importingReturns));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<ImportingReturnDto>> searchDto) {
        String agencyId = getAgencyId();
        BaseSearchDto<List<ImportingReturnDto>> search = importingReturnService.findAll(searchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ImportingReturnDto importingReturn = importingReturnService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), importingReturn));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ImportingReturnFullDto importingReturnFull = importingReturnService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), importingReturnFull));
    }

    @GetMapping("/get-import-return-transaction/{exportId}")
    public ResponseEntity<?> getTransactionById(@PathVariable String exportId) {
        List<ImportingReturnTransactionDto> importReturnTransactions = importingReturnService.getTransactionById(exportId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), importReturnTransactions));
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<?> getTotal(@PathVariable String id) {
        Double result = importingReturnService.getTotal(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String agencyId = getAgencyId();
        String number = importingReturnService.getNumber(createdDate, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập hàng bán bị trả lại"), HttpStatus.OK.value(), number));
    }

    @PostMapping("/insert")
    @RolesAllowed("IMPORT_RETURN_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ImportingReturnFullDto importingReturnFullDto) throws IOException, JAXBException {
        List<String> msg = validateInsert(importingReturnFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        importingReturnFullDto.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        ImportingReturnFullDto importingReturnFullNewDto = importingReturnService.insert(request, importingReturnFullDto);

        //update monthly balance
        if (importingReturnFullNewDto != null){
            updateMonthlyBalance(request, importingReturnFullNewDto);
        }

        ResponseEntity<?> res = importingReturnFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu nhập hàng bán bị trả lại thành công"), HttpStatus.OK.value(), importingReturnFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu nhập hàng bán bị trả lại"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("IMPORT_RETURN_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ImportingReturnFullDto importingReturnFullDto) throws IOException, JAXBException {
        List<String> msg = validateUpdate(importingReturnFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        importingReturnFullDto.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        ImportingReturnFullDto oldImportingReturnDto = importingReturnService.getFullById(request, importingReturnFullDto.getId());
        ImportingReturnFullDto importingReturnFullNewDto = importingReturnService.update(request, importingReturnFullDto);

        //update monthly balance
        if (importingReturnFullNewDto != null){
            updateMonthlyBalance(request, oldImportingReturnDto, importingReturnFullNewDto);
        }

        ResponseEntity<?> res = importingReturnFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập phiếu nhập hàng bán bị trả lại thành công"), HttpStatus.OK.value(), importingReturnFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu nhập hàng bán bị trả lại"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{importingId}")
    @RolesAllowed("IMPORT_RETURN_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String importingId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(importingId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ImportingReturnFullDto importingReturnFullDto = importingReturnService.getFullById(request, importingId);
        boolean result = importingReturnService.delete(request, importingId);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, importingReturnFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu nhập hàng bán bị trả lại thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu nhập hàng bán bị trả lại"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }


    private List<String> validateInsert(ImportingReturnFullDto importingReturnFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        ImportingReturnDto importingReturnDto = importingReturnService.getByCodeAndNumber(
                importingReturnFull.getCode(),
                importingReturnFull.getNumber(),
                DateUtils.year(importingReturnFull.getCreatedDate()),
                agencyId
        );

        if (importingReturnDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (importingReturnFull.getTransactionCustomer() == null || importingReturnFull.getTransactionCustomer().getId() == null
                || importingReturnFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Chưa nhập khách hàng giao dịch");
        }

        if (importingReturnFull.getImportReturnTransactionFulls() == null || importingReturnFull.getImportReturnTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateUpdate(ImportingReturnFullDto importingReturnFull) {
        List<String> result = new ArrayList<>();
        if (importingReturnFull.getId() == null || importingReturnFull.getId().isEmpty()) {
            result.add("Phiếu nhập kho không hợp lệ");
        }

        if (importingReturnFull.getImportReturnTransactionFulls() == null || importingReturnFull.getImportReturnTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }
    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Phiếu nhập kho không hợp lệ");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, ImportingReturnFullDto importingReturnFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(importingReturnFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, importingReturnFullDto.getCreatedDate(), importingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ImportingReturnFullDto oldImportingReturnDto, ImportingReturnFullDto importingReturnFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldImportingReturnDto.getCreatedDate());
        createdDate.setTime(importingReturnFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldImportingReturnDto.getCreatedDate(), oldImportingReturnDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldImportingReturnDto.getTransactionCustomer().getId().equals(importingReturnFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, importingReturnFullDto.getCreatedDate(), importingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, importingReturnFullDto.getCreatedDate(), importingReturnFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldImportingReturnDto.getTransactionCustomer().getId().equals(importingReturnFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldImportingReturnDto.getCreatedDate(), oldImportingReturnDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
