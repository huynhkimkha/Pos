package com.antdigital.agency.dtos.response;

import lombok.Data;

import java.util.Date;

@Data
public class AgencyDto {
    private String id;
    private String companyId;
    private String name;
    private String address;
    private String orgCode;
    private String baseUrl;
    private Date createdDate;
    private Date updatedDate;
}
