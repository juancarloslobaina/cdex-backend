package com.jacdl.web.rest;

import com.jacdl.repository.BeneficiaryRepository;
import com.jacdl.service.BeneficiaryQueryService;
import com.jacdl.service.BeneficiaryService;
import com.jacdl.service.criteria.BeneficiaryCriteria;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.jacdl.domain.Beneficiary}.
 */
@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryResource {

    private final Logger log = LoggerFactory.getLogger(BeneficiaryResource.class);

    private static final String ENTITY_NAME = "beneficiary";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BeneficiaryService beneficiaryService;

    private final BeneficiaryRepository beneficiaryRepository;

    private final BeneficiaryQueryService beneficiaryQueryService;

    public BeneficiaryResource(
        BeneficiaryService beneficiaryService,
        BeneficiaryRepository beneficiaryRepository,
        BeneficiaryQueryService beneficiaryQueryService
    ) {
        this.beneficiaryService = beneficiaryService;
        this.beneficiaryRepository = beneficiaryRepository;
        this.beneficiaryQueryService = beneficiaryQueryService;
    }

    /**
     * {@code POST  /beneficiaries} : Create a new beneficiary.
     *
     * @param beneficiaryDTO the beneficiaryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new beneficiaryDTO, or with status {@code 400 (Bad Request)} if the beneficiary has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BeneficiaryDTO> createBeneficiary(@Valid @RequestBody BeneficiaryDTO beneficiaryDTO) throws URISyntaxException {
        log.debug("REST request to save Beneficiary : {}", beneficiaryDTO);
        if (beneficiaryDTO.getId() != null) {
            throw new BadRequestAlertException("A new beneficiary cannot already have an ID", ENTITY_NAME, "idexists");
        }
        beneficiaryDTO = beneficiaryService.save(beneficiaryDTO);
        return ResponseEntity.created(new URI("/api/beneficiaries/" + beneficiaryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, beneficiaryDTO.getId().toString()))
            .body(beneficiaryDTO);
    }

    /**
     * {@code PUT  /beneficiaries/:id} : Updates an existing beneficiary.
     *
     * @param id the id of the beneficiaryDTO to save.
     * @param beneficiaryDTO the beneficiaryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated beneficiaryDTO,
     * or with status {@code 400 (Bad Request)} if the beneficiaryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the beneficiaryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> updateBeneficiary(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BeneficiaryDTO beneficiaryDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Beneficiary : {}, {}", id, beneficiaryDTO);
        if (beneficiaryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, beneficiaryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!beneficiaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        beneficiaryDTO = beneficiaryService.update(beneficiaryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, beneficiaryDTO.getId().toString()))
            .body(beneficiaryDTO);
    }

    /**
     * {@code PATCH  /beneficiaries/:id} : Partial updates given fields of an existing beneficiary, field will ignore if it is null
     *
     * @param id the id of the beneficiaryDTO to save.
     * @param beneficiaryDTO the beneficiaryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated beneficiaryDTO,
     * or with status {@code 400 (Bad Request)} if the beneficiaryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the beneficiaryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the beneficiaryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BeneficiaryDTO> partialUpdateBeneficiary(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BeneficiaryDTO beneficiaryDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Beneficiary partially : {}, {}", id, beneficiaryDTO);
        if (beneficiaryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, beneficiaryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!beneficiaryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BeneficiaryDTO> result = beneficiaryService.partialUpdate(beneficiaryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, beneficiaryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /beneficiaries} : get all the beneficiaries.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of beneficiaries in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BeneficiaryDTO>> getAllBeneficiaries(
        BeneficiaryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Beneficiaries by criteria: {}", criteria);

        Page<BeneficiaryDTO> page = beneficiaryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /beneficiaries/count} : count all the beneficiaries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBeneficiaries(BeneficiaryCriteria criteria) {
        log.debug("REST request to count Beneficiaries by criteria: {}", criteria);
        return ResponseEntity.ok().body(beneficiaryQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /beneficiaries/:id} : get the "id" beneficiary.
     *
     * @param id the id of the beneficiaryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the beneficiaryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeneficiaryDTO> getBeneficiary(@PathVariable("id") Long id) {
        log.debug("REST request to get Beneficiary : {}", id);
        Optional<BeneficiaryDTO> beneficiaryDTO = beneficiaryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(beneficiaryDTO);
    }

    /**
     * {@code DELETE  /beneficiaries/:id} : delete the "id" beneficiary.
     *
     * @param id the id of the beneficiaryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable("id") Long id) {
        log.debug("REST request to delete Beneficiary : {}", id);
        beneficiaryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
