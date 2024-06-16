package com.jacdl.web.rest;

import static com.jacdl.domain.ShipmentAsserts.*;
import static com.jacdl.web.rest.TestUtil.createUpdateProxyForBean;
import static com.jacdl.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacdl.IntegrationTest;
import com.jacdl.domain.Beneficiary;
import com.jacdl.domain.Client;
import com.jacdl.domain.Delivery;
import com.jacdl.domain.Provider;
import com.jacdl.domain.Shipment;
import com.jacdl.domain.enumeration.ShipmentStatus;
import com.jacdl.domain.enumeration.ShiptmentType;
import com.jacdl.repository.ShipmentRepository;
import com.jacdl.service.dto.ShipmentDTO;
import com.jacdl.service.mapper.ShipmentMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ShipmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShipmentResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ShipmentStatus DEFAULT_STATUS = ShipmentStatus.CREATED;
    private static final ShipmentStatus UPDATED_STATUS = ShipmentStatus.ACCEPTED;

    private static final ShiptmentType DEFAULT_TYPE = ShiptmentType.ALL;
    private static final ShiptmentType UPDATED_TYPE = ShiptmentType.MONEY;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final byte[] DEFAULT_SCREENSHOT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SCREENSHOT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_SCREENSHOT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_SCREENSHOT_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/shipments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShipmentRepository shipmentRepository;

    @Autowired
    private ShipmentMapper shipmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShipmentMockMvc;

    private Shipment shipment;

    private Shipment insertedShipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createEntity(EntityManager em) {
        Shipment shipment = new Shipment()
            .reference(DEFAULT_REFERENCE)
            .amount(DEFAULT_AMOUNT)
            .createdAt(DEFAULT_CREATED_AT)
            .status(DEFAULT_STATUS)
            .type(DEFAULT_TYPE)
            .active(DEFAULT_ACTIVE)
            .screenshot(DEFAULT_SCREENSHOT)
            .screenshotContentType(DEFAULT_SCREENSHOT_CONTENT_TYPE);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        shipment.setClient(client);
        // Add required entity
        Provider provider;
        if (TestUtil.findAll(em, Provider.class).isEmpty()) {
            provider = ProviderResourceIT.createEntity(em);
            em.persist(provider);
            em.flush();
        } else {
            provider = TestUtil.findAll(em, Provider.class).get(0);
        }
        shipment.setProvider(provider);
        // Add required entity
        Beneficiary beneficiary;
        if (TestUtil.findAll(em, Beneficiary.class).isEmpty()) {
            beneficiary = BeneficiaryResourceIT.createEntity(em);
            em.persist(beneficiary);
            em.flush();
        } else {
            beneficiary = TestUtil.findAll(em, Beneficiary.class).get(0);
        }
        shipment.setBeneficiary(beneficiary);
        return shipment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipment createUpdatedEntity(EntityManager em) {
        Shipment shipment = new Shipment()
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .active(UPDATED_ACTIVE)
            .screenshot(UPDATED_SCREENSHOT)
            .screenshotContentType(UPDATED_SCREENSHOT_CONTENT_TYPE);
        // Add required entity
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createUpdatedEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        shipment.setClient(client);
        // Add required entity
        Provider provider;
        if (TestUtil.findAll(em, Provider.class).isEmpty()) {
            provider = ProviderResourceIT.createUpdatedEntity(em);
            em.persist(provider);
            em.flush();
        } else {
            provider = TestUtil.findAll(em, Provider.class).get(0);
        }
        shipment.setProvider(provider);
        // Add required entity
        Beneficiary beneficiary;
        if (TestUtil.findAll(em, Beneficiary.class).isEmpty()) {
            beneficiary = BeneficiaryResourceIT.createUpdatedEntity(em);
            em.persist(beneficiary);
            em.flush();
        } else {
            beneficiary = TestUtil.findAll(em, Beneficiary.class).get(0);
        }
        shipment.setBeneficiary(beneficiary);
        return shipment;
    }

    @BeforeEach
    public void initTest() {
        shipment = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedShipment != null) {
            shipmentRepository.delete(insertedShipment);
            insertedShipment = null;
        }
    }

    @Test
    @Transactional
    void createShipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);
        var returnedShipmentDTO = om.readValue(
            restShipmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShipmentDTO.class
        );

        // Validate the Shipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShipment = shipmentMapper.toEntity(returnedShipmentDTO);
        assertShipmentUpdatableFieldsEquals(returnedShipment, getPersistedShipment(returnedShipment));

        insertedShipment = returnedShipment;
    }

    @Test
    @Transactional
    void createShipmentWithExistingId() throws Exception {
        // Create the Shipment with an existing ID
        shipment.setId(1L);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setReference(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setAmount(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setCreatedAt(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setStatus(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setType(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shipment.setActive(null);

        // Create the Shipment, which fails.
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        restShipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShipments() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].screenshotContentType").value(hasItem(DEFAULT_SCREENSHOT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].screenshot").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_SCREENSHOT))));
    }

    @Test
    @Transactional
    void getShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get the shipment
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL_ID, shipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipment.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.screenshotContentType").value(DEFAULT_SCREENSHOT_CONTENT_TYPE))
            .andExpect(jsonPath("$.screenshot").value(Base64.getEncoder().encodeToString(DEFAULT_SCREENSHOT)));
    }

    @Test
    @Transactional
    void getShipmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        Long id = shipment.getId();

        defaultShipmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShipmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShipmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShipmentsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reference equals to
        defaultShipmentFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllShipmentsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reference in
        defaultShipmentFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllShipmentsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reference is not null
        defaultShipmentFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reference contains
        defaultShipmentFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllShipmentsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where reference does not contain
        defaultShipmentFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount equals to
        defaultShipmentFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount in
        defaultShipmentFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount is not null
        defaultShipmentFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount is greater than or equal to
        defaultShipmentFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount is less than or equal to
        defaultShipmentFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount is less than
        defaultShipmentFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where amount is greater than
        defaultShipmentFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    void getAllShipmentsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where createdAt equals to
        defaultShipmentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShipmentsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where createdAt in
        defaultShipmentFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShipmentsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where createdAt is not null
        defaultShipmentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status equals to
        defaultShipmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status in
        defaultShipmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShipmentsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where status is not null
        defaultShipmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where type equals to
        defaultShipmentFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllShipmentsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where type in
        defaultShipmentFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllShipmentsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where type is not null
        defaultShipmentFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where active equals to
        defaultShipmentFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllShipmentsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where active in
        defaultShipmentFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllShipmentsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        // Get all the shipmentList where active is not null
        defaultShipmentFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllShipmentsByClientIsEqualToSomething() throws Exception {
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            client = ClientResourceIT.createEntity(em);
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        shipment.setClient(client);
        shipmentRepository.saveAndFlush(shipment);
        Long clientId = client.getId();
        // Get all the shipmentList where client equals to clientId
        defaultShipmentShouldBeFound("clientId.equals=" + clientId);

        // Get all the shipmentList where client equals to (clientId + 1)
        defaultShipmentShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentsByProviderIsEqualToSomething() throws Exception {
        Provider provider;
        if (TestUtil.findAll(em, Provider.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            provider = ProviderResourceIT.createEntity(em);
        } else {
            provider = TestUtil.findAll(em, Provider.class).get(0);
        }
        em.persist(provider);
        em.flush();
        shipment.setProvider(provider);
        shipmentRepository.saveAndFlush(shipment);
        Long providerId = provider.getId();
        // Get all the shipmentList where provider equals to providerId
        defaultShipmentShouldBeFound("providerId.equals=" + providerId);

        // Get all the shipmentList where provider equals to (providerId + 1)
        defaultShipmentShouldNotBeFound("providerId.equals=" + (providerId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentsByDeliveryIsEqualToSomething() throws Exception {
        Delivery delivery;
        if (TestUtil.findAll(em, Delivery.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            delivery = DeliveryResourceIT.createEntity(em);
        } else {
            delivery = TestUtil.findAll(em, Delivery.class).get(0);
        }
        em.persist(delivery);
        em.flush();
        shipment.setDelivery(delivery);
        shipmentRepository.saveAndFlush(shipment);
        Long deliveryId = delivery.getId();
        // Get all the shipmentList where delivery equals to deliveryId
        defaultShipmentShouldBeFound("deliveryId.equals=" + deliveryId);

        // Get all the shipmentList where delivery equals to (deliveryId + 1)
        defaultShipmentShouldNotBeFound("deliveryId.equals=" + (deliveryId + 1));
    }

    @Test
    @Transactional
    void getAllShipmentsByBeneficiaryIsEqualToSomething() throws Exception {
        Beneficiary beneficiary;
        if (TestUtil.findAll(em, Beneficiary.class).isEmpty()) {
            shipmentRepository.saveAndFlush(shipment);
            beneficiary = BeneficiaryResourceIT.createEntity(em);
        } else {
            beneficiary = TestUtil.findAll(em, Beneficiary.class).get(0);
        }
        em.persist(beneficiary);
        em.flush();
        shipment.setBeneficiary(beneficiary);
        shipmentRepository.saveAndFlush(shipment);
        Long beneficiaryId = beneficiary.getId();
        // Get all the shipmentList where beneficiary equals to beneficiaryId
        defaultShipmentShouldBeFound("beneficiaryId.equals=" + beneficiaryId);

        // Get all the shipmentList where beneficiary equals to (beneficiaryId + 1)
        defaultShipmentShouldNotBeFound("beneficiaryId.equals=" + (beneficiaryId + 1));
    }

    private void defaultShipmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShipmentShouldBeFound(shouldBeFound);
        defaultShipmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShipmentShouldBeFound(String filter) throws Exception {
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].screenshotContentType").value(hasItem(DEFAULT_SCREENSHOT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].screenshot").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_SCREENSHOT))));

        // Check, that the count call also returns 1
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShipmentShouldNotBeFound(String filter) throws Exception {
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShipment() throws Exception {
        // Get the shipment
        restShipmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment
        Shipment updatedShipment = shipmentRepository.findById(shipment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShipment are not directly saved in db
        em.detach(updatedShipment);
        updatedShipment
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .active(UPDATED_ACTIVE)
            .screenshot(UPDATED_SCREENSHOT)
            .screenshotContentType(UPDATED_SCREENSHOT_CONTENT_TYPE);
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(updatedShipment);

        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShipmentToMatchAllProperties(updatedShipment);
    }

    @Test
    @Transactional
    void putNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment.createdAt(UPDATED_CREATED_AT).status(UPDATED_STATUS);

        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipment))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedShipment, shipment), getPersistedShipment(shipment));
    }

    @Test
    @Transactional
    void fullUpdateShipmentWithPatch() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shipment using partial update
        Shipment partialUpdatedShipment = new Shipment();
        partialUpdatedShipment.setId(shipment.getId());

        partialUpdatedShipment
            .reference(UPDATED_REFERENCE)
            .amount(UPDATED_AMOUNT)
            .createdAt(UPDATED_CREATED_AT)
            .status(UPDATED_STATUS)
            .type(UPDATED_TYPE)
            .active(UPDATED_ACTIVE)
            .screenshot(UPDATED_SCREENSHOT)
            .screenshotContentType(UPDATED_SCREENSHOT_CONTENT_TYPE);

        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShipment))
            )
            .andExpect(status().isOk());

        // Validate the Shipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShipmentUpdatableFieldsEquals(partialUpdatedShipment, getPersistedShipment(partialUpdatedShipment));
    }

    @Test
    @Transactional
    void patchNonExistingShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shipmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shipment.setId(longCount.incrementAndGet());

        // Create the Shipment
        ShipmentDTO shipmentDTO = shipmentMapper.toDto(shipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShipmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShipment() throws Exception {
        // Initialize the database
        insertedShipment = shipmentRepository.saveAndFlush(shipment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shipment
        restShipmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shipmentRepository.count();
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

    protected Shipment getPersistedShipment(Shipment shipment) {
        return shipmentRepository.findById(shipment.getId()).orElseThrow();
    }

    protected void assertPersistedShipmentToMatchAllProperties(Shipment expectedShipment) {
        assertShipmentAllPropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }

    protected void assertPersistedShipmentToMatchUpdatableProperties(Shipment expectedShipment) {
        assertShipmentAllUpdatablePropertiesEquals(expectedShipment, getPersistedShipment(expectedShipment));
    }
}
