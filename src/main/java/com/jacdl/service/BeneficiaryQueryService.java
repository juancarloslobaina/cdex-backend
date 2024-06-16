package com.jacdl.service;

import com.jacdl.domain.*; // for static metamodels
import com.jacdl.domain.Beneficiary;
import com.jacdl.repository.BeneficiaryRepository;
import com.jacdl.service.criteria.BeneficiaryCriteria;
import com.jacdl.service.dto.BeneficiaryDTO;
import com.jacdl.service.mapper.BeneficiaryMapper;
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
 * Service for executing complex queries for {@link Beneficiary} entities in the database.
 * The main input is a {@link BeneficiaryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BeneficiaryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BeneficiaryQueryService extends QueryService<Beneficiary> {

    private final Logger log = LoggerFactory.getLogger(BeneficiaryQueryService.class);

    private final BeneficiaryRepository beneficiaryRepository;

    private final BeneficiaryMapper beneficiaryMapper;

    public BeneficiaryQueryService(BeneficiaryRepository beneficiaryRepository, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    /**
     * Return a {@link Page} of {@link BeneficiaryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BeneficiaryDTO> findByCriteria(BeneficiaryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Beneficiary> specification = createSpecification(criteria);
        return beneficiaryRepository.findAll(specification, page).map(beneficiaryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BeneficiaryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Beneficiary> specification = createSpecification(criteria);
        return beneficiaryRepository.count(specification);
    }

    /**
     * Function to convert {@link BeneficiaryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Beneficiary> createSpecification(BeneficiaryCriteria criteria) {
        Specification<Beneficiary> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Beneficiary_.id));
            }
            if (criteria.getAlias() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAlias(), Beneficiary_.alias));
            }
            if (criteria.getPhone() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhone(), Beneficiary_.phone));
            }
            if (criteria.getCity() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCity(), Beneficiary_.city));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Beneficiary_.address));
            }
            if (criteria.getReferenceAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReferenceAddress(), Beneficiary_.referenceAddress));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getUserId(), root -> root.join(Beneficiary_.user, JoinType.LEFT).get(User_.id))
                );
            }
        }
        return specification;
    }
}
