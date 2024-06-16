package com.jacdl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProviderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Provider getProviderSample1() {
        return new Provider().id(1L).referralCode("referralCode1");
    }

    public static Provider getProviderSample2() {
        return new Provider().id(2L).referralCode("referralCode2");
    }

    public static Provider getProviderRandomSampleGenerator() {
        return new Provider().id(longCount.incrementAndGet()).referralCode(UUID.randomUUID().toString());
    }
}
