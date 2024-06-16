package com.jacdl.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ShipmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Shipment getShipmentSample1() {
        return new Shipment().id(1L).reference("reference1");
    }

    public static Shipment getShipmentSample2() {
        return new Shipment().id(2L).reference("reference2");
    }

    public static Shipment getShipmentRandomSampleGenerator() {
        return new Shipment().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString());
    }
}
