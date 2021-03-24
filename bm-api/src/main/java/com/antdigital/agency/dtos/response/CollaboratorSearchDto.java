package com.antdigital.agency.dtos.response;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import lombok.Data;

import java.util.List;

@Data
public class CollaboratorSearchDto extends BaseSearchDto<List<CollaboratorDetailDto>> {
}
