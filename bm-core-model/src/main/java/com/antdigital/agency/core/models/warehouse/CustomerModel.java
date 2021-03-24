package com.antdigital.agency.core.models.warehouse;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
public class CustomerModel {
    private String id;
    private String companyId;
    private String code;
    private CustomerGroupModel customerGroup1;
    private CustomerGroupModel customerGroup2;
    private CustomerGroupModel customerGroup3;
    private String address;
    private String district;
    private String province;
    private String fax;
    private String taxCode;
    private Integer discountPercent;
    private AccountingTableModel accounting;
    private Date paymentExpiryDate;
    private Double debtLimit;
    private Integer priceGroup;
    private String fullName;
    private String transactionCustomer;
    private String email;
    private String phone;
    private String employeeRefId;
    private String collaboratorRefId;
    private Date createdDate;
    private Date updatedDate;
}
