package com.jacdl.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificationCriteriaTest {

    @Test
    void newNotificationCriteriaHasAllFiltersNullTest() {
        var notificationCriteria = new NotificationCriteria();
        assertThat(notificationCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void notificationCriteriaFluentMethodsCreatesFiltersTest() {
        var notificationCriteria = new NotificationCriteria();

        setAllFilters(notificationCriteria);

        assertThat(notificationCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void notificationCriteriaCopyCreatesNullFilterTest() {
        var notificationCriteria = new NotificationCriteria();
        var copy = notificationCriteria.copy();

        assertThat(notificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(notificationCriteria)
        );
    }

    @Test
    void notificationCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificationCriteria = new NotificationCriteria();
        setAllFilters(notificationCriteria);

        var copy = notificationCriteria.copy();

        assertThat(notificationCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(notificationCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificationCriteria = new NotificationCriteria();

        assertThat(notificationCriteria).hasToString("NotificationCriteria{}");
    }

    private static void setAllFilters(NotificationCriteria notificationCriteria) {
        notificationCriteria.id();
        notificationCriteria.reference();
        notificationCriteria.createdAt();
        notificationCriteria.status();
        notificationCriteria.userId();
        notificationCriteria.distinct();
    }

    private static Condition<NotificationCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificationCriteria> copyFiltersAre(
        NotificationCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
