package pt.up.hs.project.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPermissionMapperTest {

    private ProjectPermissionMapper projectPermissionMapper;

    @BeforeEach
    public void setUp() {
        projectPermissionMapper = new ProjectPermissionMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(projectPermissionMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(projectPermissionMapper.fromId(null)).isNull();
    }
}
