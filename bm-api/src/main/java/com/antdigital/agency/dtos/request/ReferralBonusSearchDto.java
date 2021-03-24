package com.antdigital.agency.dtos.request;

import com.antdigital.agency.dtos.response.ReferralBonusDto;
import lombok.Data;

import java.util.List;

@Data
public class ReferralBonusSearchDto extends BaseSearchDto<List<ReferralBonusDto>>{
    private String refereeId;
}
