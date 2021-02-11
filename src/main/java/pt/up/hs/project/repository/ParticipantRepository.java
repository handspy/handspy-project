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
 * Spring Data repository for the Participant entity.
 */
@Repository
public interface ParticipantRepository extends CustomRepository<Participant, Long> {

    String SELECT_BY_PROJECT_ID_SEARCH_LABELS =
        "select distinct participant.id from Participant participant left join participant.labels label " +
        "where participant.projectId = :projectId and ((:search is null or :search = '')" +
            " or (lower(participant.name) like ('%' || lower(:search) || '%'))" +
            " or (lower(participant.additionalInfo) like ('%' || lower(:search) || '%')))" +
            " and (coalesce(:labelIds) is null or label.id in (:labelIds))";

    String COUNT_BY_PROJECT_ID_SEARCH_LABELS =
        "select count(distinct participant.id) from Participant participant left join participant.labels label " +
            "where participant.projectId = :projectId and ((:search is null or :search = '')" +
            " or (lower(participant.name) like ('%' || lower(:search) || '%'))" +
            " or (lower(participant.additionalInfo) like ('%' || lower(:search) || '%')))" +
            " and (coalesce(:labelIds) is null or label.id in (:labelIds))";

    @Query(
        value = "select distinct participant from Participant participant join fetch participant.labels label " +
            "where participant.id in (" + SELECT_BY_PROJECT_ID_SEARCH_LABELS + ")",
        countQuery = COUNT_BY_PROJECT_ID_SEARCH_LABELS
    )
    Page<Participant> findAllWithEagerRelationships(
        @Param("projectId") @NotNull Long projectId,
        @Param("search") String search,
        @Param("labelIds") List<Long> labels,
        Pageable pageable
    );

    @Query(
        value = "select distinct participant from Participant participant left join participant.labels label " +
            "where participant.projectId = :projectId " +
            "and ((:search is null or :search = '') or (lower(participant.name) like ('%' || lower(:search) || '%')) or (lower(participant.additionalInfo) like ('%' || lower(:search) || '%'))) " +
            "and (coalesce(:labelIds) is null or label.id in (:labelIds))",
        countProjection = "distinct participant.id"
    )
    Page<Participant> findAllByProjectId(
        @Param("projectId") @NotNull Long projectId,
        @Param("search") String search,
        @Param("labelIds") List<Long> labels,
        Pageable pageable
    );

    @Query(COUNT_BY_PROJECT_ID_SEARCH_LABELS)
    long count(
        @Param("projectId") @NotNull Long projectId,
        @Param("search") String search,
        @Param("labelIds") List<Long> labels
    );

    List<Participant> findAllByProjectId(@NotNull Long projectId);

    @Query("select participant from Participant participant left join fetch participant.labels where participant.projectId = :projectId and participant.id = :id")
    Optional<Participant> findOneWithEagerRelationships(
        @Param("projectId") @NotNull Long projectId,
        @Param("id") @NotNull Long id
    );

    Optional<Participant> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    @Nonnull <S extends Participant> List<S> saveAll(@Nonnull Iterable<S> entities);

    void deleteAllByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
