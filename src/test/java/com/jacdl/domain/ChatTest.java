package com.jacdl.domain;

import static com.jacdl.domain.ChatTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jacdl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Chat.class);
        Chat chat1 = getChatSample1();
        Chat chat2 = new Chat();
        assertThat(chat1).isNotEqualTo(chat2);

        chat2.setId(chat1.getId());
        assertThat(chat1).isEqualTo(chat2);

        chat2 = getChatSample2();
        assertThat(chat1).isNotEqualTo(chat2);
    }
}
