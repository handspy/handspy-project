package pt.up.hs.project.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PermissionTest {

    @Test
    public void equalsVerifier() {
        Permission permission1 = new Permission();
        permission1.setName("A");
        Permission permission2 = new Permission();
        permission2.setName(permission1.getName());
        assertThat(permission1).isEqualTo(permission2);
        permission2.setName("B");
        assertThat(permission1).isNotEqualTo(permission2);
        permission1.setName(null);
        assertThat(permission1).isNotEqualTo(permission2);
    }
}
