package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ReceiptAdvice;
import com.antdigital.agency.dal.entity.ReceiptAdviceDetail;
import com.antdigital.agency.dal.entity.ReceiptDetail;
import com.antdigital.agency.dtos.response.ReceiptAdviceDetailDto;
import com.antdigital.agency.dtos.response.ReceiptAdviceDetailFullDto;
import com.antdigital.agency.dtos.response.ReceiptDetailDto;
import com.antdigital.agency.dtos.response.ReceiptDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IReceiptAdviceDetailDtoMapper {
    IReceiptAdviceDetailDtoMapper INSTANCE = Mappers.getMapper( IReceiptAdviceDetailDtoMapper.class );

    ReceiptAdviceDetailDto toReceiptDetailDto(ReceiptAdviceDetail receiptAdviceDetail);

    ReceiptAdviceDetail toReceiptAdviceDetail(ReceiptAdviceDetailDto receiptAdviceDetailDto);

    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    ReceiptAdviceDetailFullDto toReceiptAdviceDetailFullDto(ReceiptAdviceDetail receiptAdviceDetail);

    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    ReceiptAdviceDetail toReceiptAdviceDetail(ReceiptAdviceDetailFullDto receiptAdviceDetailFullDto);

    List<ReceiptAdviceDetailDto> toReceiptAdviceDetailDtoList(List<ReceiptAdviceDetail> receiptAdviceDetails);

    List<ReceiptAdviceDetailFullDto> toReceiptAdviceDetailFullDtoList(List<ReceiptAdviceDetail> receiptAdviceDetails);
}
