package pt.up.hs.project.repository;

import pt.up.hs.project.domain.Label;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Label entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByProjectIdAndName(@NotNull Long projectId, @NotNull @Size(max = 50) String name);

    List<Label> findAllByProjectId(@NotNull Long projectId);

    long countByProjectId(@NotNull Long projectId);

    Optional<Label> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    void deleteAllByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
