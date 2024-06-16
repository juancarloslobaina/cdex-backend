package com.jacdl.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ChatCriteriaTest {

    @Test
    void newChatCriteriaHasAllFiltersNullTest() {
        var chatCriteria = new ChatCriteria();
        assertThat(chatCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void chatCriteriaFluentMethodsCreatesFiltersTest() {
        var chatCriteria = new ChatCriteria();

        setAllFilters(chatCriteria);

        assertThat(chatCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void chatCriteriaCopyCreatesNullFilterTest() {
        var chatCriteria = new ChatCriteria();
        var copy = chatCriteria.copy();

        assertThat(chatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(chatCriteria)
        );
    }

    @Test
    void chatCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var chatCriteria = new ChatCriteria();
        setAllFilters(chatCriteria);

        var copy = chatCriteria.copy();

        assertThat(chatCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(chatCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var chatCriteria = new ChatCriteria();

        assertThat(chatCriteria).hasToString("ChatCriteria{}");
    }

    private static void setAllFilters(ChatCriteria chatCriteria) {
        chatCriteria.id();
        chatCriteria.message();
        chatCriteria.createdAt();
        chatCriteria.status();
        chatCriteria.fromId();
        chatCriteria.toId();
        chatCriteria.distinct();
    }

    private static Condition<ChatCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getFromId()) &&
                condition.apply(criteria.getToId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ChatCriteria> copyFiltersAre(ChatCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getFromId(), copy.getFromId()) &&
                condition.apply(criteria.getToId(), copy.getToId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
