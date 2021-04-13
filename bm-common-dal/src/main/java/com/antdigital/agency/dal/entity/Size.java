package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "sizes")
public class Size {
    @Id
    private String id;
    @Column
    private String name;
}
