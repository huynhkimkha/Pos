package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.PaymentStatusEnum;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.LicenseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IDebtClearingService;
import com.antdigital.agency.services.IDebtReportService;
import com.antdigital.agency.services.IExportingWarehouseService;
import com.antdigital.agency.services.IReceiptService;
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
@RequestMapping("/api/v1/exporting-warehouse")
public class ExportingWarehouseController extends BaseController {
    @Autowired
    IExportingWarehouseService exportingWarehouseService;

    @Autowired
    IReceiptService receiptService;

    @Autowired
    IReceiptAdviceService receiptAdviceService;

    @Autowired
    IImportingReturnService importingReturnService;

    @Autowired
    IDebtClearingService debtClearingService;

    @Autowired
    IDebtReportService debtReportService;

    @Autowired
    IAgencyService agencyService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody ExportingWarehouseSearchDto exportingWarehouseSearchDto)
            throws IOException, JAXBException {
        ExportingWarehouseSearchDto search = exportingWarehouseService.search(request, exportingWarehouseSearchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<ExportingWarehouseDto> exportingWarehouses = exportingWarehouseService.findAll(getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouses));
    }

    @PostMapping("/findAll")
    public ResponseEntity<?> findAll(@RequestBody BaseSearchDto<List<ExportingWarehouseDto>> searchDto) {
        BaseSearchDto<List<ExportingWarehouseDto>> search = exportingWarehouseService.findAll(searchDto, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ExportingWarehouseDto exportingWarehouse = exportingWarehouseService.getById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouse));
    }

    @GetMapping("/full/{id}")
    public ResponseEntity<?> getFullById(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        ExportingWarehouseFullDto exportingWarehouseFull = exportingWarehouseService.getFullById(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouseFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String number = exportingWarehouseService.getNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), number));
    }

    @GetMapping("/payment-not-complete")
    public ResponseEntity<?> getPaymentNotCompleted( @RequestParam String customerId, @RequestParam String code) {
        List<ExportingWarehouseDto> exportingWarehouses = exportingWarehouseService.getPaymentNotCompleted(customerId, code, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouses));
    }

    @GetMapping("/get-like-code")
    public ResponseEntity<?> getLikeCode(@RequestParam String code) {
        List<ExportingWarehouseDto> exportingWarehouses = exportingWarehouseService.getLikeCode(code, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouses));
    }

    @PostMapping("/get-customer-base-on-spent")
    public ResponseEntity<?> getCustomerBaseOnSpent(HttpServletRequest request, @RequestBody RangeDateDto rangeDateDto) throws IOException, JAXBException{
        String agencyId = getAgencyId();
        List<CustomerStatisticDto> customerStatistics = exportingWarehouseService.getCustomerBaseOnSpent(request, rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách khách hàng"), HttpStatus.OK.value(), customerStatistics));
    }

    @PostMapping("/get-merchandise-best-sold")
    public ResponseEntity<?> getMerchandiseBestSold(HttpServletRequest request, @RequestBody RangeDateDto rangeDateDto) throws IOException, JAXBException{
        String agencyId = getAgencyId();
        List<MerchandiseStatisticDto> merchandiseStatistics = exportingWarehouseService.getMerchandiseBestSold(request, rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách hàng hóa"), HttpStatus.OK.value(), merchandiseStatistics));
    }

    @GetMapping("/payment-not-complete-code")
    public ResponseEntity<?> getPaymentNotCompletedCode(@RequestParam String code) {
        List<ExportingWarehouseDto> exportingWarehouses = exportingWarehouseService.getPaymentNotCompletedCode(code, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouses));
    }

    @GetMapping("/total/{id}")
    public ResponseEntity<?> getNotCompleted( @PathVariable String id) {
        Double result = exportingWarehouseService.getTotal(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/getTransactionByOrderId/{id}")
    public ResponseEntity<?> getTransactionByOrderId(HttpServletRequest request, @PathVariable String id) throws IOException, JAXBException {
        List<ExportingTransactionFullDto> exportingWarehouseDtos = exportingWarehouseService.GetByOrderId(request, id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouseDtos));
    }

    @GetMapping("/getDebt/{customerId}")
    public ResponseEntity<?> getCustomerDebt( @PathVariable String customerId) {
        Double result = exportingWarehouseService.getDebt(customerId, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Nợ khách hàng cần thu"), HttpStatus.OK.value(), result));
    }

    @GetMapping("/getForReceipt")
    public ResponseEntity<?> findAll(@RequestParam String customerId, @RequestParam Double amount) {
        List<ExportingWarehouseFullDto> exportingWarehouses = exportingWarehouseService.getForReceipt(customerId, amount, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), exportingWarehouses));
    }

    @GetMapping("/getForImportingReturn")
    public ResponseEntity<?> getForImportingReturn(@RequestParam String customerId, @RequestParam String merchandiseId, @RequestParam Float quantity) {
        List<ExportingTransactionDto> exportingTransactions = exportingWarehouseService.getExportingTransactionForReturn(customerId, merchandiseId, quantity, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), exportingTransactions));
    }

    @PostMapping("/revenue-report")
    public ResponseEntity<?> revenueReport(HttpServletRequest request, @RequestBody LicenseSearchDto licenseSearchDto) throws IOException, JAXBException {
        licenseSearchDto.setAgencyId(this.getAgencyId());
        licenseSearchDto.setCompanyId(this.getCompanyId());
        List<ExportingWarehouseDetailDto> search = exportingWarehouseService.revenueReport(request, licenseSearchDto);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu xuất"), HttpStatus.OK.value(), search));
    }

    @PostMapping("/insert")
    @RolesAllowed("EXPORTING_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody ExportingWarehouseFullDto exportingWarehouseFull) throws IOException, JAXBException {
        exportingWarehouseFull.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        List<String> msg = validateInsert(exportingWarehouseFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingWarehouseFullDto exportingWarehouseFullDto = exportingWarehouseService.insert(request, exportingWarehouseFull, getCompanyId());

        //update monthly balance
        if (exportingWarehouseFullDto != null){
            updateMonthlyBalance(request, exportingWarehouseFullDto);
        }

        ResponseEntity<?> res = exportingWarehouseFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm phiếu xuất bán thành công"), HttpStatus.OK.value(), exportingWarehouseFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu xuất bán"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("EXPORTING_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody ExportingWarehouseFullDto exportingWarehouseFull) throws IOException, JAXBException {
        if (exportingWarehouseFull.getAgency() == null || exportingWarehouseFull.getAgency().getId() == null || exportingWarehouseFull.getAgency().getId().isEmpty()){
            exportingWarehouseFull.setAgency(agencyService.getAgencyById(this.getAgencyId()));
        }
        List<String> msg = validateUpdate(exportingWarehouseFull);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingWarehouseFullDto oldExportingWarehouseDto = exportingWarehouseService.getFullById(request, exportingWarehouseFull.getId());
        ExportingWarehouseFullDto exportingWarehouseFullDto = exportingWarehouseService.update(request, exportingWarehouseFull, getCompanyId());

        //update monthly balance
        if (exportingWarehouseFullDto != null){
            updateMonthlyBalance(request, oldExportingWarehouseDto, exportingWarehouseFullDto);
        }

        ResponseEntity<?> res = exportingWarehouseFullDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật phiếu xuất bán thành công"), HttpStatus.OK.value(), exportingWarehouseFullDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu xuất bán"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{exportingId}")
    @RolesAllowed("EXPORTING_MANAGEMENT")
    public ResponseEntity<?> delete(HttpServletRequest request, @PathVariable String exportingId) throws IOException, JAXBException {
        List<String> msg = validateDeleting(exportingId);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }

        ExportingWarehouseFullDto exportingWarehouseFullDto = exportingWarehouseService.getFullById(request ,exportingId);
        boolean result = exportingWarehouseService.delete(request, exportingId);

        //update monthly balance
        if (result == true){
            updateMonthlyBalance(request, exportingWarehouseFullDto);
        }

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu xuất bán thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu xuất bán"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @PostMapping("/getMonthRevenue")
    public ResponseEntity<?> getMonthRevenue(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<MonthRevenueDetailDto> monthRevenueDtos = exportingWarehouseService.getMonthRevenue(rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Doanh thu"), HttpStatus.OK.value(), monthRevenueDtos));
    }

    @PostMapping("/getDateRevenue")
    public ResponseEntity<?> getDateRevenue(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<DateRevenueDetailDto> dateRevenueDtos = exportingWarehouseService.getDateRevenue(rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Doanh thu"), HttpStatus.OK.value(), dateRevenueDtos));
    }

    @PostMapping("/getYearRevenue")
    public ResponseEntity<?> getYearRevenue(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        String agencyId = getAgencyId();
        List<YearRevenueDetailDto> yearRevenueDtos = exportingWarehouseService.getYearRevenue(rangeDateDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Doanh thu"), HttpStatus.OK.value(), yearRevenueDtos));
    }

    private List<String> validateInsert(ExportingWarehouseFullDto exportingWarehouseFull) {
        List<String> result = new ArrayList<>();
        ExportingWarehouseDto exportingWarehouseDto = exportingWarehouseService.getByCodeAndNumber(
                exportingWarehouseFull.getCode(),
                exportingWarehouseFull.getNumber(),
                DateUtils.year(exportingWarehouseFull.getCreatedDate()),
                exportingWarehouseFull.getAgency().getId()
        );
        if (exportingWarehouseDto != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (exportingWarehouseFull.getTransactionCustomer() == null || exportingWarehouseFull.getTransactionCustomer().getId() == null
                || exportingWarehouseFull.getTransactionCustomer().getId().isEmpty()) {
            result.add("Chưa nhập khách hàng giao dịch");
        }

        if (exportingWarehouseFull.getExportTransactionFulls() == null || exportingWarehouseFull.getExportTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateUpdate(ExportingWarehouseFullDto exportingWarehouseFull) {
        List<String> result = new ArrayList<>();

        ExportingWarehouseDto exportingWarehouseDto = exportingWarehouseService.getById(exportingWarehouseFull.getId());
        ExportingWarehouseDto exportingWarehouse = exportingWarehouseService.getByCodeAndNumber(
                exportingWarehouseFull.getCode(),
                exportingWarehouseFull.getNumber(),
                DateUtils.year(exportingWarehouseFull.getCreatedDate()),
                exportingWarehouseFull.getAgency().getId()
        );

        if (exportingWarehouse != null && !exportingWarehouse.getId().equals(exportingWarehouseFull.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (exportingWarehouseDto.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            result.add("Không thể cập nhật phiếu đã hoàn thành thanh toán");
        }

        if (exportingWarehouseFull.getId() == null || exportingWarehouseFull.getId().isEmpty()) {
            result.add("Phiếu xuất bán không hợp lệ");
        }

        if (exportingWarehouseFull.getExportTransactionFulls() == null || exportingWarehouseFull.getExportTransactionFulls().size() == 0) {
            result.add("Không có danh sách hàng hóa");
        }

        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();

        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại phiếu xuất bán này");
        }

        ExportingWarehouseDto exportingWarehouseDto = exportingWarehouseService.getById(id);
        if (exportingWarehouseDto.getPaymentStatus() == PaymentStatusEnum.COMPLETED) {
            result.add("Không thể xóa phiếu đã hoàn thành thanh toán");
        }

        int countReceipt = receiptService.countByExportId(id);
        if (countReceipt > 0) {
            result.add("Không thể xóa phiếu đã có thanh toán tiền mặt");
            return result;
        }

        int countReceiptAdvice = receiptAdviceService.countByExportId(id);
        if (countReceiptAdvice > 0) {
            result.add("Không thể xóa phiếu đã có thanh toán qua ngân hàng");
            return result;
        }

        int countImportingReturn = importingReturnService.countByExportId(id);
        if (countImportingReturn > 0) {
            result.add("Không thể xóa phiếu đã có trả hàng");
            return result;
        }

        int countDebt = debtClearingService.countByExportId(id);
        if (countDebt > 0) {
            result.add("Không thể xóa phiếu đã tạo phiếu bù trừ");
            return result;
        }


        return result;
    }

    private void updateMonthlyBalance(HttpServletRequest request, ExportingWarehouseFullDto exportingWarehouseFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        createdDate.setTime(exportingWarehouseFullDto.getCreatedDate());
        if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, exportingWarehouseFullDto.getCreatedDate(), exportingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
        }
    }

    private void updateMonthlyBalance(HttpServletRequest request, ExportingWarehouseFullDto oldExportingWarehouseDto, ExportingWarehouseFullDto exportingWarehouseFullDto) throws IOException, JAXBException {
        Calendar today = Calendar.getInstance();
        Calendar createdDate = Calendar.getInstance();
        Calendar oldCreatedDate = Calendar.getInstance();
        oldCreatedDate.setTime(oldExportingWarehouseDto.getCreatedDate());
        createdDate.setTime(exportingWarehouseFullDto.getCreatedDate());
        if(oldCreatedDate.before(today) && oldCreatedDate.before(createdDate) &&
                ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, oldExportingWarehouseDto.getCreatedDate(), oldExportingWarehouseDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldExportingWarehouseDto.getTransactionCustomer().getId().equals(exportingWarehouseFullDto.getTransactionCustomer().getId()) &&
                    createdDate.before(today) &&
                    ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, exportingWarehouseFullDto.getCreatedDate(), exportingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
            }
        }else if(createdDate.before(today) &&
                ((createdDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (createdDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
            debtReportService.updateMonthlyClosingBalance(request, exportingWarehouseFullDto.getCreatedDate(), exportingWarehouseFullDto.getTransactionCustomer(), this.getAgencyId());
            if(!oldExportingWarehouseDto.getTransactionCustomer().getId().equals(exportingWarehouseFullDto.getTransactionCustomer().getId()) &&
                    oldCreatedDate.before(today) &&
                    ((oldCreatedDate.get(Calendar.MONTH)-today.get(Calendar.MONTH) != 0) || (oldCreatedDate.get(Calendar.YEAR)-today.get(Calendar.YEAR) != 0))){
                debtReportService.updateMonthlyClosingBalance(request, oldExportingWarehouseDto.getCreatedDate(), oldExportingWarehouseDto.getTransactionCustomer(), this.getAgencyId());
            }
        }
    }
}
