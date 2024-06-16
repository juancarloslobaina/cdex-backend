package com.jacdl.service;

import com.jacdl.domain.*; // for static metamodels
import com.jacdl.domain.Shipment;
import com.jacdl.repository.ShipmentRepository;
import com.jacdl.service.criteria.ShipmentCriteria;
import com.jacdl.service.dto.ShipmentDTO;
import com.jacdl.service.mapper.ShipmentMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Shipment} entities in the database.
 * The main input is a {@link ShipmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ShipmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ShipmentQueryService extends QueryService<Shipment> {

    private final Logger log = LoggerFactory.getLogger(ShipmentQueryService.class);

    private final ShipmentRepository shipmentRepository;

    private final ShipmentMapper shipmentMapper;

    public ShipmentQueryService(ShipmentRepository shipmentRepository, ShipmentMapper shipmentMapper) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentMapper = shipmentMapper;
    }

    /**
     * Return a {@link Page} of {@link ShipmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ShipmentDTO> findByCriteria(ShipmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.findAll(specification, page).map(shipmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ShipmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Shipment> specification = createSpecification(criteria);
        return shipmentRepository.count(specification);
    }

    /**
     * Function to convert {@link ShipmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Shipment> createSpecification(ShipmentCriteria criteria) {
        Specification<Shipment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Shipment_.id));
            }
            if (criteria.getReference() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReference(), Shipment_.reference));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Shipment_.amount));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Shipment_.createdAt));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Shipment_.status));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), Shipment_.type));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), Shipment_.active));
            }
            if (criteria.getClientId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getClientId(), root -> root.join(Shipment_.client, JoinType.LEFT).get(Client_.id))
                );
            }
            if (criteria.getProviderId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getProviderId(), root -> root.join(Shipment_.provider, JoinType.LEFT).get(Provider_.id))
                );
            }
            if (criteria.getDeliveryId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getDeliveryId(), root -> root.join(Shipment_.delivery, JoinType.LEFT).get(Delivery_.id))
                );
            }
            if (criteria.getBeneficiaryId() != null) {
                specification = specification.and(
                    buildSpecification(
                        criteria.getBeneficiaryId(),
                        root -> root.join(Shipment_.beneficiary, JoinType.LEFT).get(Beneficiary_.id)
                    )
                );
            }
        }
        return specification;
    }
}
