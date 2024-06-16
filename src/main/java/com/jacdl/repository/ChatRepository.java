package com.jacdl.repository;

import com.jacdl.domain.Chat;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Chat entity.
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>, JpaSpecificationExecutor<Chat> {
    @Query("select chat from Chat chat where chat.from.login = ?#{authentication.name}")
    List<Chat> findByFromIsCurrentUser();

    @Query("select chat from Chat chat where chat.to.login = ?#{authentication.name}")
    List<Chat> findByToIsCurrentUser();

    default Optional<Chat> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Chat> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Chat> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select chat from Chat chat left join fetch chat.from left join fetch chat.to",
        countQuery = "select count(chat) from Chat chat"
    )
    Page<Chat> findAllWithToOneRelationships(Pageable pageable);

    @Query("select chat from Chat chat left join fetch chat.from left join fetch chat.to")
    List<Chat> findAllWithToOneRelationships();

    @Query("select chat from Chat chat left join fetch chat.from left join fetch chat.to where chat.id =:id")
    Optional<Chat> findOneWithToOneRelationships(@Param("id") Long id);
}
