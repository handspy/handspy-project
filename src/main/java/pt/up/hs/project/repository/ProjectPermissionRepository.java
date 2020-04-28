package pt.up.hs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.domain.ProjectPermissionId;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Spring Data repository for the ProjectPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectPermissionRepository extends JpaRepository<ProjectPermission, ProjectPermissionId> {

    List<ProjectPermission> findAllByIdUserAndIdProjectId(
        @NotNull String user,
        @NotNull Long projectId
    );

    List<ProjectPermission> findAllByIdUser(@NotNull String user);

    List<ProjectPermission> findAllByIdProjectId(@NotNull Long projectId);

    void deleteAllByIdUserAndIdProjectId(@NotNull String user, @NotNull Long projectId);

    void deleteAllByIdProjectId(@NotNull Long projectId);

    void deleteAllByIdUserAndIdProjectIdAndIdPermissionNameIn(
        @NotNull String user,
        @NotNull Long projectId,
        @NotNull List<String> permissionName
    );
}
