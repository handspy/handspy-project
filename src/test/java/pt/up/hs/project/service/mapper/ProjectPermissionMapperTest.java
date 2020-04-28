package pt.up.hs.project.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.up.hs.project.domain.Permission;
import pt.up.hs.project.domain.Project;
import pt.up.hs.project.domain.ProjectPermission;
import pt.up.hs.project.domain.ProjectPermissionId;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPermissionMapperTest {

    private ProjectPermissionMapper projectPermissionMapper;

    @BeforeEach
    public void setUp() {
        projectPermissionMapper = new ProjectPermissionMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        String userLogin = "A";
        Project project = new Project();
        project.setId(1L);
        Permission permission = new Permission();
        permission.setName("READ");
        ProjectPermission projectPermission = projectPermissionMapper
            .fromId(new ProjectPermissionId(userLogin, project, permission));

        assertThat(projectPermission.getUser()).isEqualTo(userLogin);
        assertThat(projectPermission.getProject()).isEqualTo(project);
        assertThat(projectPermission.getPermission()).isEqualTo(permission);

        assertThat(projectPermissionMapper.fromId(null)).isNull();
    }
}
