package com.antdigital.agency.services;

import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.OrderSearchDto;
import com.antdigital.agency.dtos.response.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

public interface IOrderService {
    List<OrderDto> findAll(String agencyId);
    OrderDto getById(String id);
    String getOrderNumber(String createdDate, String agencyId);
    OrderFullDto getFullById(HttpServletRequest request, String orderId);
    OrderDto getByCodeAndNumber(String code, String number, int year, String agencyId);
    List<OrderDto> getNotCompleted(String code, String agencyId);
    OrderFullDto insert(HttpServletRequest request, OrderFullDto orderDto);
    OrderFullDto update(HttpServletRequest request, OrderFullDto orderDto);
    boolean delete(String orderId);
    OrderSearchDto search(HttpServletRequest request, OrderSearchDto orderSearchDto, String agencyId) throws JAXBException, IOException;
    List<MonthOrderDetailDto> getMonthOrder(RangeDateDto rangeDateDto, String agencyId);
    List<DateOrderDetailDto> getDateOrder(RangeDateDto rangeDateDto, String agencyId);
    List<YearOrderDetailDto> getYearOrder(RangeDateDto rangeDateDto, String agencyId);
}
