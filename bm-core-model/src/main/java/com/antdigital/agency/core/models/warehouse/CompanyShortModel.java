package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CompanyShortModel {
    private String id;
    private String name;
    private String nameSlug;
    private String email;
    private String phone;
    private String address;
    private Date createdDate;
    private Date updatedDate;
}
