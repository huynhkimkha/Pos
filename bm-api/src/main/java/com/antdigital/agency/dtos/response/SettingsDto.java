package com.antdigital.agency.dtos.response;

import lombok.Data;
import java.util.Date;

@Data
public class SettingsDto {
    private Integer id;
    private AgencyDto agency;
    private String label;
    private String key;
    private String values;
    private Date createdDate;
    private Date updatedDate;
}
