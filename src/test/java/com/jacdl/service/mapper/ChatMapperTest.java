package com.jacdl.service.mapper;

import static com.jacdl.domain.ChatAsserts.*;
import static com.jacdl.domain.ChatTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatMapperTest {

    private ChatMapper chatMapper;

    @BeforeEach
    void setUp() {
        chatMapper = new ChatMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getChatSample1();
        var actual = chatMapper.toEntity(chatMapper.toDto(expected));
        assertChatAllPropertiesEquals(expected, actual);
    }
}
