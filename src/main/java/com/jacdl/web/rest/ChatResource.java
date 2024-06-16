package com.jacdl.web.rest;

import com.jacdl.repository.ChatRepository;
import com.jacdl.service.ChatQueryService;
import com.jacdl.service.ChatService;
import com.jacdl.service.criteria.ChatCriteria;
import com.jacdl.service.dto.ChatDTO;
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
 * REST controller for managing {@link com.jacdl.domain.Chat}.
 */
@RestController
@RequestMapping("/api/chats")
public class ChatResource {

    private final Logger log = LoggerFactory.getLogger(ChatResource.class);

    private static final String ENTITY_NAME = "chat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatService chatService;

    private final ChatRepository chatRepository;

    private final ChatQueryService chatQueryService;

    public ChatResource(ChatService chatService, ChatRepository chatRepository, ChatQueryService chatQueryService) {
        this.chatService = chatService;
        this.chatRepository = chatRepository;
        this.chatQueryService = chatQueryService;
    }

    /**
     * {@code POST  /chats} : Create a new chat.
     *
     * @param chatDTO the chatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new chatDTO, or with status {@code 400 (Bad Request)} if the chat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ChatDTO> createChat(@Valid @RequestBody ChatDTO chatDTO) throws URISyntaxException {
        log.debug("REST request to save Chat : {}", chatDTO);
        if (chatDTO.getId() != null) {
            throw new BadRequestAlertException("A new chat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        chatDTO = chatService.save(chatDTO);
        return ResponseEntity.created(new URI("/api/chats/" + chatDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, chatDTO.getId().toString()))
            .body(chatDTO);
    }

    /**
     * {@code PUT  /chats/:id} : Updates an existing chat.
     *
     * @param id the id of the chatDTO to save.
     * @param chatDTO the chatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatDTO,
     * or with status {@code 400 (Bad Request)} if the chatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChatDTO> updateChat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ChatDTO chatDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Chat : {}, {}", id, chatDTO);
        if (chatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        chatDTO = chatService.update(chatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatDTO.getId().toString()))
            .body(chatDTO);
    }

    /**
     * {@code PATCH  /chats/:id} : Partial updates given fields of an existing chat, field will ignore if it is null
     *
     * @param id the id of the chatDTO to save.
     * @param chatDTO the chatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatDTO,
     * or with status {@code 400 (Bad Request)} if the chatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the chatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the chatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ChatDTO> partialUpdateChat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ChatDTO chatDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Chat partially : {}, {}", id, chatDTO);
        if (chatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, chatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!chatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ChatDTO> result = chatService.partialUpdate(chatDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /chats} : get all the chats.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of chats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ChatDTO>> getAllChats(
        ChatCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Chats by criteria: {}", criteria);

        Page<ChatDTO> page = chatQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chats/count} : count all the chats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countChats(ChatCriteria criteria) {
        log.debug("REST request to count Chats by criteria: {}", criteria);
        return ResponseEntity.ok().body(chatQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /chats/:id} : get the "id" chat.
     *
     * @param id the id of the chatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChatDTO> getChat(@PathVariable("id") Long id) {
        log.debug("REST request to get Chat : {}", id);
        Optional<ChatDTO> chatDTO = chatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(chatDTO);
    }

    /**
     * {@code DELETE  /chats/:id} : delete the "id" chat.
     *
     * @param id the id of the chatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) {
        log.debug("REST request to delete Chat : {}", id);
        chatService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
