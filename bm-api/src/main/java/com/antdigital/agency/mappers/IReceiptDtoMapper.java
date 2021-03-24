package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.ReceiptSearchDetail;
import com.antdigital.agency.dal.entity.Receipt;
import com.antdigital.agency.dtos.response.ReceiptDto;
import com.antdigital.agency.dtos.response.ReceiptFullDto;
import com.antdigital.agency.dtos.response.ReceiptSearchDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IReceiptDtoMapper {
    IReceiptDtoMapper INSTANCE = Mappers.getMapper( IReceiptDtoMapper.class );

    ReceiptDto toReceiptDto(Receipt receipt);

    Receipt toReceipt(ReceiptDto receiptDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "transactionCustomerId", target = "transactionCustomer.id")
    ReceiptFullDto toReceiptFullDto(Receipt receipt);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "transactionCustomer.id", target = "transactionCustomerId")
    Receipt toReceipt(ReceiptFullDto receiptFullDto);

    List<ReceiptDto> toReceiptDtoList(List<Receipt> receipts);
    List<ReceiptSearchDetailDto> toReceiptSearchDetailDtoList(List<ReceiptSearchDetail> receiptSearchDetailList);
}
