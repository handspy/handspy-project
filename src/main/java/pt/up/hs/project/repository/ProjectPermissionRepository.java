package pt.up.hs.project.repository;

import pt.up.hs.project.domain.ProjectPermission;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Spring Data  repository for the ProjectPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectPermissionRepository extends JpaRepository<ProjectPermission, Long> {

    List<ProjectPermission> findAllByUserAndProjectId(@NotNull String user, Long projectId);

    List<ProjectPermission> findAllByUser(@NotNull String user);

    List<ProjectPermission> findAllByProjectId(Long project_id);

    void deleteAllByUserAndProjectId(@NotNull String user, Long projectId);

    void deleteAllByProjectId(Long projectId);

    void deleteAllByUserAndProjectIdAndPermissionNameIn(
        @NotNull String user, Long project_id, @NotNull List<String> permissionName);
}
