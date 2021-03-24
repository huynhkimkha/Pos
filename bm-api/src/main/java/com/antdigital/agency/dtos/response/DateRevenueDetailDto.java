package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class DateRevenueDetailDto {
    private int date;
    private int month;
    private int year;
    private double total;
}
