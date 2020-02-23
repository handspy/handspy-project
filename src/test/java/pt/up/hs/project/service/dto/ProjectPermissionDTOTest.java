package pt.up.hs.project.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import pt.up.hs.project.web.rest.TestUtil;

public class ProjectPermissionDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectPermissionDTO.class);
        ProjectPermissionDTO projectPermissionDTO1 = new ProjectPermissionDTO();
        projectPermissionDTO1.setId(1L);
        ProjectPermissionDTO projectPermissionDTO2 = new ProjectPermissionDTO();
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setId(projectPermissionDTO1.getId());
        assertThat(projectPermissionDTO1).isEqualTo(projectPermissionDTO2);
        projectPermissionDTO2.setId(2L);
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
        projectPermissionDTO1.setId(null);
        assertThat(projectPermissionDTO1).isNotEqualTo(projectPermissionDTO2);
    }
}
