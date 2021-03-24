package com.antdigital.agency.dal.entity;

import com.antdigital.agency.common.enums.ActivatedStatusEnum;
import com.antdigital.agency.common.enums.BlockStatusEnum;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "collaborators")
public class Collaborator {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "agency_id")
    private Agency agency;
    @Column(name="full_name")
    private String fullName;
    @Column
    private String address;
    @Column
    private String district;
    @Column
    private String province;
    @Column(name="birth_date")
    private Date birthDate;
    @Column
    private String phone;
    @Column
    private String email;
    @Column
    private String password;
    @Column(name = "sale_rank_id")
    private String saleRankId;
    @Enumerated(EnumType.STRING)
    @Column(name = "activated_status")
    private ActivatedStatusEnum activatedStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "blocked_status")
    private BlockStatusEnum blockedStatus;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "updated_date")
    private Date updatedDate;

    @PrePersist
    protected void onCreate() { createdDate = new Date(); }

    @PreUpdate
    protected void onUpdate() { updatedDate = new Date(); }
}
