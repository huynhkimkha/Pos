package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.PaymentAdviceSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IPaymentAdviceService;

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
@RequestMapping("/api/v1/payment-advice")
public class PaymentAdviceController extends BaseController {
    @Autowired
    IPaymentAdviceService paymentAdviceService;

    @Autowired
    IDebtReportService debtReportService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody PaymentAdviceSearchDto paymentAdviceSearchDto)
            throws IOException, JAXBException {
        String agencyId = getAgencyId();
        PaymentAdviceSearchDto search = paymentAdviceService.search(request, paymentAdviceSearchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giấy báo nợ chi"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        String agencyId = getAgencyId();
        List<PaymentAdviceDto> paymentAdvices = paymentAdviceService.findAll(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giấy báo nợ chi"), HttpStatus.OK.value(), paymentAdvices));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        PaymentAdviceDto paymentAdvice = paymentAdviceService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giấy báo nợ chi"), HttpStatus.OK.value(), paymentAdvice));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        PaymentAdviceFullDto paymentAdviceFull = paymentAdviceService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giấy báo nợ chi"), HttpStatus.OK.value(), paymentAdviceFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getOrderNumber(@PathVariable String createdDate) {
        String agencyId = getAgencyId();
        String paymentAdviceNumber = paymentAdviceService.getPaymentAdviceNumber(createdDate, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Giấy báo nợ chi"), HttpStatus.OK.value(), paymentAdviceNumber));
    }

    @PostMapping("/insert")
    @RolesAllowed("PAYMENT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        List<String> msg = validateInsert(paymentAdviceFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        paymentAdviceFullDto.setAgency(new AgencyDto());
        paymentAdviceFullDto.getAgency().setId(getAgencyId());
        PaymentAdviceFullDto paymentAdviceFullNewDto = paymentAdviceService.insert(paymentAdviceFullDto);

        //update monthly balance
        if (paymentAdviceFullDto != null){
            updateMonthlyBalance(request, paymentAdviceFullNewDto);
        }

        ResponseEntity<?> res = paymentAdviceFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm giấy báo nợ chi thành công"), HttpStatus.OK.value(), paymentAdviceFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm giấy báo nợ chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/insert-payment-commission")
    @RolesAllowed("PAYMENT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> insertPaymentCommission(HttpServletRequest request, @Valid @RequestBody PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        List<String> msg = validateInsertPaymentCommission(paymentAdviceFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        paymentAdviceFullDto.setAgency(new AgencyDto());
        paymentAdviceFullDto.getAgency().setId(getAgencyId());
        PaymentAdviceFullDto paymentAdviceFullNewDto = paymentAdviceService.insertPaymentCommission(paymentAdviceFullDto);

        //update monthly balance
        if (paymentAdviceFullNewDto != null){
            updateMonthlyBalance(request, paymentAdviceFullNewDto);
        }

        ResponseEntity<?> res = paymentAdviceFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm giấy báo nợ chi thành công"), HttpStatus.OK.value(), paymentAdviceFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm giấy báo nợ chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("PAYMENT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        List<String> msg = validateUpdate(paymentAdviceFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        paymentAdviceFullDto.setAgency(new AgencyDto());
        paymentAdviceFullDto.getAgency().setId(getAgencyId());
        PaymentAdviceFullDto oldPaymentAdviceFullDto = paymentAdviceService.getFullById(request, paymentAdviceFullDto.getId());
        PaymentAdviceFullDto paymentAdviceFullNewDto = paymentAdviceService.update(paymentAdviceFullDto);

        //update monthly balance
        if (paymentAdviceFullNewDto != null){
            updateMonthlyBalance(request, oldPaymentAdviceFullDto, paymentAdviceFullNewDto);
        }

        ResponseEntity<?> res = paymentAdviceFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật giấy báo nợ chi thành công"), HttpStatus.OK.value(), paymentAdviceFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật giấy báo nợ chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update-payment-commission")
    @RolesAllowed("PAYMENT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> updatePaymentCommission(HttpServletRequest request, @Valid @RequestBody PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        List<String> msg = validateUpdatePaymentCommission(paymentAdviceFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        paymentAdviceFullDto.setAgency(new AgencyDto());
        paymentAdviceFullDto.getAgency().setId(getAgencyId());
        PaymentAdviceFullDto oldPaymentAdviceFullDto = paymentAdviceService.getFullById(request, paymentAdviceFullDto.getId());
        PaymentAdviceFullDto paymentAdviceFullNewDto = paymentAdviceService.updatePaymentCommission(paymentAdviceFullDto);

        //update monthly balance
        if (paymentAdviceFullNewDto != null){
            updateMonthlyBalance(request, oldPaymentAdviceFullDto, paymentAdviceFullNewDto);
        }

        ResponseEntity<?> res = paymentAdviceFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật giấy báo nợ chi thành công"), HttpStatus.OK.value(), paymentAdviceFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật giấy báo nợ chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("PAYMENT_ADVICE_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        List<String> msg = validateDeleting(id);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentAdviceFullDto paymentAdviceFullDto= paymentAdviceService.getFullById(request ,id);
        boolean result = paymentAdviceService.delete(id);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, paymentAdviceFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa giấy báo nợ chi thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa giấy báo nợ chi"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(PaymentAdviceFullDto paymentAdviceFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        PaymentAdviceDto paymentAdviceDto = paymentAdviceService.getByCodeAndNumber(
                paymentAdviceFull.getCode(),
                paymentAdviceFull.getNumber(),
                DateUtils.year(paymentAdviceFull.getCreatedDate()),
                agencyId
        );

        if (paymentAdviceDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }
        if (paymentAdviceFull.getCustomer() == null || paymentAdviceFull.getCustomer().getId() == null
                || paymentAdviceFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (paymentAdviceFull.getTransactionCustomer() == null || paymentAdviceFull.getTransactionCustomer().getId() == null
                || paymentAdviceFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (paymentAdviceFull.getPaymentAdviceDetails() == null || paymentAdviceFull.getPaymentAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }


        return result;
    }

    private List<String> validateInsertPaymentCommission(PaymentAdviceFullDto paymentAdviceFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        PaymentAdviceDto paymentAdviceDto = paymentAdviceService.getByCodeAndNumber(
                paymentAdviceFull.getCode(),
                paymentAdviceFull.getNumber(),
                DateUtils.year(paymentAdviceFull.getCreatedDate()),
                agencyId
        );

        if (paymentAdviceDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentAdviceFull.getPaymentAdviceDetails() == null || paymentAdviceFull.getPaymentAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }


        return result;
    }


    private List<String> validateUpdate(PaymentAdviceFullDto paymentAdviceFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        PaymentAdviceDto paymentAdviceDto = paymentAdviceService.getByCodeAndNumber(
                paymentAdviceFull.getCode(),
                paymentAdviceFull.getNumber(),
                DateUtils.year(paymentAdviceFull.getCreatedDate()),
                agencyId
        );

        if (paymentAdviceDto != null && !paymentAdviceDto.getId().equals(paymentAdviceFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentAdviceFull.getId() == null || paymentAdviceFull.getId().isEmpty()) {
            result.add("Thông tin giấy báo nợ chi không hợp lệ");
        }

        if (paymentAdviceFull.getCustomer() == null || paymentAdviceFull.getCustomer().getId() == null
                || paymentAdviceFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (paymentAdviceFull.getTransactionCustomer() == null || paymentAdviceFull.getTransactionCustomer().getId() == null
                || paymentAdviceFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (paymentAdviceFull.getPaymentAdviceDetails() == null || paymentAdviceFull.getPaymentAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateUpdatePaymentCommission(PaymentAdviceFullDto paymentAdviceFull) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        PaymentAdviceDto paymentAdviceDto = paymentAdviceService.getByCodeAndNumber(
                paymentAdviceFull.getCode(),
                paymentAdviceFull.getNumber(),
                DateUtils.year(paymentAdviceFull.getCreatedDate()),
                agencyId
        );

        if (paymentAdviceDto != null && !paymentAdviceDto.getId().equals(paymentAdviceFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentAdviceFull.getId() == null || paymentAdviceFull.getId().isEmpty()) {
            result.add("Thông tin giấy báo nợ chi không hợp lệ");
        }

        if (paymentAdviceFull.getPaymentAdviceDetails() == null || paymentAdviceFull.getPaymentAdviceDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại giấy báo nợ chi này");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(paymentAdviceFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, paymentAdviceFullDto.getCreatedDate(), paymentAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, PaymentAdviceFullDto oldPaymentAdviceFullDto, PaymentAdviceFullDto paymentAdviceFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldPaymentAdviceFullDto.getCreatedDate());
        createdDate.setTime(paymentAdviceFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldPaymentAdviceFullDto.getCreatedDate(), oldPaymentAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldPaymentAdviceFullDto.getTransactionCustomer().getId().equals(paymentAdviceFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, paymentAdviceFullDto.getCreatedDate(), paymentAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, paymentAdviceFullDto.getCreatedDate(), paymentAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldPaymentAdviceFullDto.getTransactionCustomer().getId().equals(paymentAdviceFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldPaymentAdviceFullDto.getCreatedDate(), oldPaymentAdviceFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
