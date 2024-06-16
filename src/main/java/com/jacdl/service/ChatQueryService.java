package com.jacdl.service;

import com.jacdl.domain.*; // for static metamodels
import com.jacdl.domain.Chat;
import com.jacdl.repository.ChatRepository;
import com.jacdl.service.criteria.ChatCriteria;
import com.jacdl.service.dto.ChatDTO;
import com.jacdl.service.mapper.ChatMapper;
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
 * Service for executing complex queries for {@link Chat} entities in the database.
 * The main input is a {@link ChatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ChatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ChatQueryService extends QueryService<Chat> {

    private final Logger log = LoggerFactory.getLogger(ChatQueryService.class);

    private final ChatRepository chatRepository;

    private final ChatMapper chatMapper;

    public ChatQueryService(ChatRepository chatRepository, ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
    }

    /**
     * Return a {@link Page} of {@link ChatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ChatDTO> findByCriteria(ChatCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Chat> specification = createSpecification(criteria);
        return chatRepository.findAll(specification, page).map(chatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ChatCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Chat> specification = createSpecification(criteria);
        return chatRepository.count(specification);
    }

    /**
     * Function to convert {@link ChatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Chat> createSpecification(ChatCriteria criteria) {
        Specification<Chat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Chat_.id));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Chat_.message));
            }
            if (criteria.getCreatedAt() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedAt(), Chat_.createdAt));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Chat_.status));
            }
            if (criteria.getFromId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getFromId(), root -> root.join(Chat_.from, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getToId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getToId(), root -> root.join(Chat_.to, JoinType.LEFT).get(User_.id))
                );
            }
        }
        return specification;
    }
}
