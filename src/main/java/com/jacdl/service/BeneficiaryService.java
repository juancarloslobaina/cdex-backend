package com.jacdl.service;

import com.jacdl.service.dto.BeneficiaryDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.jacdl.domain.Beneficiary}.
 */
public interface BeneficiaryService {
    /**
     * Save a beneficiary.
     *
     * @param beneficiaryDTO the entity to save.
     * @return the persisted entity.
     */
    BeneficiaryDTO save(BeneficiaryDTO beneficiaryDTO);

    /**
     * Updates a beneficiary.
     *
     * @param beneficiaryDTO the entity to update.
     * @return the persisted entity.
     */
    BeneficiaryDTO update(BeneficiaryDTO beneficiaryDTO);

    /**
     * Partially updates a beneficiary.
     *
     * @param beneficiaryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BeneficiaryDTO> partialUpdate(BeneficiaryDTO beneficiaryDTO);

    /**
     * Get all the beneficiaries with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BeneficiaryDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" beneficiary.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BeneficiaryDTO> findOne(Long id);

    /**
     * Delete the "id" beneficiary.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
