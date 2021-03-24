package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.DebtReportSearchDto;
import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IGeneralJournalReportService;
import com.antdigital.agency.services.IDebtReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController extends BaseController {

    @Autowired
    private IDebtReportService debtReportService;

    @Autowired
    private IGeneralJournalReportService generalJournalReportService;

    @PostMapping("/debt-report")
    @RolesAllowed("ROLE_REPORT_MANAGEMENT")
    public ResponseEntity<?> debtReport(HttpServletRequest request, @Valid @RequestBody DebtReportSearchDto debtReportSearchDto) throws IOException, JAXBException {
        debtReportSearchDto.setAgencyId(this.getAgencyId());
        debtReportSearchDto.setCompanyId(this.getCompanyId());
        List<DebtReportDto> debtReportDtos = debtReportService.getDebtReports(request, debtReportSearchDto, false);
        ResponseEntity<?> res = debtReportDtos != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Báo cáo công nợ thành công"), HttpStatus.OK.value(), debtReportDtos))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi báo cáo"), HttpStatus.BAD_REQUEST.value(), debtReportDtos));

        return res;
    }

    @PostMapping("/get-debt")
    public ResponseEntity<?> getDebt(HttpServletRequest request, @Valid @RequestBody DebtReportSearchDto debtReportSearchDto) throws IOException, JAXBException {
        debtReportSearchDto.setAgencyId(this.getAgencyId());
        debtReportSearchDto.setCompanyId(this.getCompanyId());
        List<MonthlyClosingBalanceDto> monthlyClosingBalanceDtos = debtReportService.getDebt(request, debtReportSearchDto);
        ResponseEntity<?> res = monthlyClosingBalanceDtos != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lấy công nợ thành công"), HttpStatus.OK.value(), monthlyClosingBalanceDtos))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lấy công nợ không thành công"), HttpStatus.BAD_REQUEST.value(), monthlyClosingBalanceDtos));
        return res;
    }

    @PostMapping("/general-journal-report")
    @RolesAllowed("ROLE_REPORT_MANAGEMENT")
    public ResponseEntity<?> generalJournalReport(HttpServletRequest request, @Valid @RequestBody LicenseSearchDto licenseSearchDto) throws IOException, JAXBException {
        licenseSearchDto.setAgencyId(this.getAgencyId());
        licenseSearchDto.setCompanyId(this.getCompanyId());
        List<GeneralJournalReportDto> generalJournalReportDtos = generalJournalReportService.getOneGeneralJournalReport(request, licenseSearchDto);
        ResponseEntity<?> res = generalJournalReportDtos != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Báo cáo Sổ nhật ký chung thành công"), HttpStatus.OK.value(), generalJournalReportDtos))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi báo cáo"), HttpStatus.BAD_REQUEST.value(), generalJournalReportDtos));

        return res;
    }

    @PostMapping("/calculate-monthly-debt")
    @RolesAllowed("ROLE_REPORT_MANAGEMENT")
    public ResponseEntity<?> calculateMonthlyClosingBalance(HttpServletRequest request, @RequestParam Date fromDate, @RequestParam Date toDate) throws IOException, JAXBException, ParseException {
        debtReportService.deleteMonthlyClosingBalance(toDate, this.getAgencyId());
        debtReportService.saveMonthlyClosingBalance(request, fromDate, toDate, this.getAgencyId());
        ResponseEntity<?> res = ResponseEntity.ok(new ResponseDto(Arrays.asList("Tạo dư nợ hàng tháng thành công"), HttpStatus.OK.value(), null));
        return res;
    }
}
