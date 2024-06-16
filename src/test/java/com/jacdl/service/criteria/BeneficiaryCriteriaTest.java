package com.jacdl.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class BeneficiaryCriteriaTest {

    @Test
    void newBeneficiaryCriteriaHasAllFiltersNullTest() {
        var beneficiaryCriteria = new BeneficiaryCriteria();
        assertThat(beneficiaryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void beneficiaryCriteriaFluentMethodsCreatesFiltersTest() {
        var beneficiaryCriteria = new BeneficiaryCriteria();

        setAllFilters(beneficiaryCriteria);

        assertThat(beneficiaryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void beneficiaryCriteriaCopyCreatesNullFilterTest() {
        var beneficiaryCriteria = new BeneficiaryCriteria();
        var copy = beneficiaryCriteria.copy();

        assertThat(beneficiaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(beneficiaryCriteria)
        );
    }

    @Test
    void beneficiaryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var beneficiaryCriteria = new BeneficiaryCriteria();
        setAllFilters(beneficiaryCriteria);

        var copy = beneficiaryCriteria.copy();

        assertThat(beneficiaryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(beneficiaryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var beneficiaryCriteria = new BeneficiaryCriteria();

        assertThat(beneficiaryCriteria).hasToString("BeneficiaryCriteria{}");
    }

    private static void setAllFilters(BeneficiaryCriteria beneficiaryCriteria) {
        beneficiaryCriteria.id();
        beneficiaryCriteria.alias();
        beneficiaryCriteria.phone();
        beneficiaryCriteria.city();
        beneficiaryCriteria.address();
        beneficiaryCriteria.referenceAddress();
        beneficiaryCriteria.userId();
        beneficiaryCriteria.distinct();
    }

    private static Condition<BeneficiaryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAlias()) &&
                condition.apply(criteria.getPhone()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getReferenceAddress()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<BeneficiaryCriteria> copyFiltersAre(BeneficiaryCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAlias(), copy.getAlias()) &&
                condition.apply(criteria.getPhone(), copy.getPhone()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getReferenceAddress(), copy.getReferenceAddress()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
