package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.IPromotionProductRepository;
import com.antdigital.agency.dal.repository.IPromotionRepository;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IPromotionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements IPromotionService {
    private static final Logger logger = LoggerFactory.getLogger(PromotionServiceImpl.class);

    @Autowired
    private IPromotionProductRepository promotionProductRepository;

    @Autowired
    private IPromotionRepository promotionRepository;

    @Override
    public List<PromotionDto> findAll() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> promotionList = promotionRepository.findAllPromotion(dtf.format(now));
        return IPromotionDtoMapper.INSTANCE.toPromotionDtoList(promotionList);
    }

    @Override
    public BaseSearchDto<List<PromotionDto>> findAll(BaseSearchDto<List<PromotionDto>> searchDto) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if(searchDto == null || searchDto.getCurrentPage() == -1 || searchDto.getRecordOfPage() == 0) {
            searchDto.setResult(this.findAll());
            return searchDto;
        }

        Sort sort = null;
        if(searchDto.getSortBy() != null && !searchDto.getSortBy().isEmpty()) {
            sort = searchDto.isSortAsc() ? Sort.by(Sort.Direction.ASC, searchDto.getSortBy()) : Sort.by(Sort.Direction.DESC, searchDto.getSortBy());
        }
        PageRequest request = sort == null ? PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage())
                : PageRequest.of(searchDto.getCurrentPage(), searchDto.getRecordOfPage(), sort);

        Page<Promotion> page = promotionRepository.findAllPage(request, dtf.format(now));
        searchDto.setTotalRecords(page.getTotalElements());
        searchDto.setResult(IPromotionDtoMapper.INSTANCE.toPromotionDtoList(page.getContent()));

        return searchDto;
    }

    @Override
    public PromotionFullDto getPromotionFull(String promotionId) {
        try {
            Promotion promotion = promotionRepository.findById(promotionId).get();
            List<PromotionProduct> details = promotionProductRepository.getByPromotionId(promotion.getId());
            PromotionFullDto promotionFullDto = IPromotionDtoMapper.INSTANCE.toPromotionFullDto(promotion);
            List<PromotionProductDto> detailDto = IPromotionProductDtoMapper.INSTANCE.toPromotionProductDtoList(details);
            promotionFullDto.setPromotionProductList(detailDto);
            return promotionFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public PromotionFullDto insert(PromotionFullDto promotionFullDto) {
        try {
            Promotion promotion = IPromotionDtoMapper.INSTANCE.toPromotion(promotionFullDto);
            promotion.setId(UUIDHelper.generateType4UUID().toString());
            promotion = promotionRepository.save(promotion);

            for(PromotionProductDto detail : promotionFullDto.getPromotionProductList()) {
                if (detail.getProduct() == null || detail.getProduct().getId() == null
                        || detail.getProduct().getId().isEmpty()) {
                    continue;
                }

                PromotionProduct tempDetail = IPromotionProductDtoMapper.INSTANCE.toPromotionProduct(detail);
                tempDetail.setId(UUIDHelper.generateType4UUID().toString());
                tempDetail.setPromotion(promotion);

                promotionProductRepository.save(tempDetail);
            }

            promotionFullDto.setId(promotion.getId());
            return promotionFullDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }


    @Override
    @Transactional
    public PromotionFullDto update(PromotionFullDto promotionDto) {
        try {
            Promotion old = promotionRepository.findById(promotionDto.getId()).get();
            Promotion promotion = IPromotionDtoMapper.INSTANCE.toPromotion(promotionDto);

            promotionRepository.save(promotion);
            // collect detail was removed.
            List<PromotionProduct> promotionProductList = promotionProductRepository.getByPromotionId(promotionDto.getId());
            List<String> detailDelete = new ArrayList<>();
            for(PromotionProduct item : promotionProductList) {
                if (item.getId() == null || item.getId().isEmpty()) {
                    continue;
                }

                int index = promotionDto.getPromotionProductList().stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                int isExist = detailDelete.indexOf(item.getId());
                if (index == -1 && isExist == -1) {
                    detailDelete.add(item.getId());
                }
            }
            for(String id : detailDelete) {
                promotionProductRepository.deleteById(id);
            }

            for(PromotionProductDto promotionProductDto : promotionDto.getPromotionProductList()) {
                if (promotionProductDto.getId() == null || promotionProductDto.getId().isEmpty()) {
                    PromotionDto promotionDto1 = new PromotionDto();
                    promotionDto1.setId(promotionDto.getId());

                    promotionProductDto.setId(UUIDHelper.generateType4UUID().toString());
                    promotionProductDto.setPromotion(promotionDto1);
                }
                PromotionProduct promotionProduct = IPromotionProductDtoMapper.INSTANCE.toPromotionProduct(promotionProductDto);
                promotionProduct = promotionProductRepository.save(promotionProduct);
                promotionProductDto = IPromotionProductDtoMapper.INSTANCE.toPromotionProductDto(promotionProduct);
            }

            return promotionDto;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean delete(String id) {
        try {
            PromotionFullDto promotionFullDto = this.getPromotionFull(id);
            for(PromotionProductDto detailDto : promotionFullDto.getPromotionProductList()) {
                promotionProductRepository.deleteById(detailDto.getId());
            }
            promotionRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }
}
