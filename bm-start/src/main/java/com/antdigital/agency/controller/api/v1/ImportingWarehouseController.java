package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.*;
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
@RequestMapping("/api/v1/importing-warehouse")
public class ImportingWarehouseController extends BaseController {
    @Autowired
    IImportingWarehouseService importingWarehouseService;

    @Autowired
    IPaymentService paymentService;

    @Autowired
    IDebtReportService debtReportService;

    @Autowired
    IPaymentAdviceService paymentAdviceService;

    @Autowired
    IExportingReturnService exportingReturnService;

    @Autowired
    IAgencyService agencyService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ImportingWarehouseSearchDto importingWarehouseSearchDto)
            throws IOException, JAXBException {
        String agencyId = getAgencyId();
        ImportingWarehouseSearchDto search = importingWarehouseService.search(request, importingWarehouseSearchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        String agencyId = getAgencyId();
        List<ImportingWarehouseDto> importingWarehouses = importingWarehouseService.findAll(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ImportingWarehouseDto importingWarehouse = importingWarehouseService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouse));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ImportingWarehouseFullDto importingWarehouseFull = importingWarehouseService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouseFull));
    }

    @GetMapping("/not-complete")
    public ResponseEntity<?> getNotCompleted(@RequestParam String customerId, @RequestParam String code) {
        List<ImportingWarehouseDto> importingWarehouses = importingWarehouseService.getNotCompleted(customerId, code);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouses));
    }

    @GetMapping("/get-like-code")
    public ResponseEntity<?> getLikeCode(@RequestParam String code) {
        String agencyId = getAgencyId();
        List<ImportingWarehouseDto> importingWarehouses = importingWarehouseService.getLikeCode(code, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouses));
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<?> getNotCompleted( @PathVariable String id) {
        Double result = importingWarehouseService.getTotal(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String agencyId = getAgencyId();
        String number = importingWarehouseService.getNumber(createdDate, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), number));
    }

    @GetMapping("/getTransactionByOrderId/{id}")
    public ResponseEntity<?> getTransactionByOrderId(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        String agencyId = getAgencyId();
        List<ImportingTransactionFullDto> importingTransactionFullDtos = importingWarehouseService.GetByOrderId(request, id, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingTransactionFullDtos));
    }

    @GetMapping("/getDebt/{customerId}")
    public ResponseEntity<?> getCustomerDebt( @PathVariable String customerId) {
        String agencyId = getAgencyId();
        Double result = importingWarehouseService.getDebt(customerId, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nợ nhà cung cấp"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/getForPayment")
    public ResponseEntity<?> getForPayment(@RequestParam String customerId, @RequestParam Double amount) {
        List<ImportingWarehouseFullDto> importingWarehouses = importingWarehouseService.getForPayment(customerId, amount);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingWarehouses));
    }

    @GetMapping("/getForExportingReturn")
    public ResponseEntity<?> getForExportingReturn(@RequestParam String customerId, @RequestParam String merchandiseId, @RequestParam Float quantity) {
        List<ImportingTransactionDto> importingTransactions = importingWarehouseService.getImportingTransactionForReturn(customerId, merchandiseId, quantity);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), importingTransactions));
    }

    @PostMapping("/insert")
    @RolesAllowed("IMPORTING_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ImportingWarehouseFullDto importingWarehouseFullDto) throws IOException, JAXBException {
        List<String> msg = validateInsert(importingWarehouseFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        importingWarehouseFullDto.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        ImportingWarehouseFullDto importingWarehouseFullNewDto = importingWarehouseService.insert(request, importingWarehouseFullDto);

        //update monthly balance
        if (importingWarehouseFullDto != null) {
            updateMonthlyBalance(request, importingWarehouseFullDto);
        }
        if (importingWarehouseFullNewDto != null){
            updateMonthlyBalance(request, importingWarehouseFullNewDto);
        }

        ResponseEntity<?> res = importingWarehouseFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu nhập thành công"), HttpStatus.OK.value(), importingWarehouseFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("IMPORTING_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ImportingWarehouseFullDto importingWarehouseFullDto) throws IOException, JAXBException {
        List<String> msg = validateUpdate(importingWarehouseFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        importingWarehouseFullDto.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        ImportingWarehouseFullDto oldImportingWarehouseDto = importingWarehouseService.getFullById(request, importingWarehouseFullDto.getId());
        ImportingWarehouseFullDto importingWarehouseFullNewDto = importingWarehouseService.update(request, importingWarehouseFullDto);

        //update monthly balance
        if (importingWarehouseFullDto != null) {
            updateMonthlyBalance(request, oldImportingWarehouseDto, importingWarehouseFullDto);
        }
        if (importingWarehouseFullNewDto != null){
            updateMonthlyBalance(request, oldImportingWarehouseDto, importingWarehouseFullNewDto);
        }

        ResponseEntity<?> res = importingWarehouseFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu nhập thành công"), HttpStatus.OK.value(), importingWarehouseFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{importingId}")
    @RolesAllowed("IMPORTING_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String importingId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(importingId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ImportingWarehouseFullDto importingWarehouseFullDto = importingWarehouseService.getFullById(request ,importingId);
        boolean result = importingWarehouseService.delete(request, importingId);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, importingWarehouseFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu nhập thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu nhập"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(ImportingWarehouseFullDto importingWarehouseFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        ImportingWarehouseDto importingWarehouseDto = importingWarehouseService.getByCodeAndNumber(
                importingWarehouseFull.getCode(),
                importingWarehouseFull.getNumber(),
                DateUtils.year(importingWarehouseFull.getCreatedDate()),
                agencyId
        );
        if (importingWarehouseDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (importingWarehouseFull.getTransactionCustomer() == null || importingWarehouseFull.getTransactionCustomer().getId() == null
            || importingWarehouseFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Chưa nhập khách hàng giao dịch");
        }

        if (importingWarehouseFull.getImportTransactionFulls() == null || importingWarehouseFull.getImportTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateUpdate(ImportingWarehouseFullDto importingWarehouseFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        ImportingWarehouseDto importingWarehouseDto = importingWarehouseService.getById(importingWarehouseFull.getId());
        ImportingWarehouseDto importingWarehouse = importingWarehouseService.getByCodeAndNumber(
                importingWarehouseFull.getCode(),
                importingWarehouseFull.getNumber(),
                DateUtils.year(importingWarehouseFull.getCreatedDate()),
                agencyId
        );

        if (importingWarehouse != null && !importingWarehouse.getId().equals(importingWarehouseFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (importingWarehouseDto.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            result.add("Không thể cập nhật phiếu đã hoàn thành chi trả");
        }

        if (importingWarehouseFull.getId() == null || importingWarehouseFull.getId().isEmpty()) {
            result.add("Phiếu nhập kho không hợp lệ");
        }

        if (importingWarehouseFull.getImportTransactionFulls() == null || importingWarehouseFull.getImportTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        int countPayment = paymentService.countByImportingId(id);
        if (countPayment > 0) {
            result.add("Không thể xóa phiếu đã có thanh toán tiền mặt");
            return result;
        }

        int countPaymentAdvice = paymentAdviceService.countByImportingId(id);
        if (countPaymentAdvice > 0) {
            result.add("Không thể xóa phiếu đã có thanh toán qua ngân hàng");
            return result;
        }

        int countExportingReturn = exportingReturnService.countByImportingId(id);
        if (countExportingReturn > 0) {
            result.add("Không thể xóa phiếu đã có trả hàng");
            return result;
        }

        if(id == null || id.isEmpty()) {
            result.add("Phiếu nhập kho không hợp lệ");
        }

        ImportingWarehouseDto importingWarehouseDto = importingWarehouseService.getById(id);
        if (importingWarehouseDto.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            result.add("Không thể xóa phiếu đã hoàn thành chi trả");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, ImportingWarehouseFullDto importingWarehouseFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(importingWarehouseFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, importingWarehouseFullDto.getCreatedDate(), importingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ImportingWarehouseFullDto oldImportingWarehouseDto, ImportingWarehouseFullDto importingWarehouseFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldImportingWarehouseDto.getCreatedDate());
        createdDate.setTime(importingWarehouseFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldImportingWarehouseDto.getCreatedDate(), oldImportingWarehouseDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldImportingWarehouseDto.getTransactionCustomer().getId().equals(importingWarehouseFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, importingWarehouseFullDto.getCreatedDate(), importingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, importingWarehouseFullDto.getCreatedDate(), importingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldImportingWarehouseDto.getTransactionCustomer().getId().equals(importingWarehouseFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldImportingWarehouseDto.getCreatedDate(), oldImportingWarehouseDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
