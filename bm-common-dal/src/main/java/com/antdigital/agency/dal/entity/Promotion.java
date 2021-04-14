package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.StatusPromotionEnum;
import com.antdigital.agency.common.enums.TypePromotionEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private Float amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type_promotion")
    private TypePromotionEnum typePromotion;
    @Enumerated(EnumType.STRING)
    @Column
    private StatusPromotionEnum status;
    @Column(name = "expired_date")
    private Date expiredDate;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
