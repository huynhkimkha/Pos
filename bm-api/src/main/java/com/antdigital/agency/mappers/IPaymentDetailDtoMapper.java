package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.PaymentDetail;
import com.antdigital.agency.dtos.response.PaymentDetailDto;
import com.antdigital.agency.dtos.response.PaymentDetailFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPaymentDetailDtoMapper {
    IPaymentDetailDtoMapper INSTANCE = Mappers.getMapper( IPaymentDetailDtoMapper.class );

    PaymentDetailDto toPaymentDetailDto(PaymentDetail paymentDetail);

    PaymentDetail toPaymentDetail(PaymentDetailDto paymentDetailDto);

    @Mapping(source = "creditAccount", target = "creditAccount.id")
    @Mapping(source = "debitAccount", target = "debitAccount.id")
    PaymentDetailFullDto toPaymentDetailFullDto(PaymentDetail paymentDetail);

    @Mapping(source = "creditAccount.id", target = "creditAccount")
    @Mapping(source = "debitAccount.id", target = "debitAccount")
    PaymentDetail toPaymentDetail(PaymentDetailFullDto paymentDetailFullDto);

    List<PaymentDetailDto> toPaymentDetailDtoList(List<PaymentDetail> paymentDetails);

    List<PaymentDetailFullDto> toPaymentDetailFullDtoList(List<PaymentDetail> paymentDetails);
}
