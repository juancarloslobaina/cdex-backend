package com.jacdl.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertChatAllPropertiesEquals(Chat expected, Chat actual) {
        assertChatAutoGeneratedPropertiesEquals(expected, actual);
        assertChatAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertChatAllUpdatablePropertiesEquals(Chat expected, Chat actual) {
        assertChatUpdatableFieldsEquals(expected, actual);
        assertChatUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertChatAutoGeneratedPropertiesEquals(Chat expected, Chat actual) {
        assertThat(expected)
            .as("Verify Chat auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertChatUpdatableFieldsEquals(Chat expected, Chat actual) {
        assertThat(expected)
            .as("Verify Chat relevant properties")
            .satisfies(e -> assertThat(e.getMessage()).as("check message").isEqualTo(actual.getMessage()))
            .satisfies(e -> assertThat(e.getCreatedAt()).as("check createdAt").isEqualTo(actual.getCreatedAt()))
            .satisfies(e -> assertThat(e.getStatus()).as("check status").isEqualTo(actual.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertChatUpdatableRelationshipsEquals(Chat expected, Chat actual) {}
}