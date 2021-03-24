package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

import java.util.Date;

@Data
public class ProductGroupModel {
    private String id;
    private String companyId;
    private String name;
    private Date createdDate;
    private Date updatedDate;
}
