package com.antdigital.agency.dal.data;

import lombok.Data;

@Data
public class MonthBillDetail {
    private int monthDate;
    private int yearDate;
    private double total;
}
