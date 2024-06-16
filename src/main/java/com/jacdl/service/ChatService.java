package com.jacdl.service;

import com.jacdl.service.dto.ChatDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.jacdl.domain.Chat}.
 */
public interface ChatService {
    /**
     * Save a chat.
     *
     * @param chatDTO the entity to save.
     * @return the persisted entity.
     */
    ChatDTO save(ChatDTO chatDTO);

    /**
     * Updates a chat.
     *
     * @param chatDTO the entity to update.
     * @return the persisted entity.
     */
    ChatDTO update(ChatDTO chatDTO);

    /**
     * Partially updates a chat.
     *
     * @param chatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ChatDTO> partialUpdate(ChatDTO chatDTO);

    /**
     * Get all the chats with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ChatDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" chat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChatDTO> findOne(Long id);

    /**
     * Delete the "id" chat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
