package com.jacdl.service.mapper;

import static com.jacdl.domain.ProviderAsserts.*;
import static com.jacdl.domain.ProviderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProviderMapperTest {

    private ProviderMapper providerMapper;

    @BeforeEach
    void setUp() {
        providerMapper = new ProviderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProviderSample1();
        var actual = providerMapper.toEntity(providerMapper.toDto(expected));
        assertProviderAllPropertiesEquals(expected, actual);
    }
}
