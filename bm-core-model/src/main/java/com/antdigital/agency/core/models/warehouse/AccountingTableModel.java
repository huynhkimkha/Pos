package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

import java.util.Date;

@Data
public class AccountingTableModel {
    private String id;
    private String companyId;
    private String code;
    private Integer level;
    private String name;
    private String note;
    private Date createdDate;
    private Date updatedDate;
}
