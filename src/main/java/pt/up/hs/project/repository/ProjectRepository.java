package pt.up.hs.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.enumeration.ProjectStatus;
import pt.up.hs.project.security.PermissionsConstants;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Spring Data  repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
        value = "select distinct project from Project project inner join ProjectPermission permission " +
            "on project.id = permission.projectId " +
            "where permission.user = ?#{principal?.username} and permission.permissionName = '" + PermissionsConstants.READ + "'",
        countQuery = "select count(distinct project) from Project project inner join ProjectPermission permission " +
            "on project.id = permission.projectId " +
            "where permission.user = ?#{principal?.username} and permission.permissionName = '" + PermissionsConstants.READ + "'"
    )
    @Nonnull Page<Project> findAll(@Nonnull Pageable pageable);

    @Query(
        value = "select distinct project from Project project inner join ProjectPermission permission " +
            "on project.id = permission.projectId where " +
            "permission.user = ?#{principal?.username} and permission.permissionName = '" + PermissionsConstants.READ + "' " +
            "and (coalesce(:statuses) is null or project.status in (:statuses)) " +
            "and (:search is null or project.name like '%' || :search || '%' or project.description like '%' || :search || '%')",
        countQuery = "select count(distinct project) from Project project inner join ProjectPermission permission " +
            "on project.id = permission.projectId where " +
            "permission.user = ?#{principal?.username} and permission.permissionName = '" + PermissionsConstants.READ + "' " +
            "and (coalesce(:statuses) is null or project.status in (:statuses)) " +
            "and (:search is null or project.name like '%' || :search || '%' or project.description like '%' || :search || '%')"
    )
    @Nonnull Page<Project> findAllByStatusAndSearch(
        @Param("statuses") List<ProjectStatus> statuses,
        @Param("search") String search,
        Pageable pageable
    );

    @Query(
        value = "select count(distinct project) from Project project inner join ProjectPermission permission " +
            "on project.id = permission.projectId where " +
            "permission.user = ?#{principal?.username} and permission.permissionName = '" + PermissionsConstants.READ + "' " +
            "and (coalesce(:statuses) is null or project.status in (:statuses)) " +
            "and (:search is null or project.name like '%' || :search || '%' or project.description like '%' || :search || '%')"
    )
    @Nonnull Long countByStatusAndSearch(
        @Param("statuses") List<ProjectStatus> statuses,
        @Param("search") String search
    );
}
