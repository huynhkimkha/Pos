package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.PermissionDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/permission")
public class PermissionController extends BaseController {
    @Autowired
    IPermissionService permissionService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<PermissionDto>permissionDtos = permissionService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải quyền thành công!"), HttpStatus.OK.value(), permissionDtos));
    }

    @GetMapping("/getAllOnLicense")
    @RolesAllowed({"ROLE_ROLES_MANAGEMENT", "ROLE_COMPANY"})
    public ResponseEntity<?> getAllOnLicense() {
        List<PermissionDto> permissionDtos = permissionService.getAllOnLicense(this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Permission.loadData.Success.message"), HttpStatus.OK.value(), permissionDtos));
    }

    @GetMapping("/getAllByLicense")
    @RolesAllowed({"ROLE_ROLES_MANAGEMENT", "ROLE_COMPANY"})
    public ResponseEntity<?> getAllByLicense(HttpServletRequest request) throws IOException, JAXBException {
        List<PermissionDto> permissionDtos = permissionService.getAllByLicense(request, this.getCompanyId());
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải quyền thành công"), HttpStatus.OK.value(), permissionDtos));
    }
}
