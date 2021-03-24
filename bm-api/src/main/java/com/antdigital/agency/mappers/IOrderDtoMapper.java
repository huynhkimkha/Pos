package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.data.OrderDetail;
import com.antdigital.agency.dal.entity.Order;
import com.antdigital.agency.dtos.response.OrderDetailDto;
import com.antdigital.agency.dtos.response.OrderDto;
import com.antdigital.agency.dtos.response.OrderFullDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface IOrderDtoMapper {
    IOrderDtoMapper INSTANCE = Mappers.getMapper( IOrderDtoMapper.class );

    OrderDto toOrderDto(Order order);
    @Mapping(source = "customerId", target = "customer.id")
    OrderFullDto toOrderFullDto(Order order);

    Order toOrder(OrderDto orderDto);
    @Mapping(source = "customer.id", target = "customerId")
    Order toOrder(OrderFullDto orderFullDto);

    List<OrderDto> toOrderDtoList(List<Order> orderList);

    List<OrderDetailDto> toOrderDetailDtoList(List<OrderDetail> orderDetails);
}
