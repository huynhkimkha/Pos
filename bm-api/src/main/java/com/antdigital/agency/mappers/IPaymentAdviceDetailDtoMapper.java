package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.PaymentAdvice;
import com.antdigital.agency.dal.entity.PaymentAdviceDetail;
import com.antdigital.agency.dal.entity.PaymentDetail;
import com.antdigital.agency.dtos.response.PaymentAdviceDetailDto;
import com.antdigital.agency.dtos.response.PaymentAdviceDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface IPaymentAdviceDetailDtoMapper {
    IPaymentAdviceDetailDtoMapper INSTANCE = Mappers.getMapper( IPaymentAdviceDetailDtoMapper.class );

    PaymentAdviceDetailDto toPaymentAdviceDetailDto(PaymentAdviceDetail paymentAdviceDetail);

    PaymentAdviceDetail toPaymentAdviceDetail(PaymentAdviceDetailDto paymentAdviceDetailDto);

    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    PaymentAdviceDetailFullDto toPaymentAdviceDetailFullDto(PaymentAdviceDetail paymentAdviceDetail);

    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    PaymentAdviceDetail toPaymentAdviceDetail(PaymentAdviceDetailFullDto paymentAdviceDetailFullDto);

    List<PaymentAdviceDetailDto> toPaymentAdviceDetailDtoList(List<PaymentAdviceDetail> paymentAdviceDetails);

    List<PaymentAdviceDetailFullDto> toPaymentAdviceDetailFullDtoList(List<PaymentAdviceDetail> paymentAdviceDetails);
}
