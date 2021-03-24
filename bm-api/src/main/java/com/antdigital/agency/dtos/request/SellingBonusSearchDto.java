package com.antdigital.agency.dtos.request;

import com.antdigital.agency.dtos.response.SellingBonusDto;
import lombok.Data;

import java.util.List;

@Data
public class SellingBonusSearchDto extends BaseSearchDto<List<SellingBonusDto>>{
    private String refereeId;
}
