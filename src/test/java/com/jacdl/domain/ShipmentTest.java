package com.jacdl.domain;

import static com.jacdl.domain.BeneficiaryTestSamples.*;
import static com.jacdl.domain.ClientTestSamples.*;
import static com.jacdl.domain.DeliveryTestSamples.*;
import static com.jacdl.domain.ProviderTestSamples.*;
import static com.jacdl.domain.ShipmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ShipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Shipment.class);
        Shipment shipment1 = getShipmentSample1();
        Shipment shipment2 = new Shipment();
        assertThat(shipment1).isNotEqualTo(shipment2);

        shipment2.setId(shipment1.getId());
        assertThat(shipment1).isEqualTo(shipment2);

        shipment2 = getShipmentSample2();
        assertThat(shipment1).isNotEqualTo(shipment2);
    }

    @Test
    void clientTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Client clientBack = getClientRandomSampleGenerator();

        shipment.setClient(clientBack);
        assertThat(shipment.getClient()).isEqualTo(clientBack);

        shipment.client(null);
        assertThat(shipment.getClient()).isNull();
    }

    @Test
    void providerTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Provider providerBack = getProviderRandomSampleGenerator();

        shipment.setProvider(providerBack);
        assertThat(shipment.getProvider()).isEqualTo(providerBack);

        shipment.provider(null);
        assertThat(shipment.getProvider()).isNull();
    }

    @Test
    void deliveryTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Delivery deliveryBack = getDeliveryRandomSampleGenerator();

        shipment.setDelivery(deliveryBack);
        assertThat(shipment.getDelivery()).isEqualTo(deliveryBack);

        shipment.delivery(null);
        assertThat(shipment.getDelivery()).isNull();
    }

    @Test
    void beneficiaryTest() {
        Shipment shipment = getShipmentRandomSampleGenerator();
        Beneficiary beneficiaryBack = getBeneficiaryRandomSampleGenerator();

        shipment.setBeneficiary(beneficiaryBack);
        assertThat(shipment.getBeneficiary()).isEqualTo(beneficiaryBack);

        shipment.beneficiary(null);
        assertThat(shipment.getBeneficiary()).isNull();
    }
}
