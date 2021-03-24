package com.antdigital.agency.core.models.warehouse;

import lombok.Data;
import java.util.Date;

@Data
public class SaleRankModel {
    private String id;
    private String companyId;
    private String name;
    private float commissionRate;
    private double milestoneRevenue;
    private Date createdDate;
    private Date updatedDate;
}
