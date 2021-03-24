package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dal.entity.ReceiptAdvice;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.ReceiptAdviceSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IReceiptAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.annotation.security.RolesAllowed;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipt-advice")
public class ReceiptAdviceController extends BaseController {
    @Autowired
    IReceiptAdviceService receiptAdviceService;

    @Autowired
    IDebtReportService debtReportService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ReceiptAdviceSearchDto receiptAdviceSearchDto)
            throws IOException, JAXBException {
        ReceiptAdviceSearchDto search = receiptAdviceService.search(request, receiptAdviceSearchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ReceiptAdviceDto> receiptAdvices = receiptAdviceService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receiptAdvices));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<ReceiptAdviceDto>> searchDto) {
        BaseSearchDto<List<ReceiptAdviceDto>> search = receiptAdviceService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ReceiptAdviceDto receiptAdvice = receiptAdviceService.getById(id, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receiptAdvice));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ReceiptAdviceFullDto receiptAdviceFull = receiptAdviceService.getFullById(request, id, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receiptAdviceFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getOrderNumber(@PathVariable String createdDate) {
        String paymentAdviceNumber = receiptAdviceService.getReceiptAdviceNumber(createdDate, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), paymentAdviceNumber));
    }

    @PostMapping("/insert")
    @RolesAllowed("RECEIPT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ReceiptAdviceFullDto receiptAdviceFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(this.getAgencyId());
        receiptAdviceFull.setAgency(agencyDto);
        List<String> msg = validateInsert(receiptAdviceFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptAdviceFullDto receiptAdviceFullDto = receiptAdviceService.insert(receiptAdviceFull, this.getAgencyId());

        if (receiptAdviceFullDto != null){
            updateMonthlyBalance(request, receiptAdviceFullDto);
        }

        ResponseEntity<?> res = receiptAdviceFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu thu thành công"), HttpStatus.OK.value(), receiptAdviceFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("RECEIPT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ReceiptAdviceFullDto receiptAdviceFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(this.getAgencyId());
        receiptAdviceFull.setAgency(agencyDto);
        List<String> msg = validateUpdate(receiptAdviceFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptAdviceFullDto oldReceiptAdviceDto = receiptAdviceService.getFullById(request, receiptAdviceFull.getId(), this.getAgencyId());
        ReceiptAdviceFullDto receiptAdviceFullDto = receiptAdviceService.update(receiptAdviceFull, this.getAgencyId());

        //update monthly balance
        if (receiptAdviceFullDto != null){
            updateMonthlyBalance(request, oldReceiptAdviceDto, receiptAdviceFullDto);
        }

        ResponseEntity<?> res = receiptAdviceFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu thu thành công"), HttpStatus.OK.value(), receiptAdviceFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{receiptId}")
    @RolesAllowed("RECEIPT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String receiptId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(receiptId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptAdviceFullDto receiptAdviceFullDto = receiptAdviceService.getFullById(request ,receiptId, this.getAgencyId());
        boolean result = receiptAdviceService.delete(receiptId, this.getAgencyId());

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, receiptAdviceFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu thu thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(ReceiptAdviceFullDto receiptAdviceFull) {
        List<String> result = new ArrayList<>();
        ReceiptAdviceDto receiptAdviceDto = receiptAdviceService.getByCodeAndNumber(
                receiptAdviceFull.getCode(),
                receiptAdviceFull.getNumber(),
                DateUtils.year(receiptAdviceFull.getCreatedDate()),
                this.getAgencyId()
        );

        if (receiptAdviceDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }
        if (receiptAdviceFull.getCustomer() == null || receiptAdviceFull.getCustomer().getId() == null
                || receiptAdviceFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (receiptAdviceFull.getTransactionCustomer() == null || receiptAdviceFull.getTransactionCustomer().getId() == null
                || receiptAdviceFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (receiptAdviceFull.getReceiptAdviceDetails() == null || receiptAdviceFull.getReceiptAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateUpdate(ReceiptAdviceFullDto receiptAdviceFull) {
        List<String> result = new ArrayList<>();
        ReceiptAdviceDto receiptAdviceDto = receiptAdviceService.getByCodeAndNumber(
                receiptAdviceFull.getCode(),
                receiptAdviceFull.getNumber(),
                DateUtils.year(receiptAdviceFull.getCreatedDate()),
                this.getAgencyId()
        );

        if (receiptAdviceDto != null && !receiptAdviceDto.getId().equals(receiptAdviceFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (receiptAdviceFull.getId() == null || receiptAdviceFull.getId().isEmpty()) {
            result.add("Thông tin phiếu thu không hợp lệ");
        }

        if (receiptAdviceFull.getCustomer() == null || receiptAdviceFull.getCustomer().getId() == null
                || receiptAdviceFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (receiptAdviceFull.getTransactionCustomer() == null || receiptAdviceFull.getTransactionCustomer().getId() == null
                || receiptAdviceFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (receiptAdviceFull.getReceiptAdviceDetails() == null || receiptAdviceFull.getReceiptAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại phiếu thu này");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, ReceiptAdviceFullDto receiptAdviceFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(receiptAdviceFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, receiptAdviceFullDto.getCreatedDate(), receiptAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ReceiptAdviceFullDto oldReceiptAdviceDto, ReceiptAdviceFullDto receiptAdviceFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldReceiptAdviceDto.getCreatedDate());
        createdDate.setTime(receiptAdviceFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldReceiptAdviceDto.getCreatedDate(), oldReceiptAdviceDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldReceiptAdviceDto.getTransactionCustomer().getId().equals(receiptAdviceFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, receiptAdviceFullDto.getCreatedDate(), receiptAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, receiptAdviceFullDto.getCreatedDate(), receiptAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldReceiptAdviceDto.getTransactionCustomer().getId().equals(receiptAdviceFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldReceiptAdviceDto.getCreatedDate(), oldReceiptAdviceDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
