package pt.up.hs.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.repository.ProjectPermissionRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class ProjectPermissionEvaluator implements PermissionEvaluator {

    private final ProjectPermissionRepository projectPermissionRepository;

    @Autowired
    public ProjectPermissionEvaluator(
        ProjectPermissionRepository projectPermissionRepository
    ) {
        this.projectPermissionRepository = projectPermissionRepository;
    }

    @Override
    public boolean hasPermission(
        Authentication auth, Object targetDomainObject, Object permission
    ) {
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }

        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (!userLogin.isPresent()) {
            return false;
        }

        if (targetDomainObject instanceof Project) { // permission check is only available for projects
            List<ProjectPermission> projectPermissions = projectPermissionRepository
                .findAllByUserAndProjectId(
                    userLogin.get(),
                    ((Project) targetDomainObject).getId()
                );

            return projectPermissions.parallelStream()
                .anyMatch(projectPermission ->
                    projectPermission.getPermissionName()
                        .equals(permission.toString())
                );
        }

        return false;
    }

    @Override
    public boolean hasPermission(
        Authentication auth, Serializable targetId, String targetType, Object permission) {
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }

        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (!userLogin.isPresent()) {
            return false;
        }

        if (targetType.equals(Project.class.getCanonicalName())) {
            // permission check is only available for projects
            List<ProjectPermission> projectPermissions = projectPermissionRepository
                .findAllByUserAndProjectId(
                    userLogin.get(),
                    (Long) targetId
                );

            return projectPermissions.parallelStream()
                .anyMatch(projectPermission ->
                    projectPermission.getPermissionName()
                        .equals(permission.toString())
                );
        }

        return false;
    }
}
