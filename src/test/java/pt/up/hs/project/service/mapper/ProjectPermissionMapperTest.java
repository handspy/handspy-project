package pt.up.hs.project.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        Long projectId = 1L;
        String permission = "READ";
        ProjectPermission projectPermission = projectPermissionMapper
            .fromId(new ProjectPermissionId(userLogin, projectId, permission));

        assertThat(projectPermission.getUser()).isEqualTo(userLogin);
        assertThat(projectPermission.getProjectId()).isEqualTo(projectId);
        assertThat(projectPermission.getPermissionName()).isEqualTo(permission);

        assertThat(projectPermissionMapper.fromId(null)).isNull();
    }
}
