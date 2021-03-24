package com.antdigital.agency.dal.data;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import lombok.Data;

import java.util.Date;

@Data
public class OrderDetail {
    private String id;
    private String customerId;
    private String code;
    private String number;
    private String title;
    private ImportStatusEnum importStatus;
    private DeliveryStatusEnum deliverStatus;
    private Date createdDate;
    private String customerCode;
    private String customerName;
    private Double total;
    private Float totalQuantity;
}
