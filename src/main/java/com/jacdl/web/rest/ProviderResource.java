package com.jacdl.web.rest;

import com.jacdl.repository.ProviderRepository;
import com.jacdl.service.ProviderService;
import com.jacdl.service.dto.ProviderDTO;
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
 * REST controller for managing {@link com.jacdl.domain.Provider}.
 */
@RestController
@RequestMapping("/api/providers")
public class ProviderResource {

    private final Logger log = LoggerFactory.getLogger(ProviderResource.class);

    private static final String ENTITY_NAME = "provider";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProviderService providerService;

    private final ProviderRepository providerRepository;

    public ProviderResource(ProviderService providerService, ProviderRepository providerRepository) {
        this.providerService = providerService;
        this.providerRepository = providerRepository;
    }

    /**
     * {@code POST  /providers} : Create a new provider.
     *
     * @param providerDTO the providerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new providerDTO, or with status {@code 400 (Bad Request)} if the provider has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProviderDTO> createProvider(@Valid @RequestBody ProviderDTO providerDTO) throws URISyntaxException {
        log.debug("REST request to save Provider : {}", providerDTO);
        if (providerDTO.getId() != null) {
            throw new BadRequestAlertException("A new provider cannot already have an ID", ENTITY_NAME, "idexists");
        }
        providerDTO = providerService.save(providerDTO);
        return ResponseEntity.created(new URI("/api/providers/" + providerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, providerDTO.getId().toString()))
            .body(providerDTO);
    }

    /**
     * {@code PUT  /providers/:id} : Updates an existing provider.
     *
     * @param id the id of the providerDTO to save.
     * @param providerDTO the providerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated providerDTO,
     * or with status {@code 400 (Bad Request)} if the providerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the providerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProviderDTO> updateProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProviderDTO providerDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Provider : {}, {}", id, providerDTO);
        if (providerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, providerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!providerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        providerDTO = providerService.update(providerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, providerDTO.getId().toString()))
            .body(providerDTO);
    }

    /**
     * {@code PATCH  /providers/:id} : Partial updates given fields of an existing provider, field will ignore if it is null
     *
     * @param id the id of the providerDTO to save.
     * @param providerDTO the providerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated providerDTO,
     * or with status {@code 400 (Bad Request)} if the providerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the providerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the providerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProviderDTO> partialUpdateProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProviderDTO providerDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Provider partially : {}, {}", id, providerDTO);
        if (providerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, providerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!providerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProviderDTO> result = providerService.partialUpdate(providerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, providerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /providers} : get all the providers.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of providers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProviderDTO>> getAllProviders(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        log.debug("REST request to get a page of Providers");
        Page<ProviderDTO> page;
        if (eagerload) {
            page = providerService.findAllWithEagerRelationships(pageable);
        } else {
            page = providerService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /providers/:id} : get the "id" provider.
     *
     * @param id the id of the providerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the providerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProviderDTO> getProvider(@PathVariable("id") Long id) {
        log.debug("REST request to get Provider : {}", id);
        Optional<ProviderDTO> providerDTO = providerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(providerDTO);
    }

    /**
     * {@code DELETE  /providers/:id} : delete the "id" provider.
     *
     * @param id the id of the providerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable("id") Long id) {
        log.debug("REST request to delete Provider : {}", id);
        providerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
