package com.antdigital.agency.dtos.response;

import lombok.Data;

@Data
public class DateBillDetailDto {
    private int date;
    private int month;
    private int year;
    private double total;
}
