package pt.up.hs.project.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPermissionTest {

    private static final Project DEFAULT_PROJECT = new Project();
    static { DEFAULT_PROJECT.setId(1L); }
    private static final Project OTHER_PROJECT = new Project();
    static { OTHER_PROJECT.setId(2L); }

    private static final Permission DEFAULT_PERMISSION = new Permission();
    static { DEFAULT_PERMISSION.setName("READ"); }
    private static final Permission OTHER_PERMISSION = new Permission();
    static { DEFAULT_PERMISSION.setName("WRITE"); }

    @Test
    public void equalsVerifier() {
        ProjectPermission projectPermission1 = new ProjectPermission();
        projectPermission1.setUser("A");
        projectPermission1.setProject(DEFAULT_PROJECT);
        projectPermission1.setPermission(DEFAULT_PERMISSION);
        ProjectPermission projectPermission2 = new ProjectPermission();
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser(projectPermission1.getUser());
        projectPermission2.setProject(projectPermission1.getProject());
        projectPermission2.setPermission(projectPermission1.getPermission());
        assertThat(projectPermission1).isEqualTo(projectPermission2);
        projectPermission2.setProject(OTHER_PROJECT);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser("B");
        projectPermission2.setProject(projectPermission1.getProject());
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission2.setUser(projectPermission1.getUser());
        projectPermission2.setProject(projectPermission1.getProject());
        projectPermission2.setPermission(OTHER_PERMISSION);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission1.setUser(null);
        projectPermission1.setProject(null);
        projectPermission1.setPermission(null);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
    }
}
