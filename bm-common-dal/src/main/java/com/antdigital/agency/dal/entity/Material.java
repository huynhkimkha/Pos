package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "materials")
public class Material {
    @Id
    private String id;
    @Column
    private String name;
    @Column
    private String unit;
    @Column
    private Float price;
    @Column
    private String content;
}
