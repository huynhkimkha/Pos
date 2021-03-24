package com.antdigital.agency.biz.services.impl;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import com.antdigital.agency.common.utils.StringHelper;
import com.antdigital.agency.common.utils.UUIDHelper;
import com.antdigital.agency.core.models.warehouse.*;
import com.antdigital.agency.core.service.IWarehouseRequestService;
import com.antdigital.agency.dal.dao.IOrderDao;
import com.antdigital.agency.dal.data.*;
import com.antdigital.agency.dal.entity.*;
import com.antdigital.agency.dal.repository.*;
import com.antdigital.agency.dtos.request.BaseSearchDto;
import com.antdigital.agency.dtos.request.OrderSearchDto;
import com.antdigital.agency.dtos.response.*;
import com.antdigital.agency.mappers.*;
import com.antdigital.agency.services.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IOrderTransactionRepository orderTransactionRepository;

    @Autowired
    private IWarehouseRequestService warehouseRequestService;

    @Autowired
    private IImportingTransactionRepository importingTransactionRepository;

    @Autowired
    private IExportingTransactionRepository exportingTransactionRepository;

    @Autowired
    private IOrderDao orderDao;

    @Override
    public OrderSearchDto search(HttpServletRequest request, OrderSearchDto orderSearchDto, String agencyId)
            throws IOException, JAXBException{

        //Get customer id from warehouse service
        List<String> customerIds = new ArrayList<>();
        if(orderSearchDto.getCustomerCode() != null && !orderSearchDto.getCustomerCode().isEmpty()){
            List<CustomerModel> customerModelList = warehouseRequestService.getCustomersLikeCode(request, orderSearchDto.getCustomerCode());
            for(CustomerModel customerModel: customerModelList){
                customerIds.add(customerModel.getId());
            }
        }
        //Get customer address from warehouse service
        //Get merchandise id from warehouse service
        List<String> merchandiseIds = new ArrayList<>();
        if(orderSearchDto.getMerchandiseCode() != null && !orderSearchDto.getMerchandiseCode().isEmpty()){
            List<MerchandiseModel> merchandiseModelList = warehouseRequestService.getMerchandisesLikeCode(request, orderSearchDto.getMerchandiseCode());
            for(MerchandiseModel merchandiseModel: merchandiseModelList){
                merchandiseIds.add(merchandiseModel.getId());
            }
        }

        SearchResult<List<OrderDetail>> result = orderDao.search(
                orderSearchDto.getCode(),
                orderSearchDto.getNumber(),
                orderSearchDto.getTitle(),
                customerIds,
                merchandiseIds,
                orderSearchDto.getImportStatus(),
                orderSearchDto.getDeliverStatus(),
                orderSearchDto.getStartDate(),
                orderSearchDto.getEndDate(),
                orderSearchDto.getStartNumber(),
                orderSearchDto.getEndNumber(),
                orderSearchDto.getCreatedDateSort(),
                orderSearchDto.getCurrentPage(),
                orderSearchDto.getRecordOfPage(),
                agencyId);

        //get customer code and name
        customerIds = new ArrayList<>();
        for(OrderDetail orderDetail: result.getResult()){
            customerIds.add(orderDetail.getCustomerId());
        }
        List<CustomerModel> customerModelList = warehouseRequestService.getCustomers(request, customerIds);
        for(OrderDetail orderDetail: result.getResult()){
            CustomerModel customerModels = customerModelList.stream().filter(item -> item.getId().equals(orderDetail.getCustomerId())).findFirst().get();
            orderDetail.setCustomerCode(customerModels.getCode());
            orderDetail.setCustomerName(customerModels.getFullName());
        }

        orderSearchDto.setTotalRecords(result.getTotalRecords());
        orderSearchDto.setResult(IOrderDtoMapper.INSTANCE.toOrderDetailDtoList(result.getResult()));
        return orderSearchDto;
    }

    @Override
    public List<OrderDto> findAll(String agencyId) {
        List<Order> orders = orderRepository.findAllOrders(agencyId);
        return IOrderDtoMapper.INSTANCE.toOrderDtoList(orders);
    }

    @Override
    public OrderDto getById(String id) {
        Order order = orderRepository.findById(id).get();
        return IOrderDtoMapper.INSTANCE.toOrderDto(order);
    }

    @Override
    @Transactional
    public OrderFullDto insert(HttpServletRequest request, OrderFullDto orderDto) {
        try {
            Order newOrder = IOrderDtoMapper.INSTANCE.toOrder(orderDto);
            newOrder.setId(UUIDHelper.generateType4UUID().toString());
            newOrder = orderRepository.save(newOrder);
            orderDto.setId(newOrder.getId());

            CompletableFuture<Boolean> saveOrderTransaction = saveOrderTransaction(orderDto);

            CompletableFuture.allOf(
                    saveOrderTransaction
            );

            return orderDto;
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    @Transactional
    public OrderFullDto update(HttpServletRequest request, OrderFullDto orderDto) {
        try {
            Order order = IOrderDtoMapper.INSTANCE.toOrder(orderDto);
            orderRepository.save(order);

            CompletableFuture<Boolean> saveOrderTransaction = saveOrderTransaction(orderDto);

            CompletableFuture.allOf (
                    saveOrderTransaction
            );
            updateOrderStatus(orderDto.getId());
            return orderDto;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public boolean delete(String orderId) {
        try {
            List<OrderTransaction> orderTransactions = orderTransactionRepository.getByOrderId(orderId);
            for(OrderTransaction orderTransaction: orderTransactions) {
                orderTransactionRepository.deleteById(orderTransaction.getId());
            }
            orderRepository.deleteById(orderId);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return false;
        }
    }

    @Override
    public String getOrderNumber(String createdDate, String agencyId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Order result = orderRepository.getOrderNumber(sdf.parse(createdDate), agencyId);
            if (result == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(createdDate));
                int month = cal.get(Calendar.MONTH) + 1;
                String monthStr = month > 9 ? String.valueOf(month) : "0" + month ;
                return monthStr + "/0001";
            }
            String number = result.getNumber();
            return StringHelper.NumberOfCertificate(number);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public OrderFullDto getFullById(HttpServletRequest request, String orderId) {
        try {
            Order order = orderRepository.findById(orderId).get();
            List<OrderTransaction> transactions = orderTransactionRepository.getByOrderId(orderId);
            List<OrderTransactionFullDto> transactionFullDtos = IOrderTransactionDtoMapper.INSTANCE.toOrderTransactionDtoList(transactions);

            OrderFullDto orderFullDto = IOrderDtoMapper.INSTANCE.toOrderFullDto(order);
            orderFullDto.setOrderTransactions(transactionFullDtos);

            CompletableFuture<Boolean> getCustomers = getCustomers(request, orderFullDto);
            CompletableFuture<Boolean> getMerchandise = getMerchandise(request, orderFullDto);
            CompletableFuture<Boolean> getImportingTransaction = getImportingTransaction(orderFullDto);
            CompletableFuture<Boolean> getExportingTransaction = getExportingTransaction(orderFullDto);

            CompletableFuture.allOf (
                    getCustomers,
                    getMerchandise,
                    getImportingTransaction,
                    getExportingTransaction
            );

            return orderFullDto;

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
            return null;
        }
    }

    @Override
    public OrderDto getByCodeAndNumber(String code, String number, int year, String agencyId) {
        Order order = orderRepository.getByCodeAndNumber(code, number, year, agencyId);
        return IOrderDtoMapper.INSTANCE.toOrderDto(order);
    }

    @Override
    public List<OrderDto> getNotCompleted(String code, String agencyId) {
        List<Order> orders = orderRepository.getNotCompleted(code, agencyId);
        return IOrderDtoMapper.INSTANCE.toOrderDtoList(orders);
    }

    @Override
    public List<MonthOrderDetailDto> getMonthOrder(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<MonthOrderDetail> monthOrderDetails = orderDao.getMonthOrder(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromMonth = fromCal.get(Calendar.MONTH) + 1;
        Integer toMonth =  toCal.get(Calendar.MONTH) + 1;
        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear =  toCal.get(Calendar.YEAR);

        if (fromYear < toYear) {
            toMonth = 12;
        }
        List<MonthOrderDetail> monthOrderDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            if (fromYear - toYear == 0){
                toMonth =  toCal.get(Calendar.MONTH) + 1;
            }
            while(fromMonth <= toMonth){
                MonthOrderDetail monthOrderDetail = new MonthOrderDetail();
                monthOrderDetail.setMonthDate(fromMonth);
                monthOrderDetail.setYearDate(fromYear);
                monthOrderDetail.setTotal(0);
                monthOrderDetailList.add(monthOrderDetail);
                fromMonth += 1;
            }
            fromMonth = 1;
            fromYear += 1;
        }
        for(MonthOrderDetail monthOrderDetail: monthOrderDetails){
            List<MonthOrderDetail> detailLst = monthOrderDetailList.stream().filter(item -> item.getMonthDate() == monthOrderDetail.getMonthDate()).collect(Collectors.toList());
            for (MonthOrderDetail temp : detailLst) {
                temp.setTotal(monthOrderDetail.getTotal());
            }
        }
        List<MonthOrderDetailDto> monthOrderDetailDtos = IMonthOrderDetailDtoMapper.INSTANCE.toMonthOrderDtoList(monthOrderDetailList);

        return monthOrderDetailDtos;
    }

    @Override
    public List<DateOrderDetailDto> getDateOrder(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<DateOrderDetail> dateOrderDetails = orderDao.getDateOrder(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromDate = fromCal.get(Calendar.DAY_OF_MONTH);
        Integer toDate =  toCal.get(Calendar.DAY_OF_MONTH);
        Integer month = fromCal.get(Calendar.MONTH) + 1;
        Integer year = fromCal.get(Calendar.YEAR);

        List<DateOrderDetail> dateOrderDetailList = new ArrayList<>();
        while(fromDate <= toDate){
            DateOrderDetail dateOrderDetail = new DateOrderDetail();
            dateOrderDetail.setDate(fromDate);
            dateOrderDetail.setMonth(month);
            dateOrderDetail.setYear(year);
            dateOrderDetail.setTotal(0);
            dateOrderDetailList.add(dateOrderDetail);
            fromDate += 1;
        }

        for(DateOrderDetail dateOrderDetail: dateOrderDetails){
            List<DateOrderDetail> detailLst = dateOrderDetailList.stream().filter(item -> item.getDate() == dateOrderDetail.getDate()).collect(Collectors.toList());
            for (DateOrderDetail temp : detailLst) {
                temp.setTotal(dateOrderDetail.getTotal());
            }
        }
        List<DateOrderDetailDto> dateOrderDetailDtos = IDateOrderDetailDtoMapper.INSTANCE.toDateOrderDtoList(dateOrderDetailList);

        return dateOrderDetailDtos;
    }

    @Override
    public List<YearOrderDetailDto> getYearOrder(RangeDateDto rangeDateDto, String agencyId) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateNew = format1.format(rangeDateDto.getFromDate());
        String toDateNew = format1.format(rangeDateDto.getToDate());
        List<YearOrderDetail> yearOrderDetails = orderDao.getYearOrder(fromDateNew, toDateNew, agencyId);
        Calendar fromCal = Calendar.getInstance();
        Calendar toCal = Calendar.getInstance();

        fromCal.setTime(new Date(rangeDateDto.getFromDate()));
        toCal.setTime(new Date(rangeDateDto.getToDate()));

        Integer fromYear = fromCal.get(Calendar.YEAR);
        Integer toYear = toCal.get(Calendar.YEAR);

        List<YearOrderDetail> yearOrderDetailList = new ArrayList<>();
        while(fromYear <= toYear){
            YearOrderDetail yearOrderDetail = new YearOrderDetail();
            yearOrderDetail.setYear(fromYear);
            yearOrderDetail.setTotal(0);
            yearOrderDetailList.add(yearOrderDetail);
            fromYear += 1;
        }

        for(YearOrderDetail yearOrderDetail: yearOrderDetails) {
            List<YearOrderDetail> detailLst = yearOrderDetailList.stream().filter(item -> item.getYear() == yearOrderDetail.getYear()).collect(Collectors.toList());
            for (YearOrderDetail temp : detailLst) {
                temp.setTotal(yearOrderDetail.getTotal());
            }
        }
        List<YearOrderDetailDto> yearOrderDetailDtos = IYearOrderDetailDtoMapper.INSTANCE.toYearOrderDtoList(yearOrderDetailList);

        return yearOrderDetailDtos;
    }

    @Async
    CompletableFuture<Boolean> getCustomers(HttpServletRequest request, OrderFullDto orderFullDto)
            throws IOException, JAXBException {
        List<String> ids = new ArrayList<>();
        ids.add(orderFullDto.getCustomer().getId());

        List<CustomerModel> customerModels = warehouseRequestService.getCustomers(request, ids);
        for (CustomerModel customer: customerModels) {
            if (orderFullDto.getCustomer() != null && orderFullDto.getCustomer().getId() != null) {
                if (orderFullDto.getCustomer().getId().equals(customer.getId())) {
                    orderFullDto.setCustomer(customer);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getMerchandise(HttpServletRequest request, OrderFullDto orderFullDto)
            throws IOException, JAXBException {
        List<OrderTransactionFullDto> transactionFulls = orderFullDto.getOrderTransactions();
        List<String> ids = new ArrayList<>();
        for(OrderTransactionFullDto item : transactionFulls) {
            ids.add(item.getMerchandise().getId());
        }

        MerchandiseModel[] merchandiseModels = warehouseRequestService.getMerchandises(request, ids);
        for(MerchandiseModel merchandiseModel : merchandiseModels) {
            for(OrderTransactionFullDto item : transactionFulls) {
                if(merchandiseModel.getId().equals(item.getMerchandise().getId())) {
                    item.setMerchandise(merchandiseModel);
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getImportingTransaction(OrderFullDto orderFullDto) {
        List<ImportingTransaction> transactions = importingTransactionRepository.getByOrderId(orderFullDto.getId());
        List<ImportingTransactionDto> transactionDtos = IImportingTransactionDtoMapper.INSTANCE.toImportTransactionDtoList(transactions);
        orderFullDto.setImportingTransactions(transactionDtos);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> getExportingTransaction(OrderFullDto orderFullDto) {
        List<ExportingTransaction> transactions = exportingTransactionRepository.getByOrderId(orderFullDto.getId());
        List<ExportingTransactionDto> transactionDtos = IExportingTransactionDtoMapper.INSTANCE.toExportingTransactionDtoList(transactions);
        orderFullDto.setExportingTransactions(transactionDtos);

        return CompletableFuture.completedFuture(true);
    }

    @Async
    CompletableFuture<Boolean> saveOrderTransaction(OrderFullDto orderFullDto) {
        List<OrderTransaction> oldTransactions = orderTransactionRepository.getByOrderId(orderFullDto.getId());
        List<OrderTransactionFullDto> orderTransactionFulls = orderFullDto.getOrderTransactions();
        Order order = IOrderDtoMapper.INSTANCE.toOrder(orderFullDto);

        for (OrderTransactionFullDto item : orderTransactionFulls) {
            OrderTransaction orderTransaction = IOrderTransactionDtoMapper.INSTANCE.toOrderTransaction(item);
            if (item.getId() == null || item.getId().isEmpty()) {
                orderTransaction.setId(UUIDHelper.generateType4UUID().toString());
            }
            orderTransaction.setOrder(order);
            OrderTransaction created = orderTransactionRepository.save(orderTransaction);
            item.setId(created.getId());
        }

        if (oldTransactions != null) {
            for (OrderTransaction item : oldTransactions) {
                int index = orderTransactionFulls.stream().map(t -> t.getId()).collect(Collectors.toList()).indexOf(item.getId());
                if (index == -1) {
                    orderTransactionRepository.deleteById(item.getId());
                }
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    void updateOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId).get();
        Float orderTotal = orderTransactionRepository.getTotalQuantityByOrderId(orderId);
        Float exportTotal = exportingTransactionRepository.getTotalQuantityByOrderId(orderId);
        Float importTotal = importingTransactionRepository.getTotalQuantityByOrderId(orderId);
        exportTotal = exportTotal != null ? exportTotal : 0;
        importTotal = importTotal != null ? importTotal : 0;

        ImportStatusEnum importStatusEnum = importTotal < orderTotal ? ImportStatusEnum.UNCOMPLETED : ImportStatusEnum.COMPLETED;
        DeliveryStatusEnum deliveryStatusEnum = exportTotal < orderTotal ? DeliveryStatusEnum.UNCOMPLETED : DeliveryStatusEnum.COMPLETED;

        if (order.getImportStatus() != importStatusEnum || order.getDeliverStatus() != deliveryStatusEnum) {
            order.setImportStatus(importStatusEnum);
            order.setDeliverStatus(deliveryStatusEnum);
            orderRepository.save(order);
        }
    }
}
