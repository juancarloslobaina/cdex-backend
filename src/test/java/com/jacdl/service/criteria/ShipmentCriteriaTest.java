package com.jacdl.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ShipmentCriteriaTest {

    @Test
    void newShipmentCriteriaHasAllFiltersNullTest() {
        var shipmentCriteria = new ShipmentCriteria();
        assertThat(shipmentCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void shipmentCriteriaFluentMethodsCreatesFiltersTest() {
        var shipmentCriteria = new ShipmentCriteria();

        setAllFilters(shipmentCriteria);

        assertThat(shipmentCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void shipmentCriteriaCopyCreatesNullFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void shipmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var shipmentCriteria = new ShipmentCriteria();
        setAllFilters(shipmentCriteria);

        var copy = shipmentCriteria.copy();

        assertThat(shipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(shipmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var shipmentCriteria = new ShipmentCriteria();

        assertThat(shipmentCriteria).hasToString("ShipmentCriteria{}");
    }

    private static void setAllFilters(ShipmentCriteria shipmentCriteria) {
        shipmentCriteria.id();
        shipmentCriteria.reference();
        shipmentCriteria.amount();
        shipmentCriteria.createdAt();
        shipmentCriteria.status();
        shipmentCriteria.type();
        shipmentCriteria.active();
        shipmentCriteria.clientId();
        shipmentCriteria.providerId();
        shipmentCriteria.deliveryId();
        shipmentCriteria.beneficiaryId();
        shipmentCriteria.distinct();
    }

    private static Condition<ShipmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getAmount()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getClientId()) &&
                condition.apply(criteria.getProviderId()) &&
                condition.apply(criteria.getDeliveryId()) &&
                condition.apply(criteria.getBeneficiaryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ShipmentCriteria> copyFiltersAre(ShipmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getAmount(), copy.getAmount()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getClientId(), copy.getClientId()) &&
                condition.apply(criteria.getProviderId(), copy.getProviderId()) &&
                condition.apply(criteria.getDeliveryId(), copy.getDeliveryId()) &&
                condition.apply(criteria.getBeneficiaryId(), copy.getBeneficiaryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
