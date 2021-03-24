package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ReceiptAdviceSearchDetail;
import com.antdigital.agency.dal.entity.ReceiptAdvice;
import com.antdigital.agency.dtos.response.ReceiptAdviceDto;
import com.antdigital.agency.dtos.response.ReceiptAdviceFullDto;
import com.antdigital.agency.dtos.response.ReceiptAdviceSearchDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IReceiptAdviceDtoMapper {
    IReceiptAdviceDtoMapper INSTANCE = Mappers.getMapper( IReceiptAdviceDtoMapper.class );

    ReceiptAdviceDto toReceiptAdviceDto(ReceiptAdvice receiptAdvice);

    ReceiptAdvice toReceiptAdvice(ReceiptAdviceDto receiptAdviceDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ReceiptAdviceFullDto toReceiptAdviceFullDto(ReceiptAdvice receiptAdvice);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    ReceiptAdvice toReceiptAdvice(ReceiptAdviceFullDto receiptAdviceFullDto);

    List<ReceiptAdviceDto> toReceiptAdviceDtoList(List<ReceiptAdvice> receiptAdvices);
    List<ReceiptAdviceSearchDetailDto> toReceiptAdviceSearchDetailDtoList(List<ReceiptAdviceSearchDetail> receiptAdviceSearchDetailList);
}
