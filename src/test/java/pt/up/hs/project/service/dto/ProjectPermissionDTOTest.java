package pt.up.hs.project.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPermissionDTOTest {

    @Test
    public void dtoEqualsVerifier() {
        ProjectPermissionDTO projectPermissionDTO1 = new ProjectPermissionDTO();
        projectPermissionDTO1.setUser("A");
        projectPermissionDTO1.setProjectId(1L);
        projectPermissionDTO1.setPermissionName("READ");
        ProjectPermissionDTO projectPermissionDTO2 = new ProjectPermissionDTO();
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setUser(projectPermissionDTO1.getUser());
        projectPermissionDTO2.setProjectId(projectPermissionDTO1.getProjectId());
        projectPermissionDTO2.setPermissionName(projectPermissionDTO1.getPermissionName());
        assertThat(projectPermissionDTO1).isEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setProjectId(2L);
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setUser("B");
        projectPermissionDTO2.setProjectId(projectPermissionDTO1.getProjectId());
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setUser(projectPermissionDTO1.getUser());
        projectPermissionDTO2.setProjectId(projectPermissionDTO1.getProjectId());
        projectPermissionDTO2.setPermissionName("WRITE");
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO1.setUser(null);
        projectPermissionDTO1.setProjectId(null);
        projectPermissionDTO1.setPermissionName(null);
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
    }
}
