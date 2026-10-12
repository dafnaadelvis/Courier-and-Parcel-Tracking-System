package com.courier.util;

import java.security.SecureRandom;

public final class TrackingNumber {
    private static final SecureRandom RANDOM = new SecureRandom();

    private TrackingNumber() {
    }

    public static String generate() {
        int n = 100000 + RANDOM.nextInt(900000);
        return "CPT" + n;
    }
}
