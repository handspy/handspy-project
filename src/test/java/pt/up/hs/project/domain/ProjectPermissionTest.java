package pt.up.hs.project.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPermissionTest {

    private static final Long DEFAULT_PROJECT = 1L;
    private static final Long OTHER_PROJECT = 2L;

    private static final String DEFAULT_PERMISSION = "READ";
    private static final String OTHER_PERMISSION = "WRITE";

    @Test
    public void equalsVerifier() {
        ProjectPermission projectPermission1 = new ProjectPermission();
        projectPermission1.setUser("A");
        projectPermission1.setProjectId(DEFAULT_PROJECT);
        projectPermission1.setPermissionName(DEFAULT_PERMISSION);
        ProjectPermission projectPermission2 = new ProjectPermission();
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser(projectPermission1.getUser());
        projectPermission2.setProjectId(projectPermission1.getProjectId());
        projectPermission2.setPermissionName(projectPermission1.getPermissionName());
        assertThat(projectPermission1).isEqualTo(projectPermission2);
        projectPermission2.setProjectId(OTHER_PROJECT);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser("B");
        projectPermission2.setProjectId(projectPermission1.getProjectId());
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser(projectPermission1.getUser());
        projectPermission2.setProjectId(projectPermission1.getProjectId());
        projectPermission2.setPermissionName(OTHER_PERMISSION);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission1.setUser(null);
        projectPermission1.setProjectId(null);
        projectPermission1.setPermissionName(null);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
    }
}
