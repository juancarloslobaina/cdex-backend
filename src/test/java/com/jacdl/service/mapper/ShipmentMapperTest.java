package com.jacdl.service.mapper;

import static com.jacdl.domain.ShipmentAsserts.*;
import static com.jacdl.domain.ShipmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentMapperTest {

    private ShipmentMapper shipmentMapper;

    @BeforeEach
    void setUp() {
        shipmentMapper = new ShipmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getShipmentSample1();
        var actual = shipmentMapper.toEntity(shipmentMapper.toDto(expected));
        assertShipmentAllPropertiesEquals(expected, actual);
    }
}
