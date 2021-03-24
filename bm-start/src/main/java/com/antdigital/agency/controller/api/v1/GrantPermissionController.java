package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.GrantPermissionDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.IGrantPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grant-permission")
public class GrantPermissionController extends BaseController {
    @Autowired
    IGrantPermissionService grantPermissionService;

    @GetMapping("/{roleId}")
    public ResponseEntity<?>getByRoleId(@PathVariable String roleId) {
    List<GrantPermissionDto> grantPermissionDtos = grantPermissionService.getByRoleId(roleId);
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải thành công các quyền đã cấp"), HttpStatus.OK.value(), grantPermissionDtos));
    }
}
