package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class ImportingMaterialDto {
    private String id;
    private AgencyDto agency;
    private CustomersDto customer;
    private String code;
    private String number;
    private String description;
    private String note;
    private Float amount;
    private Date createdDate;
    private Date updatedDate;
}
