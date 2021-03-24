package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.ReceiptDetail;
import com.antdigital.agency.dtos.response.ReceiptDetailDto;
import com.antdigital.agency.dtos.response.ReceiptDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IReceiptDetailDtoMapper {
    IReceiptDetailDtoMapper INSTANCE = Mappers.getMapper( IReceiptDetailDtoMapper.class );

    ReceiptDetailDto toReceiptDetailDto(ReceiptDetail receiptDetail);

    ReceiptDetail toReceiptDetail(ReceiptDetailDto receiptDetailDto);

    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    ReceiptDetailFullDto toReceiptDetailFullDto(ReceiptDetail receiptDetail);

    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    ReceiptDetail toReceiptDetail(ReceiptDetailFullDto receiptDetailFullDto);

    List<ReceiptDetailDto> toReceiptDetailDtoList(List<ReceiptDetail> receiptDetails);

    List<ReceiptDetailFullDto> toReceiptDetailFullDtoList(List<ReceiptDetail> receiptDetails);
}
