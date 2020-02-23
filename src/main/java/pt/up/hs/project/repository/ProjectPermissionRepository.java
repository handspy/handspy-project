package pt.up.hs.project.repository;

import pt.up.hs.project.domain.ProjectPermission;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the ProjectPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectPermissionRepository extends JpaRepository<ProjectPermission, Long>, JpaSpecificationExecutor<ProjectPermission> {

}
