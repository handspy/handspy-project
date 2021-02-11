package pt.up.hs.project.cloner;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import pt.up.hs.project.service.ProjectService;
import pt.up.hs.project.service.dto.ProjectDTO;

import javax.annotation.Nonnull;

import java.util.Optional;

import static pt.up.hs.project.cloner.ClonerConstants.PROJECT_ID_PARAMETER;

@Component
@StepScope
public class ProjectRemovalTasklet implements Tasklet, InitializingBean {

    private ProjectService projectService;

    @Value("#{jobParameters[" + PROJECT_ID_PARAMETER + "]}")
    Long projectId;

    public ProjectService getProjectService() {
        return projectService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public RepeatStatus execute(
        @Nonnull StepContribution contribution,
        @Nonnull ChunkContext chunkContext
    ) {

        Optional<ProjectDTO> projectDTO = projectService.delete(projectId);
        Assert.isTrue(projectDTO.isPresent(), "Project not found");

        return RepeatStatus.FINISHED;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(projectService.findOne(projectId), "Project not found");
    }
}
