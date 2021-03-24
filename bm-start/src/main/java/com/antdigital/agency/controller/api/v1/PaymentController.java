package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IPaymentService;
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
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController extends BaseController {
    @Autowired
    IPaymentService paymentService;

    @Autowired
    IDebtReportService debtReportService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody PaymentSearchDto paymentSearchDto)
            throws IOException, JAXBException {
        PaymentSearchDto search = paymentService.search(request, paymentSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<PaymentDto> payments = paymentService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), payments));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<PaymentDto>> searchDto) {
        BaseSearchDto<List<PaymentDto>> search = paymentService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        PaymentDto payment = paymentService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), payment));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        PaymentFullDto paymentFull = paymentService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), paymentFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getPaymentNumber(@PathVariable String createdDate) {
        String paymentNumber = paymentService.getPaymentNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu chi"), HttpStatus.OK.value(), paymentNumber));
    }

    @PostMapping("/insert")
    @RolesAllowed("PAYMENT_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody PaymentFullDto paymentFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(getAgencyId());
        paymentFull.setAgency(agencyDto);
        List<String> msg = validateInsert(paymentFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentFullDto paymentFullDto = paymentService.insert(paymentFull);

        //update monthly balance
        if (paymentFullDto != null){
            updateMonthlyBalance(request, paymentFullDto);
        }

        ResponseEntity<?> res = paymentFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu chi thành công"), HttpStatus.OK.value(), paymentFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PostMapping("/insert-payment-commission")
    @RolesAllowed("PAYMENT_MANAGEMENT")
    public ResponseEntity<?> insertPaymentCommission(@Valid @RequestBody PaymentFullDto paymentFull) {
        if(paymentFull.getAgency() == null || paymentFull.getAgency().getId() == null || paymentFull.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            paymentFull.setAgency(agencyDto);
        }
        List<String> msg = validateInsertPaymentCommission(paymentFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentFullDto paymentFullDto = paymentService.insertPaymentCommission(paymentFull);

        ResponseEntity<?> res = paymentFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu chi thành công"), HttpStatus.OK.value(), paymentFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("PAYMENT_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody PaymentFullDto paymentFull) throws IOException, JAXBException {
        if(paymentFull.getAgency() == null || paymentFull.getAgency().getId() == null || paymentFull.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            paymentFull.setAgency(agencyDto);
        }
        List<String> msg = validateUpdate(paymentFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentFullDto oldPaymentFullDto = paymentService.getFullById(request, paymentFull.getId());
        PaymentFullDto paymentFullDto = paymentService.update(paymentFull);

        //update monthly balance
        if (paymentFullDto != null){
            updateMonthlyBalance(request, oldPaymentFullDto, paymentFullDto);
        }

        ResponseEntity<?> res = paymentFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu chi thành công"), HttpStatus.OK.value(), paymentFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update-payment-commission")
    @RolesAllowed("PAYMENT_MANAGEMENT")
    public ResponseEntity<?> updatePaymentCommission(HttpServletRequest request, @Valid @RequestBody PaymentFullDto paymentFull) {
        if(paymentFull.getAgency() == null || paymentFull.getAgency().getId() == null || paymentFull.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            paymentFull.setAgency(agencyDto);
        }
        List<String> msg = validateUpdatePaymentCommission(paymentFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentFullDto paymentFullDto = paymentService.updatePaymentCommission(paymentFull);

        ResponseEntity<?> res = paymentFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu chi thành công"), HttpStatus.OK.value(), paymentFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu chi"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("PAYMENT_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        List<String> msg = validateDeleting(id);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        PaymentFullDto paymentFullDto = paymentService.getFullById(request ,id);
        boolean result = paymentService.delete(id);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, paymentFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu chi thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu chi"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @PostMapping("/getMonthCost")
    public ResponseEntity<?> getMonthCost(HttpServletRequest request, @RequestBody RangeDateDto rangeDateDto) throws IOException, JAXBException {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<MonthCostDetailDto> monthRevenueDtos = paymentService.getMonthCost(request, rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), monthRevenueDtos));
    }

    @PostMapping("/getDateCost")
    public ResponseEntity<?> getDateCost(HttpServletRequest request, @RequestBody RangeDateDto rangeDateDto) throws IOException, JAXBException {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<DateCostDetailDto> dateRevenueDtos = paymentService.getDateCost(request, rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), dateRevenueDtos));
    }

    @PostMapping("/getYearCost")
    public ResponseEntity<?> getYearCost(HttpServletRequest request, @RequestBody RangeDateDto rangeDateDto) throws IOException, JAXBException {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<YearCostDetailDto> yearCostDtos = paymentService.getYearCost(request, rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), yearCostDtos));
    }

    private List<String> validateInsert(PaymentFullDto paymentFull) {
        List<String> result = new ArrayList<>();

        PaymentDto paymentDto = paymentService.getByCodeAndNumber(
                paymentFull.getCode(),
                paymentFull.getNumber(),
                DateUtils.year(paymentFull.getCreatedDate()),
                paymentFull.getAgency().getId()
        );

        if (paymentDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentFull.getCustomer() == null || paymentFull.getCustomer().getId() == null
            || paymentFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (paymentFull.getTransactionCustomer() == null || paymentFull.getTransactionCustomer().getId() == null
                || paymentFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (paymentFull.getPaymentDetails() == null || paymentFull.getPaymentDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateInsertPaymentCommission(PaymentFullDto paymentFull) {
        List<String> result = new ArrayList<>();

        PaymentDto paymentDto = paymentService.getByCodeAndNumber(
                paymentFull.getCode(),
                paymentFull.getNumber(),
                DateUtils.year(paymentFull.getCreatedDate()),
                paymentFull.getAgency().getId()
        );

        if (paymentDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentFull.getPaymentDetails() == null || paymentFull.getPaymentDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateUpdate(PaymentFullDto paymentFull) {
        List<String> result = new ArrayList<>();
        PaymentDto paymentDto = paymentService.getByCodeAndNumber(
                paymentFull.getCode(),
                paymentFull.getNumber(),
                DateUtils.year(paymentFull.getCreatedDate()),
                paymentFull.getAgency().getId()
        );

        if (paymentDto != null && !paymentDto.getId().equals(paymentFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentFull.getId() == null || paymentFull.getId().isEmpty()) {
            result.add("Thông tin phiếu chi không hợp lệ");
        }

        if (paymentFull.getCustomer() == null || paymentFull.getCustomer().getId() == null
                || paymentFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (paymentFull.getTransactionCustomer() == null || paymentFull.getTransactionCustomer().getId() == null
                || paymentFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (paymentFull.getPaymentDetails() == null || paymentFull.getPaymentDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateUpdatePaymentCommission(PaymentFullDto paymentFull) {
        List<String> result = new ArrayList<>();
        PaymentDto paymentDto = paymentService.getByCodeAndNumber(
                paymentFull.getCode(),
                paymentFull.getNumber(),
                DateUtils.year(paymentFull.getCreatedDate()),
                paymentFull.getAgency().getId()
        );

        if (paymentDto != null && !paymentDto.getId().equals(paymentFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (paymentFull.getId() == null || paymentFull.getId().isEmpty()) {
            result.add("Thông tin phiếu chi không hợp lệ");
        }

        if (paymentFull.getPaymentDetails() == null || paymentFull.getPaymentDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại phiếu chi này");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, PaymentFullDto paymentFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(paymentFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, paymentFullDto.getCreatedDate(), paymentFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, PaymentFullDto oldPaymentFullDto, PaymentFullDto paymentFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldPaymentFullDto.getCreatedDate());
        createdDate.setTime(paymentFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldPaymentFullDto.getCreatedDate(), oldPaymentFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldPaymentFullDto.getTransactionCustomer().getId().equals(paymentFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, paymentFullDto.getCreatedDate(), paymentFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, paymentFullDto.getCreatedDate(), paymentFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldPaymentFullDto.getTransactionCustomer().getId().equals(paymentFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldPaymentFullDto.getCreatedDate(), oldPaymentFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
