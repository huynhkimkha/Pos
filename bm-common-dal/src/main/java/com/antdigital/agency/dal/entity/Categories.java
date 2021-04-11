package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.CategoryStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "categories")
public class Categories {
    @Id
    private String id;
    @Column
    private String name;
    @Enumerated(EnumType.STRING)
    @Column
    private CategoryStatusEnum status;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
