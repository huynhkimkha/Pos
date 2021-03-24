package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.DeliveryStatusEnum;
import com.antdigital.agency.common.enums.ImportStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column
    private String title;
    @Column(name = "customer_id")
    private String customerId;
    @Column
    private String code;
    @Column
    private String number;
    @Enumerated(EnumType.STRING)
    @Column(name="import_status")
    private ImportStatusEnum importStatus;
    @Enumerated(EnumType.STRING)
    @Column(name="deliver_status")
    private DeliveryStatusEnum deliverStatus;
    @Column(name = "created_date")
    private Date createdDate;
}
