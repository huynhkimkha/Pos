package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dal.entity.Agency;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("/api/v1/receipt")
public class ReceiptController extends BaseController {
    @Autowired
    IReceiptService receiptService;

    @Autowired
    IDebtReportService debtReportService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ReceiptSearchDto receiptSearchDto)
            throws IOException, JAXBException {
        ReceiptSearchDto search = receiptService.search(request, receiptSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ReceiptDto> receipts = receiptService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receipts));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<ReceiptDto>> searchDto) {
        BaseSearchDto<List<ReceiptDto>> search = receiptService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ReceiptDto receipt = receiptService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receipt));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ReceiptFullDto receiptFull = receiptService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), receiptFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getOrderNumber(@PathVariable String createdDate) {
        String paymentNumber = receiptService.getReceiptNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu thu"), HttpStatus.OK.value(), paymentNumber));
    }

    @PostMapping("/insert")
    @RolesAllowed("RECEIPT_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ReceiptFullDto receiptFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(getAgencyId());
        receiptFull.setAgency(agencyDto);
        List<String> msg = validateInsert(receiptFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptFullDto receiptFullDto = receiptService.insert(receiptFull);

        //update monthly balance
        if (receiptFullDto != null){
            updateMonthlyBalance(request, receiptFullDto);
        }

        ResponseEntity<?> res = receiptFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu thu thành công"), HttpStatus.OK.value(), receiptFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("RECEIPT_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ReceiptFullDto receiptFull) throws IOException, JAXBException {
        if(receiptFull.getAgency() == null || receiptFull.getAgency().getId() == null || receiptFull.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(getAgencyId());
            receiptFull.setAgency(agencyDto);
        }
        List<String> msg = validateUpdate(receiptFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptFullDto oldReceiptDto = receiptService.getFullById(request, receiptFull.getId());
        ReceiptFullDto receiptFullDto = receiptService.update(receiptFull);

        //update monthly balance
        if (receiptFullDto != null){
            updateMonthlyBalance(request, oldReceiptDto, receiptFullDto);
        }

        ResponseEntity<?> res = receiptFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu thu thành công"), HttpStatus.OK.value(), receiptFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{receiptId}")
    @RolesAllowed("RECEIPT_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String receiptId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(receiptId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ReceiptFullDto receiptFullDto = receiptService.getFullById(request ,receiptId);
        boolean result = receiptService.delete(receiptId);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, receiptFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu thu thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu thu"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(ReceiptFullDto receiptFull) {
        List<String> result = new ArrayList<>();
        ReceiptDto receiptDto = receiptService.getByCodeAndNumber(
                receiptFull.getCode(),
                receiptFull.getNumber(),
                DateUtils.year(receiptFull.getCreatedDate()),
                receiptFull.getAgency().getId()
        );

        if (receiptDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (receiptFull.getCustomer() == null || receiptFull.getCustomer().getId() == null
                || receiptFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (receiptFull.getTransactionCustomer() == null || receiptFull.getTransactionCustomer().getId() == null
                || receiptFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (receiptFull.getReceiptDetails() == null || receiptFull.getReceiptDetails().size() == 0) {
            result.add("Không có chi tiết thanh toán");
        }

        return result;
    }

    private List<String> validateUpdate(ReceiptFullDto receiptFull) {
        List<String> result = new ArrayList<>();
        ReceiptDto receiptDto = receiptService.getByCodeAndNumber(
                receiptFull.getCode(),
                receiptFull.getNumber(),
                DateUtils.year(receiptFull.getCreatedDate()),
                receiptFull.getAgency().getId()
        );

        if (receiptDto != null && !receiptDto.getId().equals(receiptFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (receiptFull.getId() == null || receiptFull.getId().isEmpty()) {
            result.add("Thông tin phiếu thu không hợp lệ");
        }

        if (receiptFull.getCustomer() == null || receiptFull.getCustomer().getId() == null
                || receiptFull.getCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng");
        }

        if (receiptFull.getTransactionCustomer() == null || receiptFull.getTransactionCustomer().getId() == null
                || receiptFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Thiếu thông tin khách hàng giao dịch");
        }

        if (receiptFull.getReceiptDetails() == null || receiptFull.getReceiptDetails().size() == 0) {
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

    private void updateMonthlyBalance(HttpServletRequest request, ReceiptFullDto receiptFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(receiptFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, receiptFullDto.getCreatedDate(), receiptFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ReceiptFullDto oldReceiptDto, ReceiptFullDto receiptFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldReceiptDto.getCreatedDate());
        createdDate.setTime(receiptFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldReceiptDto.getCreatedDate(), oldReceiptDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldReceiptDto.getTransactionCustomer().getId().equals(receiptFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, receiptFullDto.getCreatedDate(), receiptFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, receiptFullDto.getCreatedDate(), receiptFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldReceiptDto.getTransactionCustomer().getId().equals(receiptFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldReceiptDto.getCreatedDate(), oldReceiptDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
