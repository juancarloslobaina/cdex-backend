package com.jacdl.domain;

import static com.jacdl.domain.ProductTestSamples.*;
import static com.jacdl.domain.ProviderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Product.class);
        Product product1 = getProductSample1();
        Product product2 = new Product();
        assertThat(product1).isNotEqualTo(product2);

        product2.setId(product1.getId());
        assertThat(product1).isEqualTo(product2);

        product2 = getProductSample2();
        assertThat(product1).isNotEqualTo(product2);
    }

    @Test
    void providerTest() {
        Product product = getProductRandomSampleGenerator();
        Provider providerBack = getProviderRandomSampleGenerator();

        product.setProvider(providerBack);
        assertThat(product.getProvider()).isEqualTo(providerBack);

        product.provider(null);
        assertThat(product.getProvider()).isNull();
    }
}
