package pt.up.hs.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.up.hs.project.domain.Task;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Task entity.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(
        value = "select distinct task from Task task left join fetch task.labels where task.projectId = :projectId",
        countQuery = "select count(distinct task) from Task task where task.projectId = :projectId"
    )
    Page<Task> findAllWithEagerRelationships(@Param("projectId") @NotNull Long projectId, Pageable pageable);

    @Query("select distinct task from Task task left join fetch task.labels where task.projectId = :projectId")
    List<Task> findAllWithEagerRelationships(@Param("projectId") @NotNull Long projectId);

    @Query("select task from Task task left join fetch task.labels where task.projectId = :projectId and task.id = :id")
    Optional<Task> findOneWithEagerRelationships(@Param("projectId") @NotNull Long projectId, @Param("id") Long id);

    Page<Task> findAllByProjectId(@NotNull Long projectId, Pageable pageable);

    Optional<Task> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    @Nonnull <S extends Task> List<S> saveAll(@Nonnull Iterable<S> entities);

    void deleteAllByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
