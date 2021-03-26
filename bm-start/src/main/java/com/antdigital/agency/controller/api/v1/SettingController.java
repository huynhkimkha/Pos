package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.dtos.response.SettingsDto;
import com.antdigital.agency.services.ISettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/setting")
public class SettingController extends BaseController {

    @Autowired
    private ISettingService settingService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<SettingsDto> settingsDtos = settingService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Tải cài đặt thành công!"), HttpStatus.OK.value(), settingsDtos));
    }

    @PutMapping("/update")
    @RolesAllowed("STORE_SETTING")
    public ResponseEntity<?> update(@Valid @RequestBody List<SettingsDto> settingsDtos) {
        if(settingsDtos.size() == 0) {
            return ResponseEntity.ok(new ResponseDto(Arrays.asList("Cài đặt rỗng"), HttpStatus.BAD_REQUEST.value(), ""));
        }

        List<SettingsDto> result = settingService.update(settingsDtos);
        ResponseEntity<?> res = result != null ? ResponseEntity.ok(new ResponseDto(Arrays.asList("Cập nhật cài đặt thành công!"), HttpStatus.OK.value(), result))
                : ResponseEntity.ok(new ResponseDto(Arrays.asList("Lỗi cập nhật cài đặt"), HttpStatus.BAD_REQUEST.value(), ""));

        return res;
    }
}
