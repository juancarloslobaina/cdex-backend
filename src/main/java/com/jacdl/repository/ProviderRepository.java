package com.jacdl.repository;

import com.jacdl.domain.Provider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Provider entity.
 *
 * When extending this class, extend ProviderRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ProviderRepository extends ProviderRepositoryWithBagRelationships, JpaRepository<Provider, Long> {
    default Optional<Provider> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Provider> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Provider> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select provider from Provider provider left join fetch provider.user",
        countQuery = "select count(provider) from Provider provider"
    )
    Page<Provider> findAllWithToOneRelationships(Pageable pageable);

    @Query("select provider from Provider provider left join fetch provider.user")
    List<Provider> findAllWithToOneRelationships();

    @Query("select provider from Provider provider left join fetch provider.user where provider.id =:id")
    Optional<Provider> findOneWithToOneRelationships(@Param("id") Long id);
}
