package com.antdigital.agency.dal.data;

import lombok.Data;

@Data
public class MonthCostDetail {
    private int monthDate;
    private int yearDate;
    private double total;
}
