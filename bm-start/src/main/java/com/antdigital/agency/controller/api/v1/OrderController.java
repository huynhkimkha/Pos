package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.dtos.request.OrderSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IExportingWarehouseService;
import com.antdigital.agency.services.IImportingWarehouseService;
import com.antdigital.agency.services.IOrderService;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController extends BaseController {
    @Autowired
    private IOrderService orderService;

    @Autowired
    private IImportingWarehouseService importingWarehouseService;

    @Autowired
    private IExportingWarehouseService exportingWarehouseService;

    @PostMapping("/search")
    public ResponseEntity<?> search(HttpServletRequest request, @Valid @RequestBody OrderSearchDto orderSearchDto)
            throws IOException, JAXBException {
        String agencyId = getAgencyId();
        OrderSearchDto search = orderService.search(request, orderSearchDto, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu đặt hàng"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        String agencyId = getAgencyId();
        List<OrderDto> orderDtos = orderService.findAll(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu đặt hàng"), HttpStatus.OK.value(), orderDtos));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getById(HttpServletRequest request, @PathVariable String orderId) {
        OrderFullDto orderFull = orderService.getFullById(request, orderId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu đặt hàng"), HttpStatus.OK.value(), orderFull));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getOrderNumber(@PathVariable String createdDate) {
        String agencyId = getAgencyId();
        String orderNumber = orderService.getOrderNumber(createdDate, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu đặt hàng"), HttpStatus.OK.value(), orderNumber));
    }

    @GetMapping("/not-complete")
    public ResponseEntity<?> getNotCompleted(@RequestParam String code) {
        String agencyId = getAgencyId();
        List<OrderDto> orders = orderService.getNotCompleted(code, agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu đặt hàng"), HttpStatus.OK.value(), orders));
    }

    @PostMapping("/insert")
    @RolesAllowed("ORDER_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody OrderFullDto orderFullDto) {
        List<String> msg = validateInsert(orderFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        orderFullDto.setAgency(new AgencyDto());
        orderFullDto.getAgency().setId(getAgencyId());
        OrderFullDto orderFullNewDto = orderService.insert(request, orderFullDto);

        ResponseEntity<?> res = orderFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm thành công"), HttpStatus.OK.value(), orderFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm phiếu xuất bán"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("ORDER_MANAGEMENT")
    public ResponseEntity<?> update(HttpServletRequest request, @Valid @RequestBody OrderFullDto orderFullDto) {
        List<String> msg = validateUpdate(orderFullDto);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        orderFullDto.setAgency(new AgencyDto());
        orderFullDto.getAgency().setId(getAgencyId());
        OrderFullDto orderFullNewDto = orderService.update(request, orderFullDto);

        ResponseEntity<?> res = orderFullNewDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thành công"), HttpStatus.OK.value(), orderFullNewDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật phiếu đặt hàng"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete/{id}")
    @RolesAllowed("ORDER_MANAGEMENT")
    public ResponseEntity<?> delete(@PathVariable String id) {
        List<String> msg = validateDelete(id);
        if(msg.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(msg, HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = orderService.delete(id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa phiếu đặt hàng thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa phiếu đặt hàng"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    @PostMapping("/getMonthOrder")
    public ResponseEntity<?> getMonthOrder(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<MonthOrderDetailDto> monthOrderDetailDtos = orderService.getMonthOrder(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Đặt hàng"), HttpStatus.OK.value(), monthOrderDetailDtos));
    }

    @PostMapping("/getDateOrder")
    public ResponseEntity<?> getDateOrder(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<DateOrderDetailDto> dateOrderDetailDtos = orderService.getDateOrder(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Đặt hàng"), HttpStatus.OK.value(), dateOrderDetailDtos));
    }

    @PostMapping("/getYearOrder")
    public ResponseEntity<?> getYearOrder(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<YearOrderDetailDto> yearOrderDtos = orderService.getYearOrder(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Đặt hàng"), HttpStatus.OK.value(), yearOrderDtos));
    }

    private List<String> validateInsert(OrderFullDto orderDto) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        OrderDto order = orderService.getByCodeAndNumber(
                orderDto.getCode(),
                orderDto.getNumber(),
                DateUtils.year(orderDto.getCreatedDate()),
                agencyId
        );

        if (order != null) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (orderDto == null) {
            result.add("Dữ liệu không hợp lệ");
        }

        if(orderDto.getCustomer() == null || orderDto.getCustomer().getId() == null) {
            result.add("Thiếu thông tin khách hàng");
        }

        if(orderDto.getOrderTransactions() == null || orderDto.getOrderTransactions().size() == 0) {
            result.add("Phải có ít nhất 1 sản phẩm");
        }

        return result;
    }

    private List<String> validateUpdate(OrderFullDto orderDto) {
        List<String> result = new ArrayList<>();
        String agencyId = getAgencyId();
        OrderDto order = orderService.getByCodeAndNumber(
                orderDto.getCode(),
                orderDto.getNumber(),
                DateUtils.year(orderDto.getCreatedDate()),
                agencyId
        );

        if (order != null && !order.getId().equals(orderDto.getId())) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }

        if (orderDto == null || orderDto.getId() == null) {
            result.add("Dữ liệu không hợp lệ");
        }

        if(orderDto.getCustomer() == null || orderDto.getCustomer().getId() == null) {
            result.add("Thiếu thông tin khách hàng");
        }

        if(orderDto.getOrderTransactions() == null || orderDto.getOrderTransactions().size() == 0) {
            result.add("Phải có ít nhất 1 sản phẩm");
        }

        return result;
    }

    private List<String> validateDelete(String id) {
        List<String> result = new ArrayList<>();

        if (id == null || id.isEmpty()) {
            result.add("Mã chứng từ và số chứng từ đã tồn tại");
        }
        OrderDto orderDto = orderService.getById(id);
        if (orderDto.getDeliverStatus() == DeliveryStatusEnum.COMPLETED) {
            result.add("Không cho phép xóa đặt hàng đã hoàn thành giao hàng");
        }
        if (orderDto.getImportStatus() == ImportStatusEnum.COMPLETED) {
            result.add("Không cho phép xóa đặt hàng đã hoàn thành nhập mua hàng");
        }
        if (importingWarehouseService.countByOrder(id) > 0) {
            result.add("Không cho phép xóa đặt hàng đã có nhập mua hàng");
        }
        if (exportingWarehouseService.countByOrder(id) > 0) {
            result.add("Không cho phép xóa đặt hàng đã có xuất bán hàng");
        }

        return result;
    }
}
