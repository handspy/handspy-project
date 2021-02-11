package pt.up.hs.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.service.dto.TaskDTO;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Task entity.
 */
@Repository
public interface TaskRepository extends CustomRepository<Task, Long> {

    String SELECT_BY_PROJECT_ID_SEARCH_LABELS =
        "select distinct task.id from Task task left join task.labels label " +
        "where task.projectId = :projectId " +
        "and ((:search is null or :search = '') or (lower(task.name) like " +
            "('%' || lower(:search) || '%')) or (lower(task.description) like " +
            "('%' || lower(:search) || '%'))) " +
        "and (coalesce(:labelIds) is null or label.id in (:labelIds))";

    String COUNT_BY_PROJECT_ID_SEARCH_LABELS =
        "select count(distinct task.id) from Task task left join task.labels label " +
            "where task.projectId = :projectId " +
            "and ((:search is null or :search = '') or (lower(task.name) like " +
            "('%' || lower(:search) || '%')) or (lower(task.description) like " +
            "('%' || lower(:search) || '%'))) " +
            "and (coalesce(:labelIds) is null or label.id in (:labelIds))";

    @Query(
        value = "select distinct task from Task task join fetch task.labels label " +
            "where task.id in (" + SELECT_BY_PROJECT_ID_SEARCH_LABELS + ")",
        countQuery = COUNT_BY_PROJECT_ID_SEARCH_LABELS
    )
    Page<Task> findAllWithEagerRelationships(
        @Param("projectId") @NotNull Long projectId,
        @Param("search") String search,
        @Param("labelIds") List<Long> labels,
        Pageable pageable
    );

    @Query(
        value = "select distinct task from Task task left join task.labels label " +
            "where task.projectId = :projectId " +
            "and ((:search is null or :search = '') or (lower(task.name) like ('%' || lower(:search) || '%')) or (lower(task.description) like ('%' || lower(:search) || '%'))) " +
            "and (coalesce(:labelIds) is null or label.id in (:labelIds))",
        countProjection = "distinct task.id"
    )
    Page<Task> findAllByProjectId(
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

    List<Task> findAllByProjectId(@NotNull Long projectId);

    @Query("select task from Task task left join fetch task.labels where task.projectId = :projectId and task.id = :id")
    Optional<Task> findOneWithEagerRelationships(@Param("projectId") @NotNull Long projectId, @Param("id") Long id);

    Optional<Task> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    @Nonnull <S extends Task> List<S> saveAll(@Nonnull Iterable<S> entities);

    Optional<Task> deleteByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
