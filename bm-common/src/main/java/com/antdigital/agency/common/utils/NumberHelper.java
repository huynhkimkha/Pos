package com.antdigital.agency.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberHelper {

    public static Double roundNumber(double value, int places) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
