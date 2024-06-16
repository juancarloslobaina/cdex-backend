package com.jacdl.web.rest;

import static com.jacdl.domain.DeliveryAsserts.*;
import static com.jacdl.web.rest.TestUtil.createUpdateProxyForBean;
import static com.jacdl.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacdl.IntegrationTest;
import com.jacdl.domain.Delivery;
import com.jacdl.domain.User;
import com.jacdl.repository.DeliveryRepository;
import com.jacdl.repository.UserRepository;
import com.jacdl.service.DeliveryService;
import com.jacdl.service.dto.DeliveryDTO;
import com.jacdl.service.mapper.DeliveryMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link DeliveryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DeliveryResourceIT {

    private static final BigDecimal DEFAULT_CASH_AVAILABLE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CASH_AVAILABLE = new BigDecimal(2);

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/deliveries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DeliveryRepository deliveryRepositoryMock;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Mock
    private DeliveryService deliveryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryMockMvc;

    private Delivery delivery;

    private Delivery insertedDelivery;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createEntity(EntityManager em) {
        Delivery delivery = new Delivery().cashAvailable(DEFAULT_CASH_AVAILABLE).location(DEFAULT_LOCATION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        delivery.setUser(user);
        return delivery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Delivery createUpdatedEntity(EntityManager em) {
        Delivery delivery = new Delivery().cashAvailable(UPDATED_CASH_AVAILABLE).location(UPDATED_LOCATION);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        delivery.setUser(user);
        return delivery;
    }

    @BeforeEach
    public void initTest() {
        delivery = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedDelivery != null) {
            deliveryRepository.delete(insertedDelivery);
            insertedDelivery = null;
        }
    }

    @Test
    @Transactional
    void createDelivery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);
        var returnedDeliveryDTO = om.readValue(
            restDeliveryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DeliveryDTO.class
        );

        // Validate the Delivery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDelivery = deliveryMapper.toEntity(returnedDeliveryDTO);
        assertDeliveryUpdatableFieldsEquals(returnedDelivery, getPersistedDelivery(returnedDelivery));

        insertedDelivery = returnedDelivery;
    }

    @Test
    @Transactional
    void createDeliveryWithExistingId() throws Exception {
        // Create the Delivery with an existing ID
        delivery.setId(1L);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCashAvailableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        delivery.setCashAvailable(null);

        // Create the Delivery, which fails.
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        restDeliveryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDeliveries() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        // Get all the deliveryList
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(delivery.getId().intValue())))
            .andExpect(jsonPath("$.[*].cashAvailable").value(hasItem(sameNumber(DEFAULT_CASH_AVAILABLE))))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(deliveryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDeliveriesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(deliveryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDeliveryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(deliveryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        // Get the delivery
        restDeliveryMockMvc
            .perform(get(ENTITY_API_URL_ID, delivery.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(delivery.getId().intValue()))
            .andExpect(jsonPath("$.cashAvailable").value(sameNumber(DEFAULT_CASH_AVAILABLE)))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION));
    }

    @Test
    @Transactional
    void getNonExistingDelivery() throws Exception {
        // Get the delivery
        restDeliveryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery
        Delivery updatedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDelivery are not directly saved in db
        em.detach(updatedDelivery);
        updatedDelivery.cashAvailable(UPDATED_CASH_AVAILABLE).location(UPDATED_LOCATION);
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(updatedDelivery);

        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDeliveryToMatchAllProperties(updatedDelivery);
    }

    @Test
    @Transactional
    void putNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.cashAvailable(UPDATED_CASH_AVAILABLE).location(UPDATED_LOCATION);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDelivery, delivery), getPersistedDelivery(delivery));
    }

    @Test
    @Transactional
    void fullUpdateDeliveryWithPatch() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the delivery using partial update
        Delivery partialUpdatedDelivery = new Delivery();
        partialUpdatedDelivery.setId(delivery.getId());

        partialUpdatedDelivery.cashAvailable(UPDATED_CASH_AVAILABLE).location(UPDATED_LOCATION);

        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDelivery.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDelivery))
            )
            .andExpect(status().isOk());

        // Validate the Delivery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDeliveryUpdatableFieldsEquals(partialUpdatedDelivery, getPersistedDelivery(partialUpdatedDelivery));
    }

    @Test
    @Transactional
    void patchNonExistingDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(deliveryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDelivery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        delivery.setId(longCount.incrementAndGet());

        // Create the Delivery
        DeliveryDTO deliveryDTO = deliveryMapper.toDto(delivery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(deliveryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Delivery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDelivery() throws Exception {
        // Initialize the database
        insertedDelivery = deliveryRepository.saveAndFlush(delivery);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the delivery
        restDeliveryMockMvc
            .perform(delete(ENTITY_API_URL_ID, delivery.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return deliveryRepository.count();
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

    protected Delivery getPersistedDelivery(Delivery delivery) {
        return deliveryRepository.findById(delivery.getId()).orElseThrow();
    }

    protected void assertPersistedDeliveryToMatchAllProperties(Delivery expectedDelivery) {
        assertDeliveryAllPropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }

    protected void assertPersistedDeliveryToMatchUpdatableProperties(Delivery expectedDelivery) {
        assertDeliveryAllUpdatablePropertiesEquals(expectedDelivery, getPersistedDelivery(expectedDelivery));
    }
}
