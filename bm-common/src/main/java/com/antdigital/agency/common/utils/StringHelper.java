package com.antdigital.agency.common.utils;

import java.time.LocalDate;

public class StringHelper {
    public static String NumberOfCertificate(String numberStr) {
        int month = LocalDate.now().getMonth().getValue();
        String monthStr = month > 9 ? String.valueOf(month) : "0" + month ;
        if (numberStr == null || numberStr.isEmpty()) {
            return monthStr + "/0001";
        }

        String[] arrStr = numberStr.split("/");
        int preNumber = Integer.valueOf(arrStr[1]) + 1;
        String preNumberStr = String.valueOf(preNumber);

        if (preNumber/10 < 1) {
            preNumberStr = "000" + preNumberStr;
        }
        else if (preNumber/100 < 1) {
            preNumberStr = "00" + preNumberStr;
        }
        else if (preNumber/1000 < 1) {
            preNumberStr = "0" + preNumberStr;
        }
        String number = arrStr[0] + "/" + preNumberStr;
        return number;
    }
}
