package com.antdigital.agency.dal.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "settings")
public class Settings {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String label;
    @Column(name = "\"key\"")
    private String key;
    @Column(name = "\"value\"")
    private String values;
}
