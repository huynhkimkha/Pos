package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.PaymentAdviceSearchDetail;
import com.antdigital.agency.dal.entity.PaymentAdvice;
import com.antdigital.agency.dtos.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPaymentAdviceDtoMapper {
    IPaymentAdviceDtoMapper INSTANCE = Mappers.getMapper( IPaymentAdviceDtoMapper.class );

    PaymentAdviceDto toPaymentAdviceDto(PaymentAdvice paymentAdvice);

    PaymentAdvice toPaymentAdvice(PaymentAdviceDto paymentAdviceDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    PaymentAdviceFullDto toPaymentAdviceFullDto(PaymentAdvice paymentAdvice);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    PaymentAdvice toPaymentAdvice(PaymentAdviceFullDto paymentAdviceFullDto);

    List<PaymentAdviceDto> toPaymentAdviceDtoList(List<PaymentAdvice> paymentAdvices);

    List<PaymentAdviceSearchDetailDto> toPaymentAdviceSearchDetailDtoList(List<PaymentAdviceSearchDetail> paymentAdviceSearchDetailList);
}
