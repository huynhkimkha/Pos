package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.dtos.response.SizeDto;
import com.antdigital.agency.services.ISizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/size")
public class SizeController {
    @Autowired
    private ISizeService sizeService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<SizeDto> sizeDtos = sizeService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Kích cỡ"), HttpStatus.OK.value(), sizeDtos));
    }
}
