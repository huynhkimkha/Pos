package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.core.models.warehouse.CompanyShortModel;
import com.antdigital.agency.core.service.IEzSoftwareRequestService;
import com.antdigital.agency.dtos.request.OrderSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.services.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/software")
public class EzSoftwareController extends BaseController {

    @Autowired
    private IEzSoftwareRequestService ezSoftwareRequestService;

    @GetMapping("/company/getByNameSlug")
    public ResponseEntity<?> getCompany(HttpServletRequest request, @RequestParam String nameSlug) throws IOException, JAXBException {
        CompanyShortModel company = ezSoftwareRequestService.getByNameSlug(request, nameSlug);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Lấy thông tin công ty thành công"), HttpStatus.OK.value(), company));
    }

    @GetMapping("/company/getLikeNameOrSlugName")
    public ResponseEntity<?> getLikeNameOrSlugName(HttpServletRequest request, @RequestParam String name) throws IOException, JAXBException {
        List<CompanyShortModel> companyShortDtos = ezSoftwareRequestService.getLikeNameOrSlugName(request, name);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Lấy thông tin công ty thành công"), HttpStatus.OK.value(), companyShortDtos));
    }

    @GetMapping("/company-license/validateLicense/{companyId}/{softwareId}")
    public ResponseEntity<?> validateLicense(HttpServletRequest request, @PathVariable String companyId, @PathVariable String softwareId) throws IOException, JAXBException {
        if (companyId.isBlank() || companyId.length() != 36) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Yêu cầu không hợp lệ!"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        Boolean result = ezSoftwareRequestService.validateLicense(request, companyId, softwareId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Lấy thông tin thành công!"), HttpStatus.OK.value(), result));
    }
}
