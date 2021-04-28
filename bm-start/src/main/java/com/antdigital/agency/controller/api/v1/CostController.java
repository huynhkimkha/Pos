package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.ICostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cost")
public class CostController extends BaseController{
    @Autowired
    private ICostService costService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<CostDto> costs = costService.findAll(this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), costs));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<CostDto>> searchDto) {
        BaseSearchDto<List<CostDto>> search = costService.findAll(searchDto, this.getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String id) {
        CostDto cost = costService.getCostById(id);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), cost));
    }

    @GetMapping("/get-number/{createdDate}")
    public ResponseEntity<?> getNumber(@PathVariable String createdDate) {
        String number = costService.getNumber(createdDate, getAgencyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Phiếu nhập"), HttpStatus.OK.value(), number));
    }


    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody CostDto costDto) {
        if(costDto.getAgency() == null || costDto.getAgency().getId() == null || costDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            costDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateInserting(costDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        CostDto costDtoDetail = costService.insert(costDto);

        ResponseEntity<?> res = costDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Lưu thông tin chi phí thành công"), HttpStatus.OK.value(), costDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi lưu thông tin chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody CostDto costDto) {
        if(costDto.getAgency() == null || costDto.getAgency().getId() == null || costDto.getAgency().getId().isEmpty()){
            AgencyDto agencyDto = new AgencyDto();
            agencyDto.setId(this.getAgencyId());
            costDto.setAgency(agencyDto);
        }
        List<String> errMessages = validateUpdating(costDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        CostDto costDtoDetail = costService.update(costDto);
        ResponseEntity<?> res = costDtoDetail != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật thông tin chi phí thành công"), HttpStatus.OK.value(), costDtoDetail))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật thông tin chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestParam String id) {
        List<String> errMessages = validateDeleting(id);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }

        boolean result = costService.deleteCost(id);

        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa thông tin chi phí"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    private List<String> validateUpdating(CostDto costDto){
        List<String> result = new ArrayList<>();
        if (costDto == null || costDto.getId() == null || costDto.getId().isEmpty()) {
            result.add("Thông tin không hợp lệ");
        }

        return result;
    }

    private List<String> validateInserting(CostDto costDto) {
        List<String> result = new ArrayList<>();
        return result;
    }

    private List<String> validateDeleting(String id) {
        List<String> result = new ArrayList<>();
        if(id == null || id.isEmpty()) {
            result.add("Không tồn tại khách hàng này");
        }
        // TODO check all agency services. allow deleting if it wasn't used in any agency service.

        return result;
    }

    @PostMapping("/getDateCost")
    public ResponseEntity<?> getDateCost(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<DateCostDetailDto> dateCostDetailDtos = costService.getDateCost(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), dateCostDetailDtos));
    }

    @PostMapping("/getMonthCost")
    public ResponseEntity<?> getMonthCost(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<MonthCostDetailDto> monthCostDetailDtos = costService.getMonthCost(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), monthCostDetailDtos));
    }

    @PostMapping("/getYearCost")
    public ResponseEntity<?> getYearCost(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<YearCostDetailDto> yearCostDtos = costService.getYearCost(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), yearCostDtos));
    }

    @PostMapping("/getCostStatistic")
    public ResponseEntity<?> getCostStatistic(@RequestBody RangeDateDto rangeDateDto) {
        if(rangeDateDto.getFromDate() > rangeDateDto.getToDate()){
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Ngày bắt đầu phải nhỏ hơn ngày kết thúc"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<CostDto> costDto = costService.getCostStatistic(rangeDateDto, getAgencyId());

        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi phí"), HttpStatus.OK.value(), costDto));
    }

}
