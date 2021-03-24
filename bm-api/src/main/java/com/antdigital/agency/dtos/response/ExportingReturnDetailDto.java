package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class ExportingReturnDetailDto extends ExportingReturnDto{
    private String customerCode;
    private String customerName;
    private Double total;
    private Float totalQuantity;
}
