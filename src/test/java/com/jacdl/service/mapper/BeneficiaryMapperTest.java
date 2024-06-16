package com.jacdl.service.mapper;

import static com.jacdl.domain.BeneficiaryAsserts.*;
import static com.jacdl.domain.BeneficiaryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeneficiaryMapperTest {

    private BeneficiaryMapper beneficiaryMapper;

    @BeforeEach
    void setUp() {
        beneficiaryMapper = new BeneficiaryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBeneficiarySample1();
        var actual = beneficiaryMapper.toEntity(beneficiaryMapper.toDto(expected));
        assertBeneficiaryAllPropertiesEquals(expected, actual);
    }
}
