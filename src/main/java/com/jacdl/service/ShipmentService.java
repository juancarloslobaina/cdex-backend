package com.jacdl.service;

import com.jacdl.service.dto.ShipmentDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.jacdl.domain.Shipment}.
 */
public interface ShipmentService {
    /**
     * Save a shipment.
     *
     * @param shipmentDTO the entity to save.
     * @return the persisted entity.
     */
    ShipmentDTO save(ShipmentDTO shipmentDTO);

    /**
     * Updates a shipment.
     *
     * @param shipmentDTO the entity to update.
     * @return the persisted entity.
     */
    ShipmentDTO update(ShipmentDTO shipmentDTO);

    /**
     * Partially updates a shipment.
     *
     * @param shipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ShipmentDTO> partialUpdate(ShipmentDTO shipmentDTO);

    /**
     * Get the "id" shipment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ShipmentDTO> findOne(Long id);

    /**
     * Delete the "id" shipment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
