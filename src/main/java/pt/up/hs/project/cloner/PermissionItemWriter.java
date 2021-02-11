package pt.up.hs.project.cloner;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;

import java.util.List;

import static pt.up.hs.project.cloner.ClonerConstants.NEW_PROJECT_ID_PARAMETER;

@Component
@StepScope
public class PermissionItemWriter implements ItemWriter<BulkProjectPermissionDTO> {

    private final ProjectPermissionService projectPermissionService;

    private final Long newProjectId;

    public PermissionItemWriter(
        final ProjectPermissionService projectPermissionService,
        @Value("#{jobParameters[" + NEW_PROJECT_ID_PARAMETER + "]}") final Long newProjectId
    ) {
        this.projectPermissionService = projectPermissionService;
        this.newProjectId = newProjectId;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void write(List<? extends BulkProjectPermissionDTO> items) throws Exception {
        for (BulkProjectPermissionDTO permissionDTO: items) {
            permissionDTO.setProjectId(newProjectId);
            projectPermissionService.create(
                newProjectId, permissionDTO.getUser(), permissionDTO);
        }
    }
}
