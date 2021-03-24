package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.AgencyDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IAgencyService;
import com.antdigital.agency.common.constant.Constant;
import com.antdigital.agency.core.service.IEzSoftwareRequestService;
import com.antdigital.agency.dal.repository.IAgencyRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agency")
public class AgencyController extends BaseController {
    @Autowired
    private IAgencyService agencyService;
    @Autowired
    private IEzSoftwareRequestService ezSoftwareRequestService;
    @Autowired
    private IAgencyRepository agencyRepository;

    @GetMapping("/getByCompanyId")
    public ResponseEntity<?> getByCompanyId(@RequestParam String companyId) {
        List<AgencyDto> agencyDtoList = agencyService.getByCompanyId(companyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), agencyDtoList));
    }

    @GetMapping("/findOne")
    public ResponseEntity<?> findOne(@RequestParam String agencyId) {
        AgencyDto agencyDto = agencyService.findOne(agencyId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh"), HttpStatus.OK.value(), agencyDto));
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<AgencyDto> agencyDtos = agencyService.findAll(this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách chi nhánh"), HttpStatus.OK.value(), agencyDtos));
    }

    @PostMapping("/find")
    public ResponseEntity<?> find(@RequestBody BaseSearchDto<List<AgencyDto>> searchDto) {
        BaseSearchDto<List<AgencyDto>> search = agencyService.findAll(searchDto, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh sách chi nhánh"), HttpStatus.OK.value(), search));
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<?> getById(@PathVariable String agencyId) {
        AgencyDto agencyDto = agencyService.getAgencyById(agencyId);
        ResponseEntity<?> res = agencyDto != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Agency"), HttpStatus.OK.value(), agencyDto))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Chi nhánh không tồn tại"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @GetMapping("/like-code-name/{name}")
    public ResponseEntity<?> getLikeCodeOrName(@PathVariable String name) {
        List<AgencyDto> agencyDtos = agencyService.getLikeCodeOrName(name, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Agency"), HttpStatus.OK.value(), agencyDtos));
    }

    @GetMapping("/findDifferentByOrgCodeLike")
    public ResponseEntity<?> findDifferentByOrgCodeLike(@RequestParam String orgCode, @RequestParam String agencyId) {
        if (orgCode.equals("null")) {
            orgCode = "";
        }
        List<AgencyDto> agencyDtos = agencyService.getDifferentByOrgCodeLike(orgCode, agencyId, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Agency"), HttpStatus.OK.value(), agencyDtos));
    }

    @PostMapping("/insert")
    @RolesAllowed("STORE_MANAGEMENT")
    public ResponseEntity<?> insert(HttpServletRequest request, @Valid @RequestBody AgencyDto agencyDto) throws IOException, JAXBException {
        agencyDto.setCompanyId(this.getCompanyId());
        List<String> errMessages = validateInserting(request, agencyDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        AgencyDto agencyDto1 = agencyService.insert(agencyDto);

        ResponseEntity<?> res = agencyDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Thêm chi nhánh thành công"), HttpStatus.OK.value(), agencyDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi thêm chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @PutMapping("/update")
    @RolesAllowed("STORE_MANAGEMENT")
    public ResponseEntity<?> update(@Valid @RequestBody AgencyDto agencyDto) {
        agencyDto.setCompanyId(this.getCompanyId());
        List<String> errMessages = validateUpdating(agencyDto);
        if(errMessages.size() > 0) {
            return ResponseEntity.ok(new ResponseDto(errMessages, HttpStatus.BAD_REQUEST.value(), ""));
        }
        AgencyDto agencyDto1 = agencyService.update(agencyDto);

        ResponseEntity<?> res = agencyDto1 != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật chi nhánh thành công"), HttpStatus.OK.value(), agencyDto1))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }

    @DeleteMapping("/delete")
    @RolesAllowed("STORE_MANAGEMENT")
    public ResponseEntity<?> delete(@RequestParam String id) {
        if(id.isEmpty()) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Không tồn tại chi nhánh này"), HttpStatus.BAD_REQUEST.value(), ""));
        }
        boolean result = agencyService.delete(id);
        ResponseEntity<?> res = result ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Xóa chi nhánh thành công"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi xóa chi nhánh"), HttpStatus.BAD_REQUEST.value(), ""));
        return res;
    }

    private List<String> validateInserting(HttpServletRequest request, AgencyDto agencyDto) throws IOException, JAXBException {
        List<String> result = new ArrayList<>();
        AgencyDto agencyDtoName = agencyService.getAgencyByExactName(agencyDto.getName(), agencyDto.getCompanyId());
        AgencyDto agencyDtoOrgcode = agencyService.getAgencyByOrgCode(agencyDto.getOrgCode(), agencyDto.getCompanyId());
        if (agencyDto.getAddress() == null) {
            result.add("Chưa nhập địa chỉ");
        }
        if (agencyDto.getName() == null) {
            result.add("Chưa nhập tên chi nhánh");
        }
        if (agencyDto.getOrgCode() == null) {
            result.add("Chưa nhập mã chi nhánh");
        }
        if (agencyDtoOrgcode != null) {
            result.add("Mã chi nhánh đã tồn tại");
        }
        if (agencyDtoName != null) {
            result.add("Tên chi nhánh đã tồn tại");
        }
        // Check brach limit
        int numberOfCurAgencies = agencyRepository.countAgencyByCompanyId(agencyDto.getCompanyId());
        if (ezSoftwareRequestService.reachBranchLimit(request, agencyDto.getCompanyId(), Constant.softId, numberOfCurAgencies)) {
            result.add("Đã đạt mức giới hạn chi nhánh");
        }

        return result;
    }

    private List<String> validateUpdating(AgencyDto agencyDto) {
        List<String> result = new ArrayList<>();
        AgencyDto agencyDtoName = agencyService.getAgencyByExactName(agencyDto.getName(), agencyDto.getCompanyId());
        AgencyDto agencyDtoOrgCode = agencyService.getAgencyByOrgCode(agencyDto.getOrgCode(), agencyDto.getCompanyId());
        if (agencyDto.getAddress() == null || agencyDto.getAddress().isEmpty()) {
            result.add("Chưa nhập địa chỉ");
        }
        if (agencyDto.getName() == null || agencyDto.getName().isEmpty()) {
            result.add("Chưa nhập tên chi nhánh");
        } else if (agencyDtoName != null && !agencyDtoName.getId().equals(agencyDto.getId())) {
            result.add("Tên chi nhánh đã tồn tại");
        }
        if (agencyDto.getOrgCode() == null || agencyDto.getOrgCode().isEmpty()) {
            result.add("Chưa nhập mã chi nhánh");
        } else if (agencyDtoOrgCode != null && !agencyDtoOrgCode.getId().equals(agencyDto.getId())) {
            result.add("Mã chi nhánh đã tồn tại");
        }

        return result;
    }
}
