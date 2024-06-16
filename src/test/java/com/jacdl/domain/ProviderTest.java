package com.jacdl.domain;

import static com.jacdl.domain.ClientTestSamples.*;
import static com.jacdl.domain.DeliveryTestSamples.*;
import static com.jacdl.domain.ProviderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProviderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Provider.class);
        Provider provider1 = getProviderSample1();
        Provider provider2 = new Provider();
        assertThat(provider1).isNotEqualTo(provider2);

        provider2.setId(provider1.getId());
        assertThat(provider1).isEqualTo(provider2);

        provider2 = getProviderSample2();
        assertThat(provider1).isNotEqualTo(provider2);
    }

    @Test
    void clientTest() {
        Provider provider = getProviderRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        provider.addClient(clientBack);
        assertThat(provider.getClients()).containsOnly(clientBack);

        provider.removeClient(clientBack);
        assertThat(provider.getClients()).doesNotContain(clientBack);

        provider.clients(new HashSet<>(Set.of(clientBack)));
        assertThat(provider.getClients()).containsOnly(clientBack);

        provider.setClients(new HashSet<>());
        assertThat(provider.getClients()).doesNotContain(clientBack);
    }

    @Test
    void deliveryTest() {
        Provider provider = getProviderRandomSampleGenerator();
        Delivery deliveryBack = getDeliveryRandomSampleGenerator();

        provider.addDelivery(deliveryBack);
        assertThat(provider.getDeliveries()).containsOnly(deliveryBack);

        provider.removeDelivery(deliveryBack);
        assertThat(provider.getDeliveries()).doesNotContain(deliveryBack);

        provider.deliveries(new HashSet<>(Set.of(deliveryBack)));
        assertThat(provider.getDeliveries()).containsOnly(deliveryBack);

        provider.setDeliveries(new HashSet<>());
        assertThat(provider.getDeliveries()).doesNotContain(deliveryBack);
    }
}
