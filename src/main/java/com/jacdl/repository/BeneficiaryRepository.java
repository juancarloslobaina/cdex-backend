package com.jacdl.repository;

import com.jacdl.domain.Beneficiary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Beneficiary entity.
 */
@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long>, JpaSpecificationExecutor<Beneficiary> {
    @Query("select beneficiary from Beneficiary beneficiary where beneficiary.user.login = ?#{authentication.name}")
    List<Beneficiary> findByUserIsCurrentUser();

    default Optional<Beneficiary> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Beneficiary> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Beneficiary> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select beneficiary from Beneficiary beneficiary left join fetch beneficiary.user",
        countQuery = "select count(beneficiary) from Beneficiary beneficiary"
    )
    Page<Beneficiary> findAllWithToOneRelationships(Pageable pageable);

    @Query("select beneficiary from Beneficiary beneficiary left join fetch beneficiary.user")
    List<Beneficiary> findAllWithToOneRelationships();

    @Query("select beneficiary from Beneficiary beneficiary left join fetch beneficiary.user where beneficiary.id =:id")
    Optional<Beneficiary> findOneWithToOneRelationships(@Param("id") Long id);
}
