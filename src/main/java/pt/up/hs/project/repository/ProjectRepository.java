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
 * Spring Data repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query(
        value = "select distinct project from Project project join project.permissions permission " +
            "where permission.id.user = ?#{principal} and permission.id.permission.name = '" + PermissionsConstants.READ + "'",
        countProjection = "distinct project.id"
    )
    @Nonnull List<Project> findAll();

    @Query(
        value = "select distinct project from Project project join project.permissions permission " +
            "where permission.id.user = ?#{principal} and permission.id.permission.name = '" + PermissionsConstants.READ + "' " +
            "and (coalesce(:statuses) is null or project.status in (:statuses)) " +
            "and ((:search is null or :search = '') or (lower(project.name) like ('%' || lower(:search) || '%')) or (lower(project.description) like ('%' || lower(:search) || '%')))",
        countProjection = "distinct project.id"
    )
    @Nonnull List<Project> findAllByStatusAndSearch(
        @Param("statuses") @Nonnull List<ProjectStatus> statuses,
        @Param("search") @Nonnull String search
    );

    @Query(
        value = "select count(distinct project) from Project project join project.permissions permission " +
            "where permission.id.user = ?#{principal} and permission.id.permission.name = '" + PermissionsConstants.READ + "'"
    )
    long count();
}
