package com.jacdl.web.rest;

import static com.jacdl.domain.ChatAsserts.*;
import static com.jacdl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacdl.IntegrationTest;
import com.jacdl.domain.Chat;
import com.jacdl.domain.User;
import com.jacdl.domain.enumeration.MessageStatus;
import com.jacdl.repository.ChatRepository;
import com.jacdl.repository.UserRepository;
import com.jacdl.service.ChatService;
import com.jacdl.service.dto.ChatDTO;
import com.jacdl.service.mapper.ChatMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ChatResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ChatResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final MessageStatus DEFAULT_STATUS = MessageStatus.SEND;
    private static final MessageStatus UPDATED_STATUS = MessageStatus.READ;

    private static final String ENTITY_API_URL = "/api/chats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepositoryMock;

    @Autowired
    private ChatMapper chatMapper;

    @Mock
    private ChatService chatServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restChatMockMvc;

    private Chat chat;

    private Chat insertedChat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createEntity(EntityManager em) {
        Chat chat = new Chat().message(DEFAULT_MESSAGE).createdAt(DEFAULT_CREATED_AT).status(DEFAULT_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        chat.setFrom(user);
        // Add required entity
        chat.setTo(user);
        return chat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Chat createUpdatedEntity(EntityManager em) {
        Chat chat = new Chat().message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).status(UPDATED_STATUS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        chat.setFrom(user);
        // Add required entity
        chat.setTo(user);
        return chat;
    }

    @BeforeEach
    public void initTest() {
        chat = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedChat != null) {
            chatRepository.delete(insertedChat);
            insertedChat = null;
        }
    }

    @Test
    @Transactional
    void createChat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);
        var returnedChatDTO = om.readValue(
            restChatMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ChatDTO.class
        );

        // Validate the Chat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedChat = chatMapper.toEntity(returnedChatDTO);
        assertChatUpdatableFieldsEquals(returnedChat, getPersistedChat(returnedChat));

        insertedChat = returnedChat;
    }

    @Test
    @Transactional
    void createChatWithExistingId() throws Exception {
        // Create the Chat with an existing ID
        chat.setId(1L);
        ChatDTO chatDTO = chatMapper.toDto(chat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMessageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chat.setMessage(null);

        // Create the Chat, which fails.
        ChatDTO chatDTO = chatMapper.toDto(chat);

        restChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        chat.setCreatedAt(null);

        // Create the Chat, which fails.
        ChatDTO chatDTO = chatMapper.toDto(chat);

        restChatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllChats() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList
        restChatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chat.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChatsWithEagerRelationshipsIsEnabled() throws Exception {
        when(chatServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(chatServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllChatsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(chatServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restChatMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(chatRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getChat() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get the chat
        restChatMockMvc
            .perform(get(ENTITY_API_URL_ID, chat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(chat.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getChatsByIdFiltering() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        Long id = chat.getId();

        defaultChatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultChatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultChatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllChatsByMessageIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where message equals to
        defaultChatFiltering("message.equals=" + DEFAULT_MESSAGE, "message.equals=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChatsByMessageIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where message in
        defaultChatFiltering("message.in=" + DEFAULT_MESSAGE + "," + UPDATED_MESSAGE, "message.in=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChatsByMessageIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where message is not null
        defaultChatFiltering("message.specified=true", "message.specified=false");
    }

    @Test
    @Transactional
    void getAllChatsByMessageContainsSomething() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where message contains
        defaultChatFiltering("message.contains=" + DEFAULT_MESSAGE, "message.contains=" + UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChatsByMessageNotContainsSomething() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where message does not contain
        defaultChatFiltering("message.doesNotContain=" + UPDATED_MESSAGE, "message.doesNotContain=" + DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    void getAllChatsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where createdAt equals to
        defaultChatFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where createdAt in
        defaultChatFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllChatsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where createdAt is not null
        defaultChatFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllChatsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where status equals to
        defaultChatFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllChatsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where status in
        defaultChatFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllChatsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        // Get all the chatList where status is not null
        defaultChatFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllChatsByFromIsEqualToSomething() throws Exception {
        User from;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            chatRepository.saveAndFlush(chat);
            from = UserResourceIT.createEntity(em);
        } else {
            from = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(from);
        em.flush();
        chat.setFrom(from);
        chatRepository.saveAndFlush(chat);
        Long fromId = from.getId();
        // Get all the chatList where from equals to fromId
        defaultChatShouldBeFound("fromId.equals=" + fromId);

        // Get all the chatList where from equals to (fromId + 1)
        defaultChatShouldNotBeFound("fromId.equals=" + (fromId + 1));
    }

    @Test
    @Transactional
    void getAllChatsByToIsEqualToSomething() throws Exception {
        User to;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            chatRepository.saveAndFlush(chat);
            to = UserResourceIT.createEntity(em);
        } else {
            to = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(to);
        em.flush();
        chat.setTo(to);
        chatRepository.saveAndFlush(chat);
        Long toId = to.getId();
        // Get all the chatList where to equals to toId
        defaultChatShouldBeFound("toId.equals=" + toId);

        // Get all the chatList where to equals to (toId + 1)
        defaultChatShouldNotBeFound("toId.equals=" + (toId + 1));
    }

    private void defaultChatFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultChatShouldBeFound(shouldBeFound);
        defaultChatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultChatShouldBeFound(String filter) throws Exception {
        restChatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(chat.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restChatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultChatShouldNotBeFound(String filter) throws Exception {
        restChatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restChatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingChat() throws Exception {
        // Get the chat
        restChatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingChat() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chat
        Chat updatedChat = chatRepository.findById(chat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedChat are not directly saved in db
        em.detach(updatedChat);
        updatedChat.message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).status(UPDATED_STATUS);
        ChatDTO chatDTO = chatMapper.toDto(updatedChat);

        restChatMockMvc
            .perform(put(ENTITY_API_URL_ID, chatDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isOk());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedChatToMatchAllProperties(updatedChat);
    }

    @Test
    @Transactional
    void putNonExistingChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(put(ENTITY_API_URL_ID, chatDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(chatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateChatWithPatch() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chat using partial update
        Chat partialUpdatedChat = new Chat();
        partialUpdatedChat.setId(chat.getId());

        partialUpdatedChat.status(UPDATED_STATUS);

        restChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChat))
            )
            .andExpect(status().isOk());

        // Validate the Chat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedChat, chat), getPersistedChat(chat));
    }

    @Test
    @Transactional
    void fullUpdateChatWithPatch() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the chat using partial update
        Chat partialUpdatedChat = new Chat();
        partialUpdatedChat.setId(chat.getId());

        partialUpdatedChat.message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).status(UPDATED_STATUS);

        restChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedChat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedChat))
            )
            .andExpect(status().isOk());

        // Validate the Chat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertChatUpdatableFieldsEquals(partialUpdatedChat, getPersistedChat(partialUpdatedChat));
    }

    @Test
    @Transactional
    void patchNonExistingChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, chatDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(chatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamChat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        chat.setId(longCount.incrementAndGet());

        // Create the Chat
        ChatDTO chatDTO = chatMapper.toDto(chat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restChatMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(chatDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Chat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteChat() throws Exception {
        // Initialize the database
        insertedChat = chatRepository.saveAndFlush(chat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the chat
        restChatMockMvc
            .perform(delete(ENTITY_API_URL_ID, chat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return chatRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Chat getPersistedChat(Chat chat) {
        return chatRepository.findById(chat.getId()).orElseThrow();
    }

    protected void assertPersistedChatToMatchAllProperties(Chat expectedChat) {
        assertChatAllPropertiesEquals(expectedChat, getPersistedChat(expectedChat));
    }

    protected void assertPersistedChatToMatchUpdatableProperties(Chat expectedChat) {
        assertChatAllUpdatablePropertiesEquals(expectedChat, getPersistedChat(expectedChat));
    }
}
