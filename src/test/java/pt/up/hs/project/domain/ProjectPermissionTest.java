package pt.up.hs.project.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import pt.up.hs.project.web.rest.TestUtil;

public class ProjectPermissionTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectPermission.class);
        ProjectPermission projectPermission1 = new ProjectPermission();
        projectPermission1.setId(1L);
        ProjectPermission projectPermission2 = new ProjectPermission();
        projectPermission2.setId(projectPermission1.getId());
        assertThat(projectPermission1).isEqualTo(projectPermission2);
        projectPermission2.setId(2L);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
        projectPermission1.setId(null);
        assertThat(projectPermission1).isNotEqualTo(projectPermission2);
    }
}
