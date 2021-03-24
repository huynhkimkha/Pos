package com.antdigital.agency.dtos.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CompanyShortDto {
    private String id;
    private String name;
    private String nameSlug;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
