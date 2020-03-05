package pt.up.hs.project.repository;

import pt.up.hs.project.domain.Participant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Participant entity.
 */
@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long>, JpaSpecificationExecutor<Participant> {

    @Query(
        value = "select distinct participant from Participant participant left join fetch participant.labels where participant.projectId = :projectId",
        countQuery = "select count(distinct participant) from Participant participant"
    )
    Page<Participant> findAllByProjectIdWithEagerRelationships(@Param("projectId") @NotNull Long projectId, Pageable pageable);

    @Query("select distinct participant from Participant participant left join fetch participant.labels where participant.projectId = :projectId")
    List<Participant> findAllByProjectIdWithEagerRelationships(@Param("projectId") @NotNull Long projectId);

    @Query("select participant from Participant participant left join fetch participant.labels where participant.projectId = :projectId and participant.id = :id")
    Optional<Participant> findOneWithEagerRelationships(@Param("projectId") @NotNull Long projectId, @Param("id") @NotNull Long id);

    Page<Participant> findAllByProjectId(@NotNull Long projectId, Pageable pageable);

    Optional<Participant> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    @Nonnull <S extends Participant> List<S> saveAll(@Nonnull Iterable<S> entities);

    void deleteAllByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
