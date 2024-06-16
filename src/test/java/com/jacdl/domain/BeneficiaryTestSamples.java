package com.jacdl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BeneficiaryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Beneficiary getBeneficiarySample1() {
        return new Beneficiary()
            .id(1L)
            .alias("alias1")
            .phone("phone1")
            .city("city1")
            .address("address1")
            .referenceAddress("referenceAddress1");
    }

    public static Beneficiary getBeneficiarySample2() {
        return new Beneficiary()
            .id(2L)
            .alias("alias2")
            .phone("phone2")
            .city("city2")
            .address("address2")
            .referenceAddress("referenceAddress2");
    }

    public static Beneficiary getBeneficiaryRandomSampleGenerator() {
        return new Beneficiary()
            .id(longCount.incrementAndGet())
            .alias(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .referenceAddress(UUID.randomUUID().toString());
    }
}
