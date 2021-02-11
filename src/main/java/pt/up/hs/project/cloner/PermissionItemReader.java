package pt.up.hs.project.cloner;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.up.hs.project.service.ProjectPermissionService;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;
import pt.up.hs.project.service.dto.ProjectDTO;

import java.util.Iterator;
import java.util.List;

import static pt.up.hs.project.cloner.ClonerConstants.NEW_PROJECT_ID_PARAMETER;
import static pt.up.hs.project.cloner.ClonerConstants.PROJECT_ID_PARAMETER;

@Component
@StepScope
public class PermissionItemReader implements ItemReader<BulkProjectPermissionDTO> {

    private final ProjectService projectService;
    private final ProjectPermissionService projectPermissionService;
    private final Long projectId;
    private final Long newProjectId;

    private ItemReader<BulkProjectPermissionDTO> delegate = null;

    public PermissionItemReader(
        final ProjectService projectService,
        final ProjectPermissionService projectPermissionService,
        @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}") final Long projectId,
        @Value("#{jobParameters[" + NEW_PROJECT_ID_PARAMETER + "]}") final Long newProjectId
    ) {
        this.projectService = projectService;
        this.projectPermissionService = projectPermissionService;
        this.projectId = projectId;
        this.newProjectId = newProjectId;
    }

    @Override
    public BulkProjectPermissionDTO read() throws Exception {
        if (delegate == null) {
            delegate = new IteratorItemReader<>(permissionsIterator());
        }
        return delegate.read();
    }

    public Iterator<BulkProjectPermissionDTO> permissionsIterator() {
        ProjectDTO projectDTO = projectService.findOne(newProjectId).orElse(null);
        List<BulkProjectPermissionDTO> permissionDTOs = projectPermissionService.findAll(projectId);
        return permissionDTOs
            .parallelStream()
            .filter(permissionDTO -> projectDTO != null && !permissionDTO.getUser().equals(projectDTO.getOwner()))
            .iterator();
    }
}
