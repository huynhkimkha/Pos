package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BillFullDto {
    private String id;
    private EmployeesDto employee;
    private CustomersDto customer;
    private PromotionDto promotion;
    private AgencyDto agency;
    private String code;
    private String number;
    private String description;
    private String note;
    private Float amount;
    private Date createdDate;
    private Date updatedDate;
    private List<BillProductSizeDto> billProductSizeList;
}
