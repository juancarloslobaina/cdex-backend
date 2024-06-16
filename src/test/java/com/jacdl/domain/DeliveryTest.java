package com.jacdl.domain;

import static com.jacdl.domain.DeliveryTestSamples.*;
import static com.jacdl.domain.ProviderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeliveryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Delivery.class);
        Delivery delivery1 = getDeliverySample1();
        Delivery delivery2 = new Delivery();
        assertThat(delivery1).isNotEqualTo(delivery2);

        delivery2.setId(delivery1.getId());
        assertThat(delivery1).isEqualTo(delivery2);

        delivery2 = getDeliverySample2();
        assertThat(delivery1).isNotEqualTo(delivery2);
    }

    @Test
    void providerTest() {
        Delivery delivery = getDeliveryRandomSampleGenerator();
        Provider providerBack = getProviderRandomSampleGenerator();

        delivery.addProvider(providerBack);
        assertThat(delivery.getProviders()).containsOnly(providerBack);
        assertThat(providerBack.getDeliveries()).containsOnly(delivery);

        delivery.removeProvider(providerBack);
        assertThat(delivery.getProviders()).doesNotContain(providerBack);
        assertThat(providerBack.getDeliveries()).doesNotContain(delivery);

        delivery.providers(new HashSet<>(Set.of(providerBack)));
        assertThat(delivery.getProviders()).containsOnly(providerBack);
        assertThat(providerBack.getDeliveries()).containsOnly(delivery);

        delivery.setProviders(new HashSet<>());
        assertThat(delivery.getProviders()).doesNotContain(providerBack);
        assertThat(providerBack.getDeliveries()).doesNotContain(delivery);
    }
}
