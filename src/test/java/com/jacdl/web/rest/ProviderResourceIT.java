package com.jacdl.web.rest;

import static com.jacdl.domain.ProviderAsserts.*;
import static com.jacdl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacdl.IntegrationTest;
import com.jacdl.domain.Provider;
import com.jacdl.domain.User;
import com.jacdl.repository.ProviderRepository;
import com.jacdl.repository.UserRepository;
import com.jacdl.service.ProviderService;
import com.jacdl.service.dto.ProviderDTO;
import com.jacdl.service.mapper.ProviderMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ProviderResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProviderResourceIT {

    private static final String DEFAULT_REFERRAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_REFERRAL_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/providers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ProviderRepository providerRepositoryMock;

    @Autowired
    private ProviderMapper providerMapper;

    @Mock
    private ProviderService providerServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProviderMockMvc;

    private Provider provider;

    private Provider insertedProvider;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createEntity(EntityManager em) {
        Provider provider = new Provider().referralCode(DEFAULT_REFERRAL_CODE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        provider.setUser(user);
        return provider;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createUpdatedEntity(EntityManager em) {
        Provider provider = new Provider().referralCode(UPDATED_REFERRAL_CODE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        provider.setUser(user);
        return provider;
    }

    @BeforeEach
    public void initTest() {
        provider = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProvider != null) {
            providerRepository.delete(insertedProvider);
            insertedProvider = null;
        }
    }

    @Test
    @Transactional
    void createProvider() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);
        var returnedProviderDTO = om.readValue(
            restProviderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(providerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProviderDTO.class
        );

        // Validate the Provider in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProvider = providerMapper.toEntity(returnedProviderDTO);
        assertProviderUpdatableFieldsEquals(returnedProvider, getPersistedProvider(returnedProvider));

        insertedProvider = returnedProvider;
    }

    @Test
    @Transactional
    void createProviderWithExistingId() throws Exception {
        // Create the Provider with an existing ID
        provider.setId(1L);
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProviderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(providerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferralCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        provider.setReferralCode(null);

        // Create the Provider, which fails.
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        restProviderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(providerDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProviders() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        // Get all the providerList
        restProviderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].referralCode").value(hasItem(DEFAULT_REFERRAL_CODE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvidersWithEagerRelationshipsIsEnabled() throws Exception {
        when(providerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProviderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(providerServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProvidersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(providerServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProviderMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(providerRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        // Get the provider
        restProviderMockMvc
            .perform(get(ENTITY_API_URL_ID, provider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(provider.getId().intValue()))
            .andExpect(jsonPath("$.referralCode").value(DEFAULT_REFERRAL_CODE));
    }

    @Test
    @Transactional
    void getNonExistingProvider() throws Exception {
        // Get the provider
        restProviderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider
        Provider updatedProvider = providerRepository.findById(provider.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProvider are not directly saved in db
        em.detach(updatedProvider);
        updatedProvider.referralCode(UPDATED_REFERRAL_CODE);
        ProviderDTO providerDTO = providerMapper.toDto(updatedProvider);

        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, providerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(providerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProviderToMatchAllProperties(updatedProvider);
    }

    @Test
    @Transactional
    void putNonExistingProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, providerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(providerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(providerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(providerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProviderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProvider, provider), getPersistedProvider(provider));
    }

    @Test
    @Transactional
    void fullUpdateProviderWithPatch() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the provider using partial update
        Provider partialUpdatedProvider = new Provider();
        partialUpdatedProvider.setId(provider.getId());

        partialUpdatedProvider.referralCode(UPDATED_REFERRAL_CODE);

        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProvider.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProvider))
            )
            .andExpect(status().isOk());

        // Validate the Provider in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProviderUpdatableFieldsEquals(partialUpdatedProvider, getPersistedProvider(partialUpdatedProvider));
    }

    @Test
    @Transactional
    void patchNonExistingProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, providerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(providerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(providerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProvider() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        provider.setId(longCount.incrementAndGet());

        // Create the Provider
        ProviderDTO providerDTO = providerMapper.toDto(provider);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProviderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(providerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Provider in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProvider() throws Exception {
        // Initialize the database
        insertedProvider = providerRepository.saveAndFlush(provider);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the provider
        restProviderMockMvc
            .perform(delete(ENTITY_API_URL_ID, provider.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return providerRepository.count();
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

    protected Provider getPersistedProvider(Provider provider) {
        return providerRepository.findById(provider.getId()).orElseThrow();
    }

    protected void assertPersistedProviderToMatchAllProperties(Provider expectedProvider) {
        assertProviderAllPropertiesEquals(expectedProvider, getPersistedProvider(expectedProvider));
    }

    protected void assertPersistedProviderToMatchUpdatableProperties(Provider expectedProvider) {
        assertProviderAllUpdatablePropertiesEquals(expectedProvider, getPersistedProvider(expectedProvider));
    }
}
