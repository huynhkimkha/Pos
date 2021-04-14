package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.PromotionDto;
import com.antdigital.agency.dtos.response.PromotionFullDto;

import java.util.List;

public interface IPromotionService {
    List<PromotionDto> findAll();
    BaseSearchDto<List<PromotionDto>> findAll(BaseSearchDto<List<PromotionDto>> searchDto);
    PromotionFullDto getPromotionFull(String promotionId);
    PromotionFullDto insert(PromotionFullDto promotionDto);
    PromotionFullDto update(PromotionFullDto promotionDto);
    boolean delete(String id);
}
