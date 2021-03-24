package com.antdigital.agency.dal.data;

import lombok.Data;

@Data
public class DateCostDetail {
    private int date;
    private int month;
    private int year;
    private double total;
}
