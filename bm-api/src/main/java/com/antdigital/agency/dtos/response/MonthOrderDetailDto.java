package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class MonthOrderDetailDto {
    private int monthDate;
    private int yearDate;
    private double total;
}
