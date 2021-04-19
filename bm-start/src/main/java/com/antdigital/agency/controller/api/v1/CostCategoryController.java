package com.antdigital.agency.controller.api.v1;

import com.antdigital.agency.dtos.response.CostCategoryDto;
import com.antdigital.agency.dtos.response.ResponseDto;
import com.antdigital.agency.services.ICostCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cost-category")
public class CostCategoryController {
    @Autowired
    private ICostCategoryService costCategoryService;

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll() {
        List<CostCategoryDto> costCategoryList = costCategoryService.findAll();
        return ResponseEntity.ok(new ResponseDto(Arrays.asList("Danh mục chi phí"), HttpStatus.OK.value(), costCategoryList));
    }
}
