package com.antdigital.agency.common.utils;


import java.util.Random;

public class RandomTextHelper {
    public static String generateRandomPassword(int length) {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = length;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
