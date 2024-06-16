package com.jacdl.domain;

import static com.jacdl.domain.ClientTestSamples.*;
import static com.jacdl.domain.ProviderTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Client.class);
        Client client1 = getClientSample1();
        Client client2 = new Client();
        assertThat(client1).isNotEqualTo(client2);

        client2.setId(client1.getId());
        assertThat(client1).isEqualTo(client2);

        client2 = getClientSample2();
        assertThat(client1).isNotEqualTo(client2);
    }

    @Test
    void providerTest() {
        Client client = getClientRandomSampleGenerator();
        Provider providerBack = getProviderRandomSampleGenerator();

        client.addProvider(providerBack);
        assertThat(client.getProviders()).containsOnly(providerBack);
        assertThat(providerBack.getClients()).containsOnly(client);

        client.removeProvider(providerBack);
        assertThat(client.getProviders()).doesNotContain(providerBack);
        assertThat(providerBack.getClients()).doesNotContain(client);

        client.providers(new HashSet<>(Set.of(providerBack)));
        assertThat(client.getProviders()).containsOnly(providerBack);
        assertThat(providerBack.getClients()).containsOnly(client);

        client.setProviders(new HashSet<>());
        assertThat(client.getProviders()).doesNotContain(providerBack);
        assertThat(providerBack.getClients()).doesNotContain(client);
    }
}
