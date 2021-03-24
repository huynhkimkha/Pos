package com.antdigital.agency.dtos.response;

import com.antdigital.agency.core.models.warehouse.CustomerModel;

import lombok.Data;

@Data
public class CustomerStatisticDto{
    private CustomerModel customer;
    private Double totalSpent;
}
