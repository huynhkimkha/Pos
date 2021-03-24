package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.core.models.warehouse.CustomerModel;
import com.antdigital.agency.dal.repository.ISettingsRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtClearingService;
import com.antdigital.agency.services.IDebtReportService;
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
@RequestMapping("/api/v1/debt-clearing")
public class DebtClearingController extends BaseController {
    @Autowired
    private IDebtClearingService debtClearingService;

    @Autowired
    IDebtReportService debtReportService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody DebtClearingSearchDto debtClearingSearchDto) throws IOException, JAXBException {
        DebtClearingSearchDto searchDto = debtClearingService.search(request, debtClearingSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu bù trừ"), HttpStatus.OK.value(), searchDto));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<DebtClearingDto> debtClearingDtos = debtClearingService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu bù trừ công nợ"), HttpStatus.OK.value(), debtClearingDtos));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<DebtClearingDto>> searchDto) {
        BaseSearchDto<List<DebtClearingDto>> search = debtClearingService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu bù trừ"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        DebtClearingFullDto debtClearingFull = debtClearingService.getFullById(request, id, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu bù trừ công nợ"), HttpStatus.OK.value(), debtClearingFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getOrderNumber(@PathVariable String createdDate) {
        String paymentNumber = debtClearingService.getDebtClearingNumber(createdDate, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu bù trừ công nợ"), HttpStatus.OK.value(), paymentNumber));
    }

    @PostMapping("/insert")
    @RolesAllowed("DEBT_CLEARING_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody DebtClearingFullDto debtClearingFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(this.getAgencyId());
        debtClearingFull.setAgency(agencyDto);
        List<String> msg = validateInsert(debtClearingFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        DebtClearingFullDto debtClearingFullDto = debtClearingService.insert(debtClearingFull, this.getAgencyId());

        //update monthly balance
        if (debtClearingFullDto != null){
            updateMonthlyBalance(request, debtClearingFullDto);
        }

        ResponseEntity<?> res = debtClearingFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu bù trừ công nợ thành công"), HttpStatus.OK.value(), debtClearingFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu bù trừ công nợ"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("DEBT_CLEARING_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody DebtClearingFullDto debtClearingFull) throws IOException, JAXBException {
        AgencyDto agencyDto = new AgencyDto();
        agencyDto.setId(this.getAgencyId());
        debtClearingFull.setAgency(agencyDto);
        List<String> msg = validateUpdate(debtClearingFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        DebtClearingFullDto oldDebtClearingDto = debtClearingService.getFullById(request, debtClearingFull.getId(), this.getAgencyId());
        DebtClearingFullDto debtClearingFullDto = debtClearingService.update(debtClearingFull, this.getAgencyId());

        //update monthly balance
        if (debtClearingFullDto != null){
            updateMonthlyBalance(request, oldDebtClearingDto, debtClearingFullDto);
        }

        ResponseEntity<?> res = debtClearingFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu bù trừ công nợ thành công"), HttpStatus.OK.value(), debtClearingFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu bù trừ công nợ"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @DeleteMapping("/delete/{debtClearingId}")
    @RolesAllowed("DEBT_CLEARING_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String debtClearingId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(debtClearingId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        DebtClearingFullDto debtClearingFullDto = debtClearingService.getFullById(request ,debtClearingId, this.getAgencyId());
        boolean result = debtClearingService.delete(debtClearingId, this.getAgencyId());

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, debtClearingFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu bù trừ công nợ thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu bù trừ công nợ"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInsert(DebtClearingFullDto debtClearingFull) {
        List<String> result = new ArrayList<>();

        return result;
    }

    private List<String> validateUpdate(DebtClearingFullDto debtClearingFull) {
        List<String> result = new ArrayList<>();
        DebtClearingDto debtClearingDto = debtClearingService.getByCodeAndNumber(
                debtClearingFull.getCode(),
                debtClearingFull.getNumber(),
                DateUtils.year(debtClearingFull.getCreatedDate()),
                this.getAgencyId()
        );

        if (debtClearingDto != null && !debtClearingDto.getId().equals(debtClearingDto.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại phiếu bù trừ này");
        }

        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, DebtClearingFullDto debtClearingFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(debtClearingFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            List<CustomerModel> customerList = new ArrayList<>();
            for(DebtClearingDetailFullDto debtClearingDetailFullDto: debtClearingFullDto.getDebtClearingDetails()){
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomer().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomer());
                };
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomerDebt().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomerDebt());
                };
            }
            for(CustomerModel customer: customerList){
                debtReportService.updateMonthlyClosingBalance(request, debtClearingFullDto.getCreatedDate(), customer, this.getAgencyId());
            }
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, DebtClearingFullDto oldDebtClearingDto, DebtClearingFullDto debtClearingFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldDebtClearingDto.getCreatedDate());
        createdDate.setTime(debtClearingFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            List<CustomerModel> customerList = new ArrayList<>();
            for(DebtClearingDetailFullDto debtClearingDetailFullDto: debtClearingFullDto.getDebtClearingDetails()){
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomer().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomer());
                };
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomerDebt().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomerDebt());
                };
            }
            for(DebtClearingDetailFullDto debtClearingDetailFullDto: oldDebtClearingDto.getDebtClearingDetails()){
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomer().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomer());
                };
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomerDebt().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomerDebt());
                };
            }
            for(CustomerModel customer: customerList){
                debtReportService.updateMonthlyClosingBalance(request, oldDebtClearingDto.getCreatedDate(), customer, this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            List<CustomerModel> customerList = new ArrayList<>();
            for(DebtClearingDetailFullDto debtClearingDetailFullDto: debtClearingFullDto.getDebtClearingDetails()){
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomer().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomer());
                };
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomerDebt().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomerDebt());
                };
            }
            for(DebtClearingDetailFullDto debtClearingDetailFullDto: oldDebtClearingDto.getDebtClearingDetails()){
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomer().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomer());
                };
                if(!customerList.stream().filter(item -> item.getId().equals(debtClearingDetailFullDto.getCustomerDebt().getId())).findFirst().isPresent()){
                    customerList.add(debtClearingDetailFullDto.getCustomerDebt());
                };
            }
            for(CustomerModel customer: customerList){
                debtReportService.updateMonthlyClosingBalance(request, debtClearingFullDto.getCreatedDate(), customer, this.getAgencyId());
            }
        }
    }
}
