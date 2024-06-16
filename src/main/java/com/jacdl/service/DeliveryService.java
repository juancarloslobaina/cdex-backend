package com.jacdl.service;

import com.jacdl.service.dto.DeliveryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.jacdl.domain.Delivery}.
 */
public interface DeliveryService {
    /**
     * Save a delivery.
     *
     * @param deliveryDTO the entity to save.
     * @return the persisted entity.
     */
    DeliveryDTO save(DeliveryDTO deliveryDTO);

    /**
     * Updates a delivery.
     *
     * @param deliveryDTO the entity to update.
     * @return the persisted entity.
     */
    DeliveryDTO update(DeliveryDTO deliveryDTO);

    /**
     * Partially updates a delivery.
     *
     * @param deliveryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DeliveryDTO> partialUpdate(DeliveryDTO deliveryDTO);

    /**
     * Get all the deliveries.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DeliveryDTO> findAll(Pageable pageable);

    /**
     * Get all the deliveries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DeliveryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" delivery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DeliveryDTO> findOne(Long id);

    /**
     * Delete the "id" delivery.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
