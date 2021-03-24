package com.antdigital.agency.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class UUIDHelper {
    /**
     * Type 5 UUID Generation
     */
    public static UUID generateV5(String url, String name) throws URISyntaxException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // concat domain + input + date
        URI uri = new URI(url);
        String source = uri.getHost().concat(name) + new Date().getTime();
        byte[] bytes = source.getBytes("UTF-8");
        UUID uuid = type5UUIDFromBytes(bytes);

        return uuid;
    }
    /**
     * Type 5 UUID Generation
     */
    public static UUID type5UUIDFromBytes(byte[] name) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = Arrays.copyOfRange(md.digest(name), 0, 16);
        bytes[6] &= 0x0f; /* clear version        */
        bytes[6] |= 0x50; /* set to version 5     */
        bytes[8] &= 0x3f; /* clear variant        */
        bytes[8] |= 0x80; /* set to IETF variant  */
        return constructType5UUID(bytes);
    }
    /**
     * Type 4 UUID Generation
     */
    public static UUID generateType4UUID() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

    private static UUID constructType5UUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";

        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);

        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }
}
