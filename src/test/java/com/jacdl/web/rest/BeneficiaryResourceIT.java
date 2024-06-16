package com.jacdl.web.rest;

import static com.jacdl.domain.BeneficiaryAsserts.*;
import static com.jacdl.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacdl.IntegrationTest;
import com.jacdl.domain.Beneficiary;
import com.jacdl.domain.User;
import com.jacdl.repository.BeneficiaryRepository;
import com.jacdl.repository.UserRepository;
import com.jacdl.service.BeneficiaryService;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.service.mapper.BeneficiaryMapper;
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
 * Integration tests for the {@link BeneficiaryResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BeneficiaryResourceIT {

    private static final String DEFAULT_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_ALIAS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/beneficiaries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BeneficiaryRepository beneficiaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BeneficiaryRepository beneficiaryRepositoryMock;

    @Autowired
    private BeneficiaryMapper beneficiaryMapper;

    @Mock
    private BeneficiaryService beneficiaryServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBeneficiaryMockMvc;

    private Beneficiary beneficiary;

    private Beneficiary insertedBeneficiary;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Beneficiary createEntity(EntityManager em) {
        Beneficiary beneficiary = new Beneficiary()
            .alias(DEFAULT_ALIAS)
            .phone(DEFAULT_PHONE)
            .city(DEFAULT_CITY)
            .address(DEFAULT_ADDRESS)
            .referenceAddress(DEFAULT_REFERENCE_ADDRESS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        beneficiary.setUser(user);
        return beneficiary;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Beneficiary createUpdatedEntity(EntityManager em) {
        Beneficiary beneficiary = new Beneficiary()
            .alias(UPDATED_ALIAS)
            .phone(UPDATED_PHONE)
            .city(UPDATED_CITY)
            .address(UPDATED_ADDRESS)
            .referenceAddress(UPDATED_REFERENCE_ADDRESS);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        beneficiary.setUser(user);
        return beneficiary;
    }

    @BeforeEach
    public void initTest() {
        beneficiary = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBeneficiary != null) {
            beneficiaryRepository.delete(insertedBeneficiary);
            insertedBeneficiary = null;
        }
    }

    @Test
    @Transactional
    void createBeneficiary() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);
        var returnedBeneficiaryDTO = om.readValue(
            restBeneficiaryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BeneficiaryDTO.class
        );

        // Validate the Beneficiary in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBeneficiary = beneficiaryMapper.toEntity(returnedBeneficiaryDTO);
        assertBeneficiaryUpdatableFieldsEquals(returnedBeneficiary, getPersistedBeneficiary(returnedBeneficiary));

        insertedBeneficiary = returnedBeneficiary;
    }

    @Test
    @Transactional
    void createBeneficiaryWithExistingId() throws Exception {
        // Create the Beneficiary with an existing ID
        beneficiary.setId(1L);
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBeneficiaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAliasIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        beneficiary.setAlias(null);

        // Create the Beneficiary, which fails.
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        restBeneficiaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        beneficiary.setPhone(null);

        // Create the Beneficiary, which fails.
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        restBeneficiaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        beneficiary.setCity(null);

        // Create the Beneficiary, which fails.
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        restBeneficiaryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBeneficiaries() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(beneficiary.getId().intValue())))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].referenceAddress").value(hasItem(DEFAULT_REFERENCE_ADDRESS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBeneficiariesWithEagerRelationshipsIsEnabled() throws Exception {
        when(beneficiaryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBeneficiaryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(beneficiaryServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBeneficiariesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(beneficiaryServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBeneficiaryMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(beneficiaryRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBeneficiary() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get the beneficiary
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL_ID, beneficiary.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(beneficiary.getId().intValue()))
            .andExpect(jsonPath("$.alias").value(DEFAULT_ALIAS))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.referenceAddress").value(DEFAULT_REFERENCE_ADDRESS));
    }

    @Test
    @Transactional
    void getBeneficiariesByIdFiltering() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        Long id = beneficiary.getId();

        defaultBeneficiaryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBeneficiaryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBeneficiaryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAliasIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where alias equals to
        defaultBeneficiaryFiltering("alias.equals=" + DEFAULT_ALIAS, "alias.equals=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAliasIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where alias in
        defaultBeneficiaryFiltering("alias.in=" + DEFAULT_ALIAS + "," + UPDATED_ALIAS, "alias.in=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAliasIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where alias is not null
        defaultBeneficiaryFiltering("alias.specified=true", "alias.specified=false");
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAliasContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where alias contains
        defaultBeneficiaryFiltering("alias.contains=" + DEFAULT_ALIAS, "alias.contains=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAliasNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where alias does not contain
        defaultBeneficiaryFiltering("alias.doesNotContain=" + UPDATED_ALIAS, "alias.doesNotContain=" + DEFAULT_ALIAS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByPhoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where phone equals to
        defaultBeneficiaryFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByPhoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where phone in
        defaultBeneficiaryFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByPhoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where phone is not null
        defaultBeneficiaryFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    @Transactional
    void getAllBeneficiariesByPhoneContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where phone contains
        defaultBeneficiaryFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByPhoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where phone does not contain
        defaultBeneficiaryFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where city equals to
        defaultBeneficiaryFiltering("city.equals=" + DEFAULT_CITY, "city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where city in
        defaultBeneficiaryFiltering("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY, "city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where city is not null
        defaultBeneficiaryFiltering("city.specified=true", "city.specified=false");
    }

    @Test
    @Transactional
    void getAllBeneficiariesByCityContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where city contains
        defaultBeneficiaryFiltering("city.contains=" + DEFAULT_CITY, "city.contains=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByCityNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where city does not contain
        defaultBeneficiaryFiltering("city.doesNotContain=" + UPDATED_CITY, "city.doesNotContain=" + DEFAULT_CITY);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where address equals to
        defaultBeneficiaryFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where address in
        defaultBeneficiaryFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where address is not null
        defaultBeneficiaryFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where address contains
        defaultBeneficiaryFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where address does not contain
        defaultBeneficiaryFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void getAllBeneficiariesByReferenceAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where referenceAddress equals to
        defaultBeneficiaryFiltering(
            "referenceAddress.equals=" + DEFAULT_REFERENCE_ADDRESS,
            "referenceAddress.equals=" + UPDATED_REFERENCE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllBeneficiariesByReferenceAddressIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where referenceAddress in
        defaultBeneficiaryFiltering(
            "referenceAddress.in=" + DEFAULT_REFERENCE_ADDRESS + "," + UPDATED_REFERENCE_ADDRESS,
            "referenceAddress.in=" + UPDATED_REFERENCE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllBeneficiariesByReferenceAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where referenceAddress is not null
        defaultBeneficiaryFiltering("referenceAddress.specified=true", "referenceAddress.specified=false");
    }

    @Test
    @Transactional
    void getAllBeneficiariesByReferenceAddressContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where referenceAddress contains
        defaultBeneficiaryFiltering(
            "referenceAddress.contains=" + DEFAULT_REFERENCE_ADDRESS,
            "referenceAddress.contains=" + UPDATED_REFERENCE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllBeneficiariesByReferenceAddressNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        // Get all the beneficiaryList where referenceAddress does not contain
        defaultBeneficiaryFiltering(
            "referenceAddress.doesNotContain=" + UPDATED_REFERENCE_ADDRESS,
            "referenceAddress.doesNotContain=" + DEFAULT_REFERENCE_ADDRESS
        );
    }

    @Test
    @Transactional
    void getAllBeneficiariesByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            beneficiaryRepository.saveAndFlush(beneficiary);
            user = UserResourceIT.createEntity(em);
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        beneficiary.setUser(user);
        beneficiaryRepository.saveAndFlush(beneficiary);
        Long userId = user.getId();
        // Get all the beneficiaryList where user equals to userId
        defaultBeneficiaryShouldBeFound("userId.equals=" + userId);

        // Get all the beneficiaryList where user equals to (userId + 1)
        defaultBeneficiaryShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultBeneficiaryFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBeneficiaryShouldBeFound(shouldBeFound);
        defaultBeneficiaryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBeneficiaryShouldBeFound(String filter) throws Exception {
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(beneficiary.getId().intValue())))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].referenceAddress").value(hasItem(DEFAULT_REFERENCE_ADDRESS)));

        // Check, that the count call also returns 1
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBeneficiaryShouldNotBeFound(String filter) throws Exception {
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBeneficiaryMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBeneficiary() throws Exception {
        // Get the beneficiary
        restBeneficiaryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBeneficiary() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beneficiary
        Beneficiary updatedBeneficiary = beneficiaryRepository.findById(beneficiary.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBeneficiary are not directly saved in db
        em.detach(updatedBeneficiary);
        updatedBeneficiary
            .alias(UPDATED_ALIAS)
            .phone(UPDATED_PHONE)
            .city(UPDATED_CITY)
            .address(UPDATED_ADDRESS)
            .referenceAddress(UPDATED_REFERENCE_ADDRESS);
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(updatedBeneficiary);

        restBeneficiaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, beneficiaryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beneficiaryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBeneficiaryToMatchAllProperties(updatedBeneficiary);
    }

    @Test
    @Transactional
    void putNonExistingBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, beneficiaryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beneficiaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(beneficiaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBeneficiaryWithPatch() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beneficiary using partial update
        Beneficiary partialUpdatedBeneficiary = new Beneficiary();
        partialUpdatedBeneficiary.setId(beneficiary.getId());

        partialUpdatedBeneficiary.alias(UPDATED_ALIAS).city(UPDATED_CITY);

        restBeneficiaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBeneficiary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBeneficiary))
            )
            .andExpect(status().isOk());

        // Validate the Beneficiary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBeneficiaryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBeneficiary, beneficiary),
            getPersistedBeneficiary(beneficiary)
        );
    }

    @Test
    @Transactional
    void fullUpdateBeneficiaryWithPatch() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the beneficiary using partial update
        Beneficiary partialUpdatedBeneficiary = new Beneficiary();
        partialUpdatedBeneficiary.setId(beneficiary.getId());

        partialUpdatedBeneficiary
            .alias(UPDATED_ALIAS)
            .phone(UPDATED_PHONE)
            .city(UPDATED_CITY)
            .address(UPDATED_ADDRESS)
            .referenceAddress(UPDATED_REFERENCE_ADDRESS);

        restBeneficiaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBeneficiary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBeneficiary))
            )
            .andExpect(status().isOk());

        // Validate the Beneficiary in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBeneficiaryUpdatableFieldsEquals(partialUpdatedBeneficiary, getPersistedBeneficiary(partialUpdatedBeneficiary));
    }

    @Test
    @Transactional
    void patchNonExistingBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, beneficiaryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(beneficiaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(beneficiaryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBeneficiary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        beneficiary.setId(longCount.incrementAndGet());

        // Create the Beneficiary
        BeneficiaryDTO beneficiaryDTO = beneficiaryMapper.toDto(beneficiary);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBeneficiaryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(beneficiaryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Beneficiary in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBeneficiary() throws Exception {
        // Initialize the database
        insertedBeneficiary = beneficiaryRepository.saveAndFlush(beneficiary);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the beneficiary
        restBeneficiaryMockMvc
            .perform(delete(ENTITY_API_URL_ID, beneficiary.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return beneficiaryRepository.count();
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

    protected Beneficiary getPersistedBeneficiary(Beneficiary beneficiary) {
        return beneficiaryRepository.findById(beneficiary.getId()).orElseThrow();
    }

    protected void assertPersistedBeneficiaryToMatchAllProperties(Beneficiary expectedBeneficiary) {
        assertBeneficiaryAllPropertiesEquals(expectedBeneficiary, getPersistedBeneficiary(expectedBeneficiary));
    }

    protected void assertPersistedBeneficiaryToMatchUpdatableProperties(Beneficiary expectedBeneficiary) {
        assertBeneficiaryAllUpdatablePropertiesEquals(expectedBeneficiary, getPersistedBeneficiary(expectedBeneficiary));
    }
}
