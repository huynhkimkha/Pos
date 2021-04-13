package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.ProductStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    private String id;
    @Column
    private String name;
    @Column(name = "name_slug")
    private String nameSlug;
    @Column(name = "images")
    private String image;
    @Column(name = "content")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column
    private ProductStatusEnum status;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
