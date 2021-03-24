package com.antdigital.agency.core.models.warehouse;

import lombok.Data;

import java.util.Date;

@Data
public class TaxTableModel {
    private String id;
    private String companyId;
    private String code;
    private String name;
    private String note;
    private Integer percent;
    private AccountingTableModel accountingTable;
    private Date createdDate;
    private Date updatedDate;
}
