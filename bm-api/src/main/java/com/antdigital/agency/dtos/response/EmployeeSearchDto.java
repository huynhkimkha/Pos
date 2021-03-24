package com.antdigital.agency.dtos.response;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeSearchDto extends BaseSearchDto<List<EmployeeDetailDto>> {
}
