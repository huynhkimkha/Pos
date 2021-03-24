package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.PaymentSearchDetail;
import com.antdigital.agency.dal.entity.Payment;
import com.antdigital.agency.dtos.response.PaymentDto;
import com.antdigital.agency.dtos.response.PaymentFullDto;
import com.antdigital.agency.dtos.response.PaymentSearchDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IPaymentDtoMapper {
    IPaymentDtoMapper INSTANCE = Mappers.getMapper( IPaymentDtoMapper.class );

    PaymentDto toPaymentDto(Payment payment);

    Payment toPayment(PaymentDto paymentDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    PaymentFullDto toPaymentFullDto(Payment payment);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    Payment toPayment(PaymentFullDto paymentFullDto);

    List<PaymentDto> toPaymentDtoList(List<Payment> payments);

    List<PaymentSearchDetailDto> toPaymentSearchDetailDtoList(List<PaymentSearchDetail> paymentSearchDetailList);
}
