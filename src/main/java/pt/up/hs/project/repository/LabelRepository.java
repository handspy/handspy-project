package pt.up.hs.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.up.hs.project.domain.Label;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

/**
 * Spring Data  repository for the Label entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LabelRepository extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {

    Optional<Label> findByProjectIdAndName(@NotNull Long projectId, @NotNull @Size(max = 50) String name);

    Page<Label> findAllByProjectId(@NotNull Long projectId, Pageable pageable);

    Optional<Label> findByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);

    void deleteAllByProjectIdAndId(@NotNull Long projectId, @NotNull Long id);
}
