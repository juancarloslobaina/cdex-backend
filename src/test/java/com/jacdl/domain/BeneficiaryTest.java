package com.jacdl.domain;

import static com.jacdl.domain.BeneficiaryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BeneficiaryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Beneficiary.class);
        Beneficiary beneficiary1 = getBeneficiarySample1();
        Beneficiary beneficiary2 = new Beneficiary();
        assertThat(beneficiary1).isNotEqualTo(beneficiary2);

        beneficiary2.setId(beneficiary1.getId());
        assertThat(beneficiary1).isEqualTo(beneficiary2);

        beneficiary2 = getBeneficiarySample2();
        assertThat(beneficiary1).isNotEqualTo(beneficiary2);
    }
}
