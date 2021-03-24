package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.OrderTransaction;
import com.antdigital.agency.dtos.response.OrderTransactionDto;
import com.antdigital.agency.dtos.response.OrderTransactionFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IOrderTransactionDtoMapper {
    IOrderTransactionDtoMapper INSTANCE = Mappers.getMapper( IOrderTransactionDtoMapper.class );

    OrderTransactionDto toOrderTransactionDto(OrderTransaction orderTransaction);

    OrderTransaction toOrderTransaction(OrderTransactionDto orderTransactionDto);

    @Mapping(source = "merchandiseId", target = "merchandise.id")
    OrderTransactionFullDto toOrderTransactionFullDto(OrderTransaction orderTransaction);

    @Mapping(source = "merchandise.id", target = "merchandiseId")
    OrderTransaction toOrderTransaction(OrderTransactionFullDto orderTransactionFullDto);

    List<OrderTransactionFullDto> toOrderTransactionDtoList(List<OrderTransaction> orderTransactionList);
}
